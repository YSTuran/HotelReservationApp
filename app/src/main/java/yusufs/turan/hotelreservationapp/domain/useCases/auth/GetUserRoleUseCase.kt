package yusufs.turan.hotelreservationapp.domain.useCases.auth

import yusufs.turan.hotelreservationapp.domain.model.UserRole
import yusufs.turan.hotelreservationapp.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserRoleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): UserRole {
        return repository.getCurrentUserRole()
    }
}