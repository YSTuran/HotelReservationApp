package yusufs.turan.hotelreservationapp.domain.useCases.hotel

import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Inject

class AddHotelUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotel: Hotel): Result<Unit> {

        if (hotel.name.isBlank()) {
            return Result.failure(Exception("Otel ismi boş bırakılamaz."))
        }
        if (hotel.city.isBlank()) {
            return Result.failure(Exception("Şehir bilgisi zorunludur."))
        }
        if (hotel.pricePerNight <= 0) {
            return Result.failure(Exception("Gecelik ücret 0 veya daha düşük olamaz."))
        }
        if (hotel.description.length < 10) {
            return Result.failure(Exception("Otel açıklaması çok kısa."))
        }

        val hotelToAdd = hotel.copy(isApproved = false)

        return repository.addHotel(hotelToAdd)
    }
}