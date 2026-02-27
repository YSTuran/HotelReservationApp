package yusufs.turan.hotelreservationapp.ui.features.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import yusufs.turan.hotelreservationapp.domain.model.Hotel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHotelScreen(
    navController: NavController,
    viewModel: OwnerViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }

    val addStatus by viewModel.addHotelStatus.collectAsState()

    LaunchedEffect(addStatus) {
        if (addStatus?.contains("Basarili") == true) {
            viewModel.resetAddStatus()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Yeni Otel Ekle") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Otel Adı") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Şehir") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Adres") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Açıklama") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = priceStr,
                onValueChange = { priceStr = it },
                label = { Text("Gecelik Ücret (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val price = priceStr.toDoubleOrNull() ?: 0.0

                    val newHotel = Hotel(
                        id = "",
                        name = name,
                        description = description,
                        address = address,
                        city = city,
                        pricePerNight = price,
                        ownerId = currentUserId,
                        rating = 0.0,
                        imageUrls = emptyList(),
                        isApproved = false
                    )
                    viewModel.addHotel(newHotel)
                }
            ) {
                Text("Onaya Gönder")
            }

            addStatus?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = if (it.contains("Hata")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
        }
    }
}
