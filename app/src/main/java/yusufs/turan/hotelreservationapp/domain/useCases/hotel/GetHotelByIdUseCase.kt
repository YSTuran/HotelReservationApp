package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class GetHotelByIdUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotelId: String): Hotel? {
        if (hotelId.isBlank()) return null
        return repository.getHotelById(hotelId)
    }
}
