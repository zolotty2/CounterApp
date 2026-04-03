import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import database.Database
import models.VendingMachineMonitorItem
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MonitorScreen() {
    val database = remember { Database() }
    val scope = rememberCoroutineScope()
    var machines by remember { mutableStateOf<List<VendingMachineMonitorItem>>(emptyList()) }
    var filteredMachines by remember { mutableStateOf<List<VendingMachineMonitorItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var filterText by remember { mutableStateOf("") }
    var showOnlyOnline by remember { mutableStateOf(false) }
    var showOnlyOffline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            val data = database.getMonitorVendingMachines()
            machines = data
            filteredMachines = data
            loading = false
        }
    }

    fun applyFilters() {
        filteredMachines = machines.filter { machine ->
            (filterText.isEmpty() || machine.location.contains(filterText, ignoreCase = true) ||
                    machine.serialCode.contains(filterText, ignoreCase = true)) &&
                    (!showOnlyOnline || machine.connectionStatus == "Online") &&
                    (!showOnlyOffline || machine.connectionStatus == "Offline")
        }
    }

    fun exportToExcel() {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val file = File("monitor_export_$timestamp.csv")
        file.printWriter().use { writer ->
            writer.println("№;Серийный номер;Местоположение;Модель;Связь;Загрузка;Денежные средства;Последнее событие;Оборудование;Доп.инфо")
            filteredMachines.forEach {
                writer.println("${it.id};${it.serialCode};${it.location};${it.brandModel};${it.connectionStatus};${it.loadInfo};${it.cashAmount};${it.lastEvent};${it.equipmentInfo};${it.additionalInfo}")
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Монитор торговых автоматов", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("данные актуальны на ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))} (UTC+3)", fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Общее состояние", fontWeight = FontWeight.Medium)
                Row {
                    Checkbox(checked = showOnlyOnline, onCheckedChange = { showOnlyOnline = it; applyFilters() })
                    Text("Online", modifier = Modifier.padding(end = 16.dp))
                    Checkbox(checked = showOnlyOffline, onCheckedChange = { showOnlyOffline = it; applyFilters() })
                    Text("Offline")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = filterText, onValueChange = { filterText = it; applyFilters() }, label = { Text("Поиск (адрес/серийный номер)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = { applyFilters() }) { Text("Применить") }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { filterText = ""; showOnlyOnline = false; showOnlyOffline = false; applyFilters() }) { Text("Очистить") }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = { exportToExcel() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50))) {
                        Icon(Icons.Filled.Save, contentDescription = null)   // ← ИСПРАВЛЕНО
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Экспорт в Excel")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredMachines.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет активных торговых автоматов, соответствующих заданному фильтру", fontSize = 14.sp, color = Color.Gray)
            }
        } else {
            Card(elevation = 4.dp, modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.background(Color(0xFF1a237e)).padding(12.dp)) {
                        TableHeaderCell("#", 0.5f)
                        TableHeaderCell("Торговый автомат", 2f)
                        TableHeaderCell("Связь", 1f)
                        TableHeaderCell("Загрузка", 1f)
                        TableHeaderCell("Денежные средства", 1.5f)
                        TableHeaderCell("События", 1.5f)
                        TableHeaderCell("Оборудование", 1.5f)
                        TableHeaderCell("Информация", 1f)
                        TableHeaderCell("Доп.", 1f)
                    }
                    filteredMachines.forEach { machine ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(12.dp).border(BorderStroke(1.dp, Color.LightGray))) {
                            TableCell(machine.id.toString(), 0.5f)
                            TableCell("${machine.serialCode} - \"${machine.location}\"", 2f)
                            TableCell(machine.connectionStatus, 1f)
                            TableCell(machine.loadInfo, 1f)
                            TableCell(machine.cashAmount, 1.5f)
                            TableCell(machine.lastEvent, 1.5f)
                            TableCell(machine.equipmentInfo, 1.5f)
                            TableCell(machine.brandModel, 1f)
                            TableCell(machine.additionalInfo, 1f)
                        }
                        machine.detailRows?.let {
                            Row(modifier = Modifier.background(Color(0xFFF5F5F5)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                                Spacer(modifier = Modifier.width(48.dp))
                                Text(it.joinToString(" • "), fontSize = 10.sp, color = Color.DarkGray)
                            }
                        }
                        Divider()
                    }
                    Row(modifier = Modifier.background(Color(0xFFE0E0E0)).padding(12.dp)) {
                        val totalCash = filteredMachines.sumOf { it.cashAmount.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
                        Text("Итого автоматов: ${filteredMachines.size} (${filteredMachines.count { it.connectionStatus == "Online" }} / ${filteredMachines.count { it.connectionStatus == "Offline" }} / 0). Денег в автоматах: $totalCash р.", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableHeaderCell(text: String, colWeight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(colWeight),
        fontWeight = FontWeight.Bold,
        color = Color.White,
        fontSize = 12.sp
    )
}

@Composable
fun RowScope.TableCell(text: String, colWeight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(colWeight),
        fontSize = 11.sp
    )
}