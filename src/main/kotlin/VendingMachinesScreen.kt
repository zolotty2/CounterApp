
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
import models.VendingMachine

@Composable
fun VendingMachinesScreen() {
    val database = remember { Database() }
    val scope = rememberCoroutineScope()
    var machines by remember { mutableStateOf<List<VendingMachine>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            machines = database.getVendingMachines()
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Вендинговые аппараты", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(machines) { machine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${machine.brand} ${machine.model}", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Серийный номер: ${machine.serialCode}", fontSize = 12.sp, color = Color.Gray)
                            Text("Местоположение: ${machine.location}", fontSize = 12.sp)
                            Text("Выручка: ${String.format("%.2f", machine.totalEarnings)} ₽", fontSize = 12.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}