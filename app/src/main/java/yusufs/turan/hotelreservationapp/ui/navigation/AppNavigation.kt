package yusufs.turan.hotelreservationapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import yusufs.turan.hotelreservationapp.domain.model.UserRole
import yusufs.turan.hotelreservationapp.ui.features.admin.AdminDashboardScreen
import yusufs.turan.hotelreservationapp.ui.features.auth.AuthUiState
import yusufs.turan.hotelreservationapp.ui.features.auth.AuthViewModel
import yusufs.turan.hotelreservationapp.ui.features.auth.LoginScreen
import yusufs.turan.hotelreservationapp.ui.features.auth.RegisterScreen
import yusufs.turan.hotelreservationapp.ui.features.client.ClientHomeScreen
import yusufs.turan.hotelreservationapp.ui.features.owner.AddHotelScreen
import yusufs.turan.hotelreservationapp.ui.features.owner.OwnerHomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkUserRole()
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Screen.ClientHome.route) {
            ClientHomeScreen()
        }

        composable(Screen.AdminHome.route) {
            AdminDashboardScreen()
        }

        composable(Screen.OwnerHome.route) {
            OwnerHomeScreen(navController)
        }

        composable(Screen.AddHotel.route) {
            AddHotelScreen(navController = navController)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                val role = (uiState as AuthUiState.Success).role
                val targetRoute = when (role) {
                    UserRole.ADMIN -> Screen.AdminHome.route
                    UserRole.HOTEL -> Screen.OwnerHome.route
                    UserRole.USER -> Screen.ClientHome.route
                    else -> Screen.Login.route
                }
                navController.navigate(targetRoute) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }
}