package yusufs.turan.hotelreservationapp.domain.repository

import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation

interface HotelRepository {
    suspend fun getHotels(): List<Hotel>
    suspend fun addHotel(hotel: Hotel): Result<Unit>
    suspend fun approveHotel(hotelId: String): Result<Unit>
    suspend fun createReservation(reservation: Reservation): Result<Unit>
    suspend fun getOwnerReservations(ownerId: String): List<Reservation>
}
