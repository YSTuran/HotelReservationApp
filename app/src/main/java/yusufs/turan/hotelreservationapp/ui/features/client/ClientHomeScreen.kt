package yusufs.turan.hotelreservationapp.ui.features.client

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus
import yusufs.turan.hotelreservationapp.ui.features.auth.AuthViewModel
import yusufs.turan.hotelreservationapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ClientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservationStatus by viewModel.reservationStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(reservationStatus) {
        reservationStatus?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearReservationStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oteller") },
                actions = {
                    TextButton(onClick = { authViewModel.logout() }) {
                        Text("Cikis")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is ClientUiState.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }

                is ClientUiState.Success -> {
                    val filteredHotels = state.hotels.filter { hotel ->
                        val q = searchQuery.trim()
                        q.isBlank() ||
                            hotel.name.contains(q, ignoreCase = true) ||
                            hotel.city.contains(q, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Otel veya il ara") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Text("Otel Listesi", style = MaterialTheme.typography.titleLarge)
                        }

                        if (filteredHotels.isEmpty()) {
                            item {
                                Text("Aramaniza uygun otel bulunamadi.")
                            }
                        } else {
                            items(filteredHotels) { hotel ->
                                ClientHotelCard(
                                    hotel = hotel,
                                    onClick = {
                                        navController.navigate(Screen.HotelDetail.createRoute(hotel.id))
                                    }
                                )
                            }
                        }

                        item {
                            Text(
                                text = "Rezervasyonlarim",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (state.reservations.isEmpty()) {
                            item {
                                Text("Henuz rezervasyonunuz yok.")
                            }
                        } else {
                            items(state.reservations) { reservation ->
                                ClientReservationItem(
                                    reservation = reservation,
                                    onCancelClick = { viewModel.cancelReservation(reservation.id) }
                                )
                            }
                        }
                    }
                }

                is ClientUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Hata: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientHotelCard(
    hotel: Hotel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = hotel.name, style = MaterialTheme.typography.titleMedium)
            Text(text = hotel.city, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${hotel.pricePerNight} TL",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ClientReservationItem(
    reservation: Reservation,
    onCancelClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(reservation.hotelName, style = MaterialTheme.typography.titleMedium)
            Text("Giris: ${formatDate(reservation.checkInTimestamp)}")
            Text("Cikis: ${formatDate(reservation.checkOutTimestamp)}")
            Text(
                text = "Toplam: ${reservation.totalPrice} TL",
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

                if (reservation.status != ReservationStatus.CANCELED) {
                    TextButton(onClick = onCancelClick) {
                        Text("Iptal Et")
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
