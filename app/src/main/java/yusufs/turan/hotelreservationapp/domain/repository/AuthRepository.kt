package yusufs.turan.hotelreservationapp.domain.repository

import yusufs.turan.hotelreservationapp.domain.model.AppUser
import yusufs.turan.hotelreservationapp.domain.model.UserRole

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AppUser>
    suspend fun register(email: String, password: String, role: UserRole): Result<AppUser>
    suspend fun getCurrentUserRole(): UserRole
    suspend fun logout()
}