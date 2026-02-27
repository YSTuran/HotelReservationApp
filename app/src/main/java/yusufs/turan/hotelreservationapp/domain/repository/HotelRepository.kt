package yusufs.turan.hotelreservationapp.domain.repository

import yusufs.turan.hotelreservationapp.domain.model.Hotel

interface HotelRepository {
    suspend fun getHotels(): List<Hotel>
    suspend fun addHotel(hotel: Hotel): Result<Unit>
    suspend fun approveHotel(hotelId: String): Result<Unit>
}