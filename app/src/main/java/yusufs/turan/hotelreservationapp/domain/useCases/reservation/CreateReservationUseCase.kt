package yusufs.turan.hotelreservationapp.domain.useCases.reservation

import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.model.ReservationStatus
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(
    private val repository: HotelRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(
        hotel: Hotel,
        checkInTimestamp: Long,
        checkOutTimestamp: Long
    ): Result<Unit> {
        val currentUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Rezervasyon icin once giris yapmalisiniz."))

        if (hotel.id.isBlank()) {
            return Result.failure(Exception("Gecerli bir otel secilemedi."))
        }

        if (!hotel.isApproved) {
            return Result.failure(Exception("Sadece onayli oteller icin rezervasyon yapabilirsiniz."))
        }

        if (checkInTimestamp <= 0L || checkOutTimestamp <= 0L) {
            return Result.failure(Exception("Lutfen giris ve cikis tarihlerini secin."))
        }

        if (checkOutTimestamp <= checkInTimestamp) {
            return Result.failure(Exception("Cikis tarihi, giris tarihinden sonra olmalidir."))
        }

        val nights = calculateNightCount(checkInTimestamp, checkOutTimestamp)
        if (nights <= 0L) {
            return Result.failure(Exception("En az 1 gecelik rezervasyon yapmalisiniz."))
        }

        val now = System.currentTimeMillis()
        val reservation = Reservation(
            hotelId = hotel.id,
            hotelName = hotel.name,
            hotelOwnerId = hotel.ownerId,
            userId = currentUser.uid,
            userEmail = currentUser.email.orEmpty(),
            checkInTimestamp = checkInTimestamp,
            checkOutTimestamp = checkOutTimestamp,
            totalPrice = hotel.pricePerNight * nights,
            createdAt = now,
            status = ReservationStatus.PENDING
        )

        return repository.createReservation(reservation)
    }

    private fun calculateNightCount(checkInTimestamp: Long, checkOutTimestamp: Long): Long {
        val zoneId = ZoneId.systemDefault()
        val checkInDate = Instant.ofEpochMilli(checkInTimestamp).atZone(zoneId).toLocalDate()
        val checkOutDate = Instant.ofEpochMilli(checkOutTimestamp).atZone(zoneId).toLocalDate()
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate)
    }
}
