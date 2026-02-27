package yusufs.turan.hotelreservationapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import yusufs.turan.hotelreservationapp.data.remote.dto.HotelDto
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HotelRepository {

    private val hotelCollection = firestore.collection("hotels")

    override suspend fun getHotels(): List<Hotel> {
        return try {
            val snapshot = hotelCollection.get().await()


            snapshot.documents.mapNotNull { doc ->
                doc.toObject(HotelDto::class.java)?.toHotel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun addHotel(hotel: Hotel): Result<Unit> {
        return try {
            val documentRef = if (hotel.id.isEmpty()) {
                hotelCollection.document()
            } else {
                hotelCollection.document(hotel.id)
            }

            val hotelWithId = hotel.copy(id = documentRef.id)


            val hotelDto = HotelDto(
                id = hotelWithId.id,
                name = hotelWithId.name,
                description = hotelWithId.description,
                address = hotelWithId.address,
                city = hotelWithId.city,
                imageUrls = hotelWithId.imageUrls,
                pricePerNight = hotelWithId.pricePerNight,
                rating = hotelWithId.rating,
                ownerId = hotelWithId.ownerId,
                isApproved = hotelWithId.isApproved
            )

            documentRef.set(hotelDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun approveHotel(hotelId: String): Result<Unit> {
        return try {
            hotelCollection.document(hotelId).update("isApproved", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}