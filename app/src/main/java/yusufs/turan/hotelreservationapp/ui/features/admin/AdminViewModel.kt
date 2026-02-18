package yusufs.turan.hotelreservationapp.ui.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.useCases.hotel.GetAdminHotelsUseCase
import javax.inject.Inject

sealed class AdminUiState {
    object Loading : AdminUiState()
    data class Success(val allHotels: List<Hotel>) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAdminHotelsUseCase: GetAdminHotelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAllHotels()
    }

    fun loadAllHotels() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                val hotels = getAdminHotelsUseCase()
                _uiState.value = AdminUiState.Success(hotels)
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Veri çekilemedi")
            }
        }
    }
}