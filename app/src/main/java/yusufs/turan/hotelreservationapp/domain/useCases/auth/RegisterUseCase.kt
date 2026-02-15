package yusufs.turan.hotelreservationapp.domain.useCases.auth

import yusufs.turan.hotelreservationapp.domain.model.AppUser
import yusufs.turan.hotelreservationapp.domain.model.UserRole
import yusufs.turan.hotelreservationapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, role: UserRole): Result<AppUser> {

        if (email.isBlank()) {
            return Result.failure(Exception("Email adresi geçerli değil."))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Şifre en az 6 karakterden oluşmalıdır."))
        }

        return repository.register(email, password, role)
    }
}