package yusufs.turan.hotelreservationapp.domain.useCases.auth

import yusufs.turan.hotelreservationapp.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
