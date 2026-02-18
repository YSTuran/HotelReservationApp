package yusufs.turan.hotelreservationapp.ui.features.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetHotelsUseCase
import javax.inject.Inject

sealed class ClientUiState {
    object Loading : ClientUiState()
    data class Success(val hotels: List<Hotel>) : ClientUiState()
    data class Error(val message: String) : ClientUiState()
}

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val getHotelsUseCase: GetHotelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClientUiState>(ClientUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHotels()
    }

    fun loadHotels() {
        viewModelScope.launch {
            _uiState.value = ClientUiState.Loading
            try {
                val hotels = getHotelsUseCase()
                _uiState.value = ClientUiState.Success(hotels)
            } catch (e: Exception) {
                _uiState.value = ClientUiState.Error(e.message ?: "Oteller yüklenemedi")
            }
        }
    }
}