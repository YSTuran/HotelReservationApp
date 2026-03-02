package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import com.google.firebase.auth.FirebaseAuth
import yusufs.turan.hotelreservationapp.domain.model.HotelComment
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class AddHotelCommentUseCase @Inject constructor(
    private val repository: HotelRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(hotelId: String, commentText: String): Result<Unit> {
        val currentUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Yorum icin once giris yapmalisiniz."))

        if (hotelId.isBlank()) {
            return Result.failure(Exception("Gecerli otel bulunamadi."))
        }

        if (commentText.isBlank()) {
            return Result.failure(Exception("Yorum bos olamaz."))
        }

        val comment = HotelComment(
            hotelId = hotelId,
            userId = currentUser.uid,
            userEmail = currentUser.email.orEmpty(),
            comment = commentText.trim(),
            createdAt = System.currentTimeMillis()
        )

        return repository.addHotelComment(comment)
    }
}
