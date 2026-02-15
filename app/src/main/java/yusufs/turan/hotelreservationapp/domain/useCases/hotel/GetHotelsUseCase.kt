package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class GetHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(): List<Hotel> {
        val allHotels = repository.getHotels()

        return allHotels.filter { it.isApproved }
    }
}