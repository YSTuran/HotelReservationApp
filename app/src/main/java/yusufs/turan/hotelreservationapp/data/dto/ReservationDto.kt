package yusufs.turan.hotelreservationapp.data.dto

import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus

data class ReservationDto(
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
    val status: String = ReservationStatus.PENDING.name
) {
    fun toReservation(): Reservation {
        val parsedStatus = runCatching { ReservationStatus.valueOf(status) }
            .getOrElse { ReservationStatus.PENDING }

        return Reservation(
            id = id,
            hotelId = hotelId,
            hotelName = hotelName,
            hotelOwnerId = hotelOwnerId,
            userId = userId,
            userEmail = userEmail,
            checkInTimestamp = checkInTimestamp,
            checkOutTimestamp = checkOutTimestamp,
            totalPrice = totalPrice,
            createdAt = createdAt,
            status = parsedStatus
        )
    }
}

fun Reservation.toDto(): ReservationDto {
    return ReservationDto(
        id = id,
        hotelId = hotelId,
        hotelName = hotelName,
        hotelOwnerId = hotelOwnerId,
        userId = userId,
        userEmail = userEmail,
        checkInTimestamp = checkInTimestamp,
        checkOutTimestamp = checkOutTimestamp,
        totalPrice = totalPrice,
        createdAt = createdAt,
        status = status.name
    )
}
