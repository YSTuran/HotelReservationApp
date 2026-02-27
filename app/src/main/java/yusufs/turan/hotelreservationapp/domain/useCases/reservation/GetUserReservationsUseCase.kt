package yusufs.turan.hotelreservationapp.domain.useCases.reservation

import com.google.firebase.auth.FirebaseAuth
import yusufs.turan.hotelreservationapp.domain.model.Reservation
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class GetUserReservationsUseCase @Inject constructor(
    private val repository: HotelRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(): List<Reservation> {
        val userId = firebaseAuth.currentUser?.uid ?: return emptyList()
        return repository.getUserReservations(userId)
    }
}
