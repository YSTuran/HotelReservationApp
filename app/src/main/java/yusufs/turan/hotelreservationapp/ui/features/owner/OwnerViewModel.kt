package yusufs.turan.hotelreservationapp.ui.features.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.AddHotelUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetMyHotelsUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.reservation.ApproveReservationUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.reservation.GetOwnerReservationsUseCase
import javax.inject.Inject

sealed class OwnerUiState {
    object Loading : OwnerUiState()
    data class Success(
        val myHotels: List<Hotel>,
        val reservations: List<Reservation>
    ) : OwnerUiState()

    data class Error(val message: String) : OwnerUiState()
}

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val addHotelUseCase: AddHotelUseCase,
    private val getMyHotelsUseCase: GetMyHotelsUseCase,
    private val getOwnerReservationsUseCase: GetOwnerReservationsUseCase,
    private val approveReservationUseCase: ApproveReservationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<OwnerUiState>(OwnerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _addHotelStatus = MutableStateFlow<String?>(null)
    val addHotelStatus = _addHotelStatus.asStateFlow()

    private val _reservationActionStatus = MutableStateFlow<String?>(null)
    val reservationActionStatus = _reservationActionStatus.asStateFlow()

    init {
        loadMyHotels()
    }

    fun loadMyHotels() {
        viewModelScope.launch {
            _uiState.value = OwnerUiState.Loading
            try {
                val hotels = getMyHotelsUseCase()
                val reservations = getOwnerReservationsUseCase()
                _uiState.value = OwnerUiState.Success(
                    myHotels = hotels,
                    reservations = reservations
                )
            } catch (e: Exception) {
                _uiState.value = OwnerUiState.Error(e.message ?: "Otelleriniz yuklenemedi")
            }
        }
    }

    fun addHotel(hotel: Hotel) {
        viewModelScope.launch {
            _addHotelStatus.value = "Yukleniyor..."
            val result = addHotelUseCase(hotel)

            result.onSuccess {
                _addHotelStatus.value = "Basarili! Otel onaya gonderildi."
                loadMyHotels()
            }.onFailure { error ->
                _addHotelStatus.value = "Hata: ${error.message}"
            }
        }
    }

    fun approveReservation(reservationId: String) {
        viewModelScope.launch {
            val result = approveReservationUseCase(reservationId)
            result.onSuccess {
                _reservationActionStatus.value = "Rezervasyon onaylandi"
                loadMyHotels()
            }.onFailure { error ->
                _reservationActionStatus.value = error.message ?: "Rezervasyon onaylanamadi"
            }
        }
    }

    fun clearReservationActionStatus() {
        _reservationActionStatus.value = null
    }

    fun resetAddStatus() {
        _addHotelStatus.value = null
    }
}
