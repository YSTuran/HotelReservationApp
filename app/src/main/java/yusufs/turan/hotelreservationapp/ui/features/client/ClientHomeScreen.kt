package yusufs.turan.hotelreservationapp.ui.features.client

import HotelItem
import android.app.DatePickerDialog
import android.content.Context
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus
import yusufs.turan.hotelreservationapp.ui.features.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    authViewModel: AuthViewModel,
    viewModel: ClientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservationStatus by viewModel.reservationStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedHotelForReservation by remember { mutableStateOf<Hotel?>(null) }

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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("Otel Listesi", style = MaterialTheme.typography.titleLarge)
                        }

                        items(state.hotels) { hotel ->
                            HotelItem(
                                hotel = hotel,
                                actionLabel = "Rezervasyon Yap",
                                onActionClick = {
                                    selectedHotelForReservation = hotel
                                }
                            )
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
                                    onCancelClick = {
                                        viewModel.cancelReservation(reservation.id)
                                    }
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

    selectedHotelForReservation?.let { hotel ->
        ReservationDateDialog(
            hotel = hotel,
            onDismiss = { selectedHotelForReservation = null },
            onConfirm = { checkIn, checkOut ->
                viewModel.reserveHotel(
                    hotel = hotel,
                    checkInTimestamp = checkIn,
                    checkOutTimestamp = checkOut
                )
                selectedHotelForReservation = null
            }
        )
    }
}

@Composable
private fun ClientReservationItem(
    reservation: Reservation,
    onCancelClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

@Composable
private fun ReservationDateDialog(
    hotel: Hotel,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit
) {
    var checkInTimestamp by remember { mutableStateOf<Long?>(null) }
    var checkOutTimestamp by remember { mutableStateOf<Long?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val checkInText = checkInTimestamp?.let { formatDate(it) } ?: "Giris tarihi sec"
    val checkOutText = checkOutTimestamp?.let { formatDate(it) } ?: "Cikis tarihi sec"

    val isValidDateRange = checkInTimestamp != null &&
        checkOutTimestamp != null &&
        checkOutTimestamp!! > checkInTimestamp!!

    val nights = if (isValidDateRange) calculateNightCount(
        checkInTimestamp = checkInTimestamp!!,
        checkOutTimestamp = checkOutTimestamp!!
    ).toInt() else 0

    val totalPrice = if (nights > 0) {
        hotel.pricePerNight * nights
    } else {
        0.0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rezervasyon") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = hotel.name, style = MaterialTheme.typography.titleMedium)

                OutlinedButton(
                    onClick = {
                        openDatePicker(
                            context = context,
                            minDate = startOfToday()
                        ) { selectedDate ->
                            checkInTimestamp = selectedDate
                            if (checkOutTimestamp != null && checkOutTimestamp!! <= selectedDate) {
                                checkOutTimestamp = null
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(checkInText)
                }

                OutlinedButton(
                    onClick = {
                        val minCheckOut = addDays(
                            timestamp = checkInTimestamp ?: startOfToday(),
                            dayCount = 1
                        )
                        openDatePicker(
                            context = context,
                            minDate = minCheckOut
                        ) { selectedDate ->
                            checkOutTimestamp = selectedDate
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(checkOutText)
                }

                if (nights > 0) {
                    Text("Gece sayisi: $nights")
                    Text(
                        text = "Toplam fiyat: $totalPrice TL",
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Lutfen gecerli bir tarih araligi secin.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(checkInTimestamp!!, checkOutTimestamp!!)
                },
                enabled = isValidDateRange
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Iptal")
            }
        }
    )
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

private fun openDatePicker(
    context: Context,
    minDate: Long,
    onDateSelected: (Long) -> Unit
) {
    val initial = Calendar.getInstance()

    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onDateSelected(selectedCalendar.timeInMillis)
        },
        initial.get(Calendar.YEAR),
        initial.get(Calendar.MONTH),
        initial.get(Calendar.DAY_OF_MONTH)
    )

    dialog.datePicker.minDate = minDate
    dialog.show()
}

private fun startOfToday(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).format(Date(timestamp))
}

private fun calculateNightCount(checkInTimestamp: Long, checkOutTimestamp: Long): Long {
    val zoneId = ZoneId.systemDefault()
    val checkInDate = Instant.ofEpochMilli(checkInTimestamp).atZone(zoneId).toLocalDate()
    val checkOutDate = Instant.ofEpochMilli(checkOutTimestamp).atZone(zoneId).toLocalDate()
    return ChronoUnit.DAYS.between(checkInDate, checkOutDate)
}

private fun addDays(timestamp: Long, dayCount: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp
        add(Calendar.DAY_OF_MONTH, dayCount)
    }.timeInMillis
}
