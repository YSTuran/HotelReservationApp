package yusufs.turan.hotelreservationapp.domain.model

data class HotelComment(
    val id: String = "",
    val hotelId: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val comment: String = "",
    val createdAt: Long = 0L
)
