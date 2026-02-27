package yusufs.turan.hotelreservationapp.ui.features.owner

import HotelItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import yusufs.turan.hotelreservationapp.domain.model.Reservation
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

    LaunchedEffect(Unit) {
        viewModel.loadMyHotels()
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
        }
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
                            OwnerReservationItem(reservation = reservation)
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
private fun OwnerReservationItem(reservation: Reservation) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
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
        }
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0L) return "-"
    return SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).format(Date(timestamp))
}
