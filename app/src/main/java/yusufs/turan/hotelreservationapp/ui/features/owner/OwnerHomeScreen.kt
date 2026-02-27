package yusufs.turan.hotelreservationapp.ui.features.owner

import HotelItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus
import yusufs.turan.hotelreservationapp.ui.features.auth.AuthViewModel
import yusufs.turan.hotelreservationapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: OwnerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservationActionStatus by viewModel.reservationActionStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadMyHotels()
    }

    LaunchedEffect(reservationActionStatus) {
        reservationActionStatus?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearReservationActionStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Benim Otellerim") },
                actions = {
                    TextButton(onClick = { authViewModel.logout() }) {
                        Text("Cikis")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddHotel.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Otel Ekle")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when (val state = uiState) {
            is OwnerUiState.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is OwnerUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.myHotels.isNotEmpty()) {
                        item {
                            Text("Otellerim", style = MaterialTheme.typography.titleLarge)
                        }
                        items(state.myHotels) { hotel ->
                            HotelItem(hotel = hotel)
                        }
                    } else {
                        item {
                            Text("Henuz otel eklemediniz.")
                        }
                    }

                    item {
                        Text("Gelen Rezervasyonlar", style = MaterialTheme.typography.titleLarge)
                    }

                    if (state.reservations.isEmpty()) {
                        item {
                            Text("Henuz rezervasyon yok.")
                        }
                    } else {
                        items(state.reservations) { reservation ->
                            OwnerReservationItem(
                                reservation = reservation,
                                onApproveClick = {
                                    viewModel.approveReservation(reservation.id)
                                }
                            )
                        }
                    }
                }
            }

            is OwnerUiState.Error -> {
                Box(Modifier.fillMaxSize()) {
                    Text(state.message, Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun OwnerReservationItem(
    reservation: Reservation,
    onApproveClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = reservation.hotelName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Musteri: ${reservation.userEmail}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Giris: ${formatDate(reservation.checkInTimestamp)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Cikis: ${formatDate(reservation.checkOutTimestamp)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Toplam: ${reservation.totalPrice} TL",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Durum: ${reservation.status.toDisplayText()}",
                    color = reservation.status.toDisplayColor(),
                    style = MaterialTheme.typography.labelLarge
                )

                if (reservation.status == ReservationStatus.PENDING) {
                    Button(onClick = onApproveClick) {
                        Text("Onayla")
                    }
                }
            }
        }
    }
}

private fun ReservationStatus.toDisplayText(): String {
    return when (this) {
        ReservationStatus.PENDING -> "Bekliyor"
        ReservationStatus.APPROVED -> "Onaylandi"
        ReservationStatus.CANCELED -> "Iptal"
    }
}

private fun ReservationStatus.toDisplayColor(): Color {
    return when (this) {
        ReservationStatus.PENDING -> Color(0xFFEF6C00)
        ReservationStatus.APPROVED -> Color(0xFF2E7D32)
        ReservationStatus.CANCELED -> Color(0xFFB71C1C)
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0L) return "-"
    return SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).format(Date(timestamp))
}
