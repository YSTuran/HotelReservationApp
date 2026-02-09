package yusufs.turan.hotelreservationapp.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val city: String,
    val imageUrls: List<String>,
    val pricePerNight: Double,
    val rating: Double,
    val ownerId: String,
    val amenities: List<String>,
    val isApproved: Boolean = false
)
