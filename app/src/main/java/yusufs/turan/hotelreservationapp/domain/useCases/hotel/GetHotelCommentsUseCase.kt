package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import yusufs.turan.hotelreservationapp.domain.model.HotelComment
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class GetHotelCommentsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotelId: String): List<HotelComment> {
        if (hotelId.isBlank()) return emptyList()
        return repository.getHotelComments(hotelId)
    }
}
