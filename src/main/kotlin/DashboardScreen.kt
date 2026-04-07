import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.User

@Composable
fun DashboardScreen(user: User) {
    var visibleWidgets by remember {
        mutableStateOf(
            mutableMapOf(
                "efficiency" to true,
                "status" to true,
                "summary" to true,
                "sales" to true,
                "news" to true
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Личный кабинет. Главная",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238),        // ← Тёмно-серый
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Актуальная информация о сети вендинговых аппаратов",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 380.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(widgetsList.filter { visibleWidgets[it.id] == true }) { widget ->
                WidgetCard(
                    title = widget.title,
                    onClose = { visibleWidgets[widget.id] = false }
                ) {
                    when (widget.id) {
                        "efficiency" -> EfficiencyWidget()
                        "status" -> StatusWidget()
                        "summary" -> SummaryWidget()
                        "sales" -> SalesWidget()
                        "news" -> NewsWidget()
                    }
                }
            }
        }

        if (visibleWidgets.values.any { !it }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                var showAddDialog by remember { mutableStateOf(false) }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF9800))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Добавить виджет")
                }

                if (showAddDialog) {
                    AddWidgetDialog(
                        availableWidgets = widgetsList.filter { visibleWidgets[it.id] == false },
                        onAdd = { widgetId ->
                            visibleWidgets[widgetId] = true
                            showAddDialog = false
                        },
                        onDismiss = { showAddDialog = false }
                    )
                }
            }
        }
    }
}

val widgetsList = listOf(
    Widget("efficiency", "Эффективность сети"),
    Widget("status", "Состояние сети"),
    Widget("summary", "Сводка"),
    Widget("sales", "Динамика продаж"),
    Widget("news", "Новости")
)

data class Widget(val id: String, val title: String)

@Composable
fun WidgetCard(
    title: String,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 6.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF263238)     // ← Тёмно-серый заголовок
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Скрыть",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }

            Divider()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

// Виджеты (с минимальными правками цвета)
@Composable
fun EfficiencyWidget() {
    val workingPercent = 100.0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Работающие автоматы", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val sweepAngle = (workingPercent * 3.6f).toFloat()
                drawArc(color = Color(0xFF4CAF50), startAngle = -90f, sweepAngle = sweepAngle, useCenter = true)
                drawArc(color = Color.LightGray, startAngle = -90f + sweepAngle, sweepAngle = 360f - sweepAngle, useCenter = true)
            }
            Text(
                text = "${String.format("%.1f", workingPercent)}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            LegendItem(color = Color(0xFF4CAF50), text = "Работают")
            LegendItem(color = Color.Red, text = "Не работают")
            LegendItem(color = Color(0xFFFF9800), text = "На обслуживании")
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 11.sp)
    }
}

@Composable
fun StatusWidget() {
    val stats = mapOf("Работает" to 3, "Не работает" to 0, "На обслуживании" to 0)
    val total = stats.values.sum()

    Column(modifier = Modifier.fillMaxSize()) {
        stats.forEach { (status, count) ->
            val percentage = if (total > 0) count.toFloat() / total else 0f
            val color = when (status) {
                "Работает" -> Color(0xFF4CAF50)
                "Не работает" -> Color.Red
                else -> Color(0xFFFF9800)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(status, fontSize = 12.sp)
                Text("$count / $total", fontSize = 12.sp, color = color)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = percentage,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = color,
                backgroundColor = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SummaryWidget() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
        SummaryRow("Денег в ТА", "27 599 ₽")
        SummaryRow("Сданы в ТА", "12 109 ₽")
        SummaryRow("Выручка, сегодня", "11 910 ₽")
        SummaryRow("Выручка, через", "13 360 ₽")
        SummaryRow("Имеющиеся скидки, сегодня", "8 145 ₽")
        SummaryRow("Имеющиеся скидки, через", "9 900 ₽")
        SummaryRow("Обслуживание ТА, сегодня", "2 / 1")
    }
}

@Composable
fun SummaryRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, color = Color(0xFF263238))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
    }
}

@Composable
fun SalesWidget() {
    var filterType by remember { mutableStateOf("amount") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Данные по продукции с 01.03.2025 по 10.03.2025",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            FilterChip(selected = filterType == "amount", onClick = { filterType = "amount" }, label = "По сумме")
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(selected = filterType == "quantity", onClick = { filterType = "quantity" }, label = "По количеству")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val salesData = listOf(
            Pair("01.03", 15000), Pair("02.03", 12000), Pair("03.03", 10000),
            Pair("04.03", 8000), Pair("05.03", 5000), Pair("06.03", 3000),
            Pair("07.03", 2000), Pair("08.03", 1000), Pair("09.03", 500), Pair("10.03", 0)
        )

        val maxValue = salesData.maxOfOrNull { it.second } ?: 1

        salesData.forEach { (date, value) ->
            val percentage = value.toFloat() / maxValue
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(date, fontSize = 10.sp, modifier = Modifier.width(40.dp))
                Box(
                    modifier = Modifier.weight(1f).height(24.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier.fillMaxHeight().fillMaxWidth(percentage).clip(RoundedCornerShape(4.dp)).background(Color(0xFFFF9800))
                    )
                }
                Text(
                    text = if (filterType == "amount") "${value / 1000}k ₽" else "${value / 100} шт",
                    fontSize = 10.sp,
                    modifier = Modifier.width(50.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }
    }
}

@Composable
fun FilterChip(selected: Boolean, onClick: () -> Unit, label: String) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFFFF9800) else Color.LightGray.copy(alpha = 0.3f)
    ) {
        Text(
            label,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) Color.White else Color.Black
        )
    }
}

@Composable
fun NewsWidget() {
    val news = listOf(
        Triple("29.01.25", "Промышленники КПИН получили аварийную сеть.", ""),
        Triple("31.12.24", "Изготовление подразделений от КПТ Vending / KIT.", ""),
        Triple("28.12.24", "Создание ПАС 35 х 27м для КПТ.", ""),
        Triple("04.12.24", "Разработка новой CRM-системы КПТ Shop.", ""),
        Triple("27.11.24", "Новый модуль основных автоматов от КПТ Vending.", ""),
        Triple("20.11.24", "Подключение сертификата PCI DSS 4.0.", "")
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        news.forEachIndexed { index, (date, title, _) ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(date, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFFFF9800))
                Text(title, fontSize = 12.sp, color = Color(0xFF263238))
            }
            if (index < news.size - 1) Divider()
        }
    }
}

@Composable
fun AddWidgetDialog(
    availableWidgets: List<Widget>,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить виджет") },
        text = {
            Column {
                Text("Выберите виджет для добавления:")
                Spacer(modifier = Modifier.height(16.dp))
                availableWidgets.forEach { widget ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAdd(widget.id) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(widget.title)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}