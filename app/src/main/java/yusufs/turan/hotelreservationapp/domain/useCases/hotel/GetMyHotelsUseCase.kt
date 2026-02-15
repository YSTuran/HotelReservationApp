package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import com.google.firebase.auth.FirebaseAuth
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class GetMyHotelsUseCase @Inject constructor(
    private val repository: HotelRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(): List<Hotel> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return emptyList()
        val allHotels = repository.getHotels()

        return allHotels.filter { it.ownerId == currentUserId }
    }
}