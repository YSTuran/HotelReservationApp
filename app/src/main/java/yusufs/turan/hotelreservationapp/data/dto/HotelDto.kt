package yusufs.turan.hotelreservationapp.data.remote.dto

import yusufs.turan.hotelreservationapp.domain.model.Hotel

data class HotelDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val city: String = "",
    val imageUrls: List<String> = emptyList(),
    val pricePerNight: Double = 0.0,
    val rating: Double = 0.0,
    val ownerId: String = "",
    val amenities: List<String> = emptyList(),
    val isApproved: Boolean = false
) {
    // DTO -> Domain Model Dönüşümü
    fun toHotel(): Hotel {
        return Hotel(
            id = id,
            name = name,
            description = description,
            address = address,
            city = city,
            imageUrls = imageUrls,
            pricePerNight = pricePerNight,
            rating = rating,
            ownerId = ownerId,
            isApproved = isApproved
        )
    }
}

// Domain Model -> DTO Dönüşümü (Veri eklerken/güncellerken kullanacaksın)
fun Hotel.toDto(): HotelDto {
    return HotelDto(
        id = id,
        name = name,
        description = description,
        address = address,
        city = city,
        imageUrls = imageUrls,
        pricePerNight = pricePerNight,
        rating = rating,
        ownerId = ownerId,
        isApproved = isApproved
    )
}