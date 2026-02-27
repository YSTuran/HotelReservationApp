package yusufs.turan.hotelreservationapp.ui.features.owner

import HotelItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import yusufs.turan.hotelreservationapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerHomeScreen(
    navController: NavController,
    viewModel: OwnerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyHotels()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Benim Otellerim") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddHotel.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Otel Ekle")
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is OwnerUiState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
            is OwnerUiState.Success -> {
                if (state.myHotels.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Henüz otel eklemediniz.")
                    }
                } else {
                    LazyColumn(contentPadding = padding) {
                        items(state.myHotels) { hotel ->
                            HotelItem(hotel)
                        }
                    }
                }
            }
            is OwnerUiState.Error -> Box(Modifier.fillMaxSize()) { Text(state.message, Modifier.align(Alignment.Center)) }
        }
    }
}