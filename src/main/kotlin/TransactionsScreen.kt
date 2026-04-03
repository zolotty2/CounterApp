
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
import models.Transaction

@Composable
fun TransactionsScreen() {
    val database = remember { Database() }
    val scope = rememberCoroutineScope()
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            transactions = database.getTransactions()
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Транзакции", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(transactions) { transaction ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(transaction.productName, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Text("${String.format("%.2f", transaction.amount)} ₽", color = Color(0xFF4CAF50))
                            }
                            Text("${transaction.location} • ${transaction.method}", fontSize = 12.sp, color = Color.Gray)
                            Text(transaction.date, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}