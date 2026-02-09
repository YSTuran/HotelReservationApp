package yusufs.turan.hotelreservationapp.domain.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.GUEST,
    val name: String = ""
)
