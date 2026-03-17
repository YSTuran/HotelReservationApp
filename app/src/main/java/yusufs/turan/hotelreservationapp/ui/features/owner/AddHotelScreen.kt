package yusufs.turan.hotelreservationapp.ui.features.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val addStatus by viewModel.addHotelStatus.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Otel Adi") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Sehir") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Adres") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Aciklama") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = priceStr,
                onValueChange = { priceStr = it },
                label = { Text("Gecelik Ucret (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text(if (selectedImageUri == null) "Resim Sec" else "Resmi Degistir")
            }

            selectedImageUri?.let { uri ->
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .allowHardware(false)
                        .build(),
                    contentDescription = "Secilen otel resmi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

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
                    viewModel.addHotel(
                        hotel = newHotel,
                        imageUri = selectedImageUri
                    )
                }
            ) {
                Text("Onaya Gonder")
            }

            addStatus?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = if (it.contains("Hata")) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Vazgec")
            }
        }
    }
}
