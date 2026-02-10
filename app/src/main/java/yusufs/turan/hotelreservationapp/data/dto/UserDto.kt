package yusufs.turan.hotelreservationapp.data.dto

import yusufs.turan.hotelreservationapp.domain.model.AppUser
import yusufs.turan.hotelreservationapp.domain.model.UserRole

data class UserDto(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "USER"
) {
    fun toAppUser(): AppUser {
        return AppUser(
            uid = uid,
            email = email,
            name = name,
            role = try {
                UserRole.valueOf(role)
            } catch (e: Exception) {
                UserRole.USER
            }
        )
    }
}

fun AppUser.toDto(): UserDto {
    return UserDto(
        uid = uid,
        email = email,
        name = name,
        role = role.name
    )
}