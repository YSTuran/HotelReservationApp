package yusufs.turan.hotelreservationapp.domain.useCases.reservation

import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class ApproveReservationUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(reservationId: String): Result<Unit> {
        if (reservationId.isBlank()) {
            return Result.failure(Exception("Gecerli rezervasyon bulunamadi."))
        }
        return repository.updateReservationStatus(reservationId, ReservationStatus.APPROVED)
    }
}
