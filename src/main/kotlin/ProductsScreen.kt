
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import database.Database
import models.Product

@Composable
fun ProductsScreen() {
    val database = remember { Database() }
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            products = database.getProducts()
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Продукты", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(products) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(product.name, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                product.description?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
                            }
                            Text("${String.format("%.2f", product.price)} ₽", fontSize = 16.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}