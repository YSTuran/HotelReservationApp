package yusufs.turan.hotelreservationapp.domain.useCases.reservation

import com.google.firebase.auth.FirebaseAuth
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(
    private val repository: HotelRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(hotel: Hotel): Result<Unit> {
        val currentUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Rezervasyon icin once giris yapmalisiniz."))

        if (hotel.id.isBlank()) {
            return Result.failure(Exception("Gecerli bir otel secilemedi."))
        }

        if (!hotel.isApproved) {
            return Result.failure(Exception("Sadece onayli oteller icin rezervasyon yapabilirsiniz."))
        }

        val now = System.currentTimeMillis()
        val reservation = Reservation(
            hotelId = hotel.id,
            hotelName = hotel.name,
            hotelOwnerId = hotel.ownerId,
            userId = currentUser.uid,
            userEmail = currentUser.email.orEmpty(),
            checkInTimestamp = now,
            checkOutTimestamp = now + ONE_NIGHT_IN_MILLIS,
            totalPrice = hotel.pricePerNight,
            createdAt = now
        )

        return repository.createReservation(reservation)
    }

    private companion object {
        const val ONE_NIGHT_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}
