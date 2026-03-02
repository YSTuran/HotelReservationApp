package yusufs.turan.hotelreservationapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    object ClientHome : Screen("client_home")
    object HotelDetail : Screen("hotel_detail/{hotelId}") {
        fun createRoute(hotelId: String): String = "hotel_detail/$hotelId"
    }
    object AdminHome : Screen("admin_home")
    object OwnerHome : Screen("owner_home")

    object AddHotel : Screen("add_hotel")
}
