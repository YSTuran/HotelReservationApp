package yusufs.turan.hotelreservationapp.data.dto

import yusufs.turan.hotelreservationapp.domain.model.HotelComment

data class HotelCommentDto(
    val id: String = "",
    val hotelId: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val comment: String = "",
    val createdAt: Long = 0L
) {
    fun toHotelComment(): HotelComment {
        return HotelComment(
            id = id,
            hotelId = hotelId,
            userId = userId,
            userEmail = userEmail,
            comment = comment,
            createdAt = createdAt
        )
    }
}

fun HotelComment.toDto(): HotelCommentDto {
    return HotelCommentDto(
        id = id,
        hotelId = hotelId,
        userId = userId,
        userEmail = userEmail,
        comment = comment,
        createdAt = createdAt
    )
}
