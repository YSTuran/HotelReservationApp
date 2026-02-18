import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import yusufs.turan.hotelreservationapp.domain.model.Hotel
import yusufs.turan.hotelreservationapp.ui.features.owner.OwnerViewModel
import androidx.compose.runtime.*

@Composable
fun AddHotelScreen(
    viewModel: OwnerViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    val status by viewModel.addHotelStatus.collectAsState()
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Otel Adı") })
        TextField(value = city, onValueChange = { city = it }, label = { Text("Şehir") })

        Button(onClick = {
            val newHotel = Hotel(id = "", name = name, city = city,
                description = "", address = "", imageUrls = emptyList(),
                pricePerNight = 0.0, rating = 0.0, ownerId = "")
            viewModel.addHotel(newHotel)
        }) {
            Text("Onaya Gönder")
        }

        status?.let { Text(it) }
    }
}