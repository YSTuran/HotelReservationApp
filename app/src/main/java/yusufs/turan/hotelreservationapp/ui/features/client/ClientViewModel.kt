package yusufs.turan.hotelreservationapp.ui.features.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetHotelsUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.reservation.CancelReservationUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.reservation.CreateReservationUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.reservation.GetUserReservationsUseCase
import javax.inject.Inject

sealed class ClientUiState {
    object Loading : ClientUiState()
    data class Success(
        val hotels: List<Hotel>,
        val reservations: List<Reservation>
    ) : ClientUiState()

    data class Error(val message: String) : ClientUiState()
}

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val getUserReservationsUseCase: GetUserReservationsUseCase,
    private val createReservationUseCase: CreateReservationUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClientUiState>(ClientUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _reservationStatus = MutableStateFlow<String?>(null)
    val reservationStatus = _reservationStatus.asStateFlow()

    init {
        loadHotels()
    }

    fun loadHotels() {
        viewModelScope.launch {
            _uiState.value = ClientUiState.Loading
            try {
                val hotels = getHotelsUseCase()
                val reservations = getUserReservationsUseCase()
                _uiState.value = ClientUiState.Success(
                    hotels = hotels,
                    reservations = reservations
                )
            } catch (e: Exception) {
                _uiState.value = ClientUiState.Error(e.message ?: "Veriler yuklenemedi")
            }
        }
    }

    fun reserveHotel(
        hotel: Hotel,
        checkInTimestamp: Long,
        checkOutTimestamp: Long
    ) {
        viewModelScope.launch {
            val result = createReservationUseCase(
                hotel = hotel,
                checkInTimestamp = checkInTimestamp,
                checkOutTimestamp = checkOutTimestamp
            )
            result.onSuccess {
                _reservationStatus.value = "Rezervasyon kaydedildi"
                loadHotels()
            }.onFailure { error ->
                _reservationStatus.value = error.message ?: "Rezervasyon olusturulamadi"
            }
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            val result = cancelReservationUseCase(reservationId)
            result.onSuccess {
                _reservationStatus.value = "Rezervasyon iptal edildi"
                loadHotels()
            }.onFailure { error ->
                _reservationStatus.value = error.message ?: "Rezervasyon iptal edilemedi"
            }
        }
    }

    fun clearReservationStatus() {
        _reservationStatus.value = null
    }
}
