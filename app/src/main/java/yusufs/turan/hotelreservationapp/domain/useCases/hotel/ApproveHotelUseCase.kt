package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class ApproveHotelUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotelId: String): Result<Unit> {
        return repository.approveHotel(hotelId)
    }
}