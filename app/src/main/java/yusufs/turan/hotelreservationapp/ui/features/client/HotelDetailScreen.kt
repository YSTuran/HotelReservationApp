package yusufs.turan.hotelreservationapp.ui.features.client

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.HotelComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    navController: NavController,
    viewModel: HotelDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var commentText by remember { mutableStateOf("") }
    var showReservationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Otel Detayi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Beklenmeyen hata",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                val hotel = uiState.hotel ?: return@Scaffold

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        HotelDetailHeader(
                            hotel = hotel,
                            onReserveClick = { showReservationDialog = true }
                        )
                    }

                    item {
                        Text("Yorumlar", style = MaterialTheme.typography.titleLarge)
                    }

                    item {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Yorum yazin") }
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.addComment(commentText)
                                commentText = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Yorum Ekle")
                        }
                    }

                    if (uiState.comments.isEmpty()) {
                        item { Text("Henuz yorum yok.") }
                    } else {
                        items(uiState.comments) { comment ->
                            HotelCommentItem(comment = comment)
                        }
                    }
                }
            }
        }
    }

    if (showReservationDialog) {
        ReservationDateDialog(
            hotel = uiState.hotel,
            onDismiss = { showReservationDialog = false },
            onConfirm = { checkIn, checkOut ->
                viewModel.reserveHotel(checkIn, checkOut)
                showReservationDialog = false
            }
        )
    }
}

@Composable
private fun HotelDetailHeader(
    hotel: Hotel,
    onReserveClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val imageUrl = hotel.imageUrls.firstOrNull()
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = hotel.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Text(hotel.name, style = MaterialTheme.typography.titleLarge)
            Text("Sehir: ${hotel.city}")
            Text("Adres: ${hotel.address}")
            Text("Aciklama: ${hotel.description}")
            Text("Puan: ${hotel.rating}")
            Text(
                text = "Fiyat: ${hotel.pricePerNight} TL / gece",
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = onReserveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rezervasyon Yap")
            }
        }
    }
}

@Composable
private fun HotelCommentItem(comment: HotelComment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(comment.userEmail, style = MaterialTheme.typography.labelLarge)
            Text(comment.comment, style = MaterialTheme.typography.bodyMedium)
            Text(
                formatDate(comment.createdAt),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ReservationDateDialog(
    hotel: Hotel?,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit
) {
    val currentHotel = hotel ?: return
    var checkInTimestamp by remember { mutableStateOf<Long?>(null) }
    var checkOutTimestamp by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    val checkInText = checkInTimestamp?.let { formatDate(it) } ?: "Giris tarihi sec"
    val checkOutText = checkOutTimestamp?.let { formatDate(it) } ?: "Cikis tarihi sec"

    val isValidDateRange = checkInTimestamp != null &&
        checkOutTimestamp != null &&
        checkOutTimestamp!! > checkInTimestamp!!

    val nights = if (isValidDateRange) {
        calculateNightCount(checkInTimestamp!!, checkOutTimestamp!!).toInt()
    } else {
        0
    }

    val totalPrice = if (nights > 0) currentHotel.pricePerNight * nights else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rezervasyon Tarihi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(currentHotel.name, style = MaterialTheme.typography.titleMedium)

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
                ) { Text(checkInText) }

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
                ) { Text(checkOutText) }

                if (nights > 0) {
                    Text("Gece sayisi: $nights")
                    Text(
                        "Toplam: $totalPrice TL",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(checkInTimestamp!!, checkOutTimestamp!!) },
                enabled = isValidDateRange
            ) { Text("Onayla") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Iptal") }
        }
    )
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

private fun startOfToday(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0L) return "-"
    return SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).format(Date(timestamp))
}
