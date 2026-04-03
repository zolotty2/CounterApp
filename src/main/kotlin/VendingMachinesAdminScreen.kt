import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import database.Database
import models.VendingMachine
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun VendingMachinesAdminScreen() {
    val database = remember { Database() }
    val scope = rememberCoroutineScope()
    var machines by remember { mutableStateOf<List<VendingMachine>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingMachine by remember { mutableStateOf<VendingMachine?>(null) }

    fun loadMachines() {
        scope.launch {
            loading = true
            machines = database.getVendingMachines()
            loading = false
        }
    }

    LaunchedEffect(Unit) { loadMachines() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Торговые автоматы", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Создать")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(machines) { machine ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { editingMachine = machine }, elevation = 4.dp) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("${machine.brand} ${machine.model}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Text("Серийный: ${machine.serialCode}", fontSize = 12.sp, color = Color.Gray)
                                Text(machine.location, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${String.format("%.2f", machine.totalEarnings)} ₽", color = Color(0xFF4CAF50))
                                Text(machine.status, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog || editingMachine != null) {
        VendingMachineFormDialog(
            initialMachine = editingMachine,
            onDismiss = {
                showCreateDialog = false
                editingMachine = null
            },
            onSave = {
                loadMachines()
                showCreateDialog = false
                editingMachine = null
            }
        )
    }
}

@Composable
fun VendingMachineFormDialog(initialMachine: VendingMachine?, onDismiss: () -> Unit, onSave: () -> Unit) {
    val database = remember { Database() }
    val scope = rememberCoroutineScope()
    var serialCode by remember { mutableStateOf(initialMachine?.serialCode ?: "") }
    var inventoryCode by remember { mutableStateOf(initialMachine?.inventoryCode ?: "") }
    var location by remember { mutableStateOf(initialMachine?.location ?: "") }
    var brand by remember { mutableStateOf(initialMachine?.brand ?: "") }
    var model by remember { mutableStateOf(initialMachine?.model ?: "") }
    var status by remember { mutableStateOf(initialMachine?.status ?: "Работает") }
    var type by remember { mutableStateOf(initialMachine?.type ?: "Кофейный") }
    var totalEarnings by remember { mutableStateOf(initialMachine?.totalEarnings?.toString() ?: "0") }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialMachine == null) "Создание ТА" else "Редактирование ТА") },
        text = {
            Column(modifier = Modifier.widthIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = serialCode, onValueChange = { serialCode = it }, label = { Text("Серийный номер *") }, singleLine = true)
                OutlinedTextField(value = inventoryCode, onValueChange = { inventoryCode = it }, label = { Text("Инвентарный номер *") }, singleLine = true)
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Местоположение *") }, singleLine = true)
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Производитель") }, singleLine = true)
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Модель") }, singleLine = true)
                OutlinedTextField(value = totalEarnings, onValueChange = { totalEarnings = it }, label = { Text("Общая выручка") })
                Spacer(modifier = Modifier.height(8.dp))
                Text("Статус", fontSize = 12.sp)
                Row {
                    listOf("Работает", "Не работает", "На обслуживании").forEach { st ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = status == st, onClick = { status = st })
                            Text(st, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        saving = true
                        // Здесь должен быть вызов database.saveVendingMachine(...)
                        // Для демонстрации просто задержка
                        kotlinx.coroutines.delay(1000)
                        saving = false
                        onSave()
                    }
                },
                enabled = !saving && serialCode.isNotBlank() && inventoryCode.isNotBlank() && location.isNotBlank()
            ) {
                if (saving) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}