package yusufs.turan.hotelreservationapp.domain.model

data class Reservation(
    val id: String = "",
    val hotelId: String = "",
    val hotelName: String = "",
    val hotelOwnerId: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val checkInTimestamp: Long = 0L,
    val checkOutTimestamp: Long = 0L,
    val totalPrice: Double = 0.0,
    val createdAt: Long = 0L,
    val status: ReservationStatus = ReservationStatus.PENDING
)
