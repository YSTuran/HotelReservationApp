package yusufs.turan.hotelreservationapp.domain.repository

import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.HotelComment
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus

interface HotelRepository {
    suspend fun getHotels(): List<Hotel>
    suspend fun getHotelById(hotelId: String): Hotel?
    suspend fun addHotel(hotel: Hotel): Result<Unit>
    suspend fun approveHotel(hotelId: String): Result<Unit>
    suspend fun getHotelComments(hotelId: String): List<HotelComment>
    suspend fun addHotelComment(comment: HotelComment): Result<Unit>
    suspend fun createReservation(reservation: Reservation): Result<Unit>
    suspend fun getOwnerReservations(ownerId: String): List<Reservation>
    suspend fun getUserReservations(userId: String): List<Reservation>
    suspend fun updateReservationStatus(
        reservationId: String,
        status: ReservationStatus
    ): Result<Unit>
}
