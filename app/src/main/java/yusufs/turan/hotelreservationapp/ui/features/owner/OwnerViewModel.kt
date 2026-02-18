package yusufs.turan.hotelreservationapp.ui.features.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.AddHotelUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetMyHotelsUseCase
import javax.inject.Inject

sealed class OwnerUiState {
    object Loading : OwnerUiState()
    data class Success(val myHotels: List<Hotel>) : OwnerUiState()
    data class Error(val message: String) : OwnerUiState()
}

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val addHotelUseCase: AddHotelUseCase,
    private val getMyHotelsUseCase: GetMyHotelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<OwnerUiState>(OwnerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _addHotelStatus = MutableStateFlow<String?>(null)
    val addHotelStatus = _addHotelStatus.asStateFlow()

    init {
        loadMyHotels()
    }

    fun loadMyHotels() {
        viewModelScope.launch {
            _uiState.value = OwnerUiState.Loading
            try {
                val hotels = getMyHotelsUseCase()
                _uiState.value = OwnerUiState.Success(hotels)
            } catch (e: Exception) {
                _uiState.value = OwnerUiState.Error(e.message ?: "Otelleriniz yüklenemedi")
            }
        }
    }

    fun addHotel(hotel: Hotel) {
        viewModelScope.launch {
            _addHotelStatus.value = "Yükleniyor..."
            val result = addHotelUseCase(hotel)

            result.onSuccess {
                _addHotelStatus.value = "Başarılı! Otel onaya gönderildi."
            }.onFailure { error ->
                _addHotelStatus.value = "Hata: ${error.message}"
            }
        }
    }

    fun resetAddStatus() {
        _addHotelStatus.value = null
    }
}