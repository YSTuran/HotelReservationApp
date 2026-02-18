package yusufs.turan.hotelreservationapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    object ClientHome : Screen("client_home")
    object AdminHome : Screen("admin_home")
    object OwnerHome : Screen("owner_home")

    object AddHotel : Screen("add_hotel")
}