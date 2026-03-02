package yusufs.turan.hotelreservationapp.ui.features.client

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.HotelComment
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.AddHotelCommentUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetHotelByIdUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetHotelCommentsUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.reservation.CreateReservationUseCase
import javax.inject.Inject

data class HotelDetailUiState(
    val isLoading: Boolean = true,
    val hotel: Hotel? = null,
    val comments: List<HotelComment> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHotelByIdUseCase: GetHotelByIdUseCase,
    private val getHotelCommentsUseCase: GetHotelCommentsUseCase,
    private val addHotelCommentUseCase: AddHotelCommentUseCase,
    private val createReservationUseCase: CreateReservationUseCase
) : ViewModel() {

    private val hotelId: String = savedStateHandle["hotelId"] ?: ""

    private val _uiState = MutableStateFlow(HotelDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage = _actionMessage.asStateFlow()

    init {
        loadHotelDetails()
    }

    fun loadHotelDetails() {
        viewModelScope.launch {
            if (hotelId.isBlank()) {
                _uiState.value = HotelDetailUiState(
                    isLoading = false,
                    errorMessage = "Gecerli otel bulunamadi."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val hotel = getHotelByIdUseCase(hotelId)
                if (hotel == null) {
                    _uiState.value = HotelDetailUiState(
                        isLoading = false,
                        errorMessage = "Otel bulunamadi."
                    )
                    return@launch
                }

                val comments = getHotelCommentsUseCase(hotelId)
                _uiState.value = HotelDetailUiState(
                    isLoading = false,
                    hotel = hotel,
                    comments = comments,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = HotelDetailUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Detaylar yuklenemedi."
                )
            }
        }
    }

    fun addComment(commentText: String) {
        viewModelScope.launch {
            val result = addHotelCommentUseCase(hotelId, commentText)
            result.onSuccess {
                _actionMessage.value = "Yorum eklendi."
                loadHotelDetails()
            }.onFailure { error ->
                _actionMessage.value = error.message ?: "Yorum eklenemedi."
            }
        }
    }

    fun reserveHotel(checkInTimestamp: Long, checkOutTimestamp: Long) {
        viewModelScope.launch {
            val hotel = _uiState.value.hotel
            if (hotel == null) {
                _actionMessage.value = "Otel bilgisi bulunamadi."
                return@launch
            }

            val result = createReservationUseCase(
                hotel = hotel,
                checkInTimestamp = checkInTimestamp,
                checkOutTimestamp = checkOutTimestamp
            )

            result.onSuccess {
                _actionMessage.value = "Rezervasyon olusturuldu."
            }.onFailure { error ->
                _actionMessage.value = error.message ?: "Rezervasyon olusturulamadi."
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
