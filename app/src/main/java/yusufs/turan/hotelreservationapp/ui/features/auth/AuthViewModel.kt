package yusufs.turan.hotelreservationapp.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yusufs.turan.hotelreservationapp.domain.model.UserRole
import yusufs.turan.hotelreservationapp.domain.useCases.auth.GetUserRoleUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.auth.LoginUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.auth.LogoutUseCase
import yusufs.turan.hotelreservationapp.domain.useCases.auth.RegisterUseCase
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: UserRole) : AuthUiState()
    object LoggedOut : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun checkUserRole() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val role = getUserRoleUseCase()
            if (role != UserRole.GUEST) {
                _uiState.value = AuthUiState.Success(role)
            } else {
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUseCase(email, pass)

            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user.role)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Giris basarisiz")
            }
        }
    }

    fun register(email: String, pass: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registerUseCase(email, pass, role)

            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user.role)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Kayit basarisiz")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            logoutUseCase()
            _uiState.value = AuthUiState.LoggedOut
        }
    }
}
