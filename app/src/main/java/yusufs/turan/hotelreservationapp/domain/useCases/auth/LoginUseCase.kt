package yusufs.turan.hotelreservationapp.domain.useCases.auth

import yusufs.turan.hotelreservationapp.domain.model.AppUser
import yusufs.turan.hotelreservationapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser> {

        if (email.isBlank()) {
            return Result.failure(Exception("Email adresi boş olamaz."))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Şifre alanı boş olamaz."))
        }

        return repository.login(email, password)
    }
}