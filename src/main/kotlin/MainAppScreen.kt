import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.User

@Composable
fun MainAppScreen(user: User, onLogout: () -> Unit) {
    var selectedMenuItem by remember { mutableStateOf("Главная") }
    var expandedAdmin by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }

    val isAdmin = user.role == "admin"
    val isManager = user.role == "manager" || isAdmin
    val isEngineer = user.role == "engineer" || isAdmin

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(Color(0xFF263238))
        ) {
            // Логотип
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFF37474F))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("КПТ Vending", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Профиль
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFF37474F))
                    .clickable { showProfileMenu = !showProfileMenu }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.small).background(Color(0xFFFF9800)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.fullName.take(2).uppercase(), fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(user.fullName, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Text(
                            when (user.role) {
                                "admin" -> "Администратор"
                                "manager" -> "Менеджер"
                                "engineer" -> "Инженер"
                                else -> "Пользователь"
                            },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                }
            }

            // Меню профиля
            if (showProfileMenu) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    elevation = 4.dp
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = "Мой профиль",
                            onClick = { showProfileMenu = false }
                        )
                        ProfileMenuItem(
                            icon = Icons.Default.List,
                            title = "Мои сессии",
                            onClick = { showProfileMenu = false }
                        )
                        Divider()
                        ProfileMenuItem(
                            icon = Icons.Default.ExitToApp,
                            title = "Выход",
                            onClick = {
                                showProfileMenu = false
                                onLogout()
                            },
                            color = Color.Red
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Основное меню
            SidebarMenuItem(
                title = "Главная",
                isSelected = selectedMenuItem == "Главная",
                onClick = { selectedMenuItem = "Главная" }
            )

            if (isManager) {
                SidebarMenuItem(
                    title = "Монитор ТА",
                    isSelected = selectedMenuItem == "Монитор ТА",
                    onClick = { selectedMenuItem = "Монитор ТА" }
                )
                SidebarMenuItem(
                    title = "Детальные отчеты",
                    isSelected = selectedMenuItem == "Детальные отчеты",
                    onClick = { selectedMenuItem = "Детальные отчеты" }
                )
            }

            if (isEngineer || isManager) {
                SidebarMenuItem(
                    title = "Учет ТМЦ",
                    isSelected = selectedMenuItem == "Учет ТМЦ",
                    onClick = { selectedMenuItem = "Учет ТМЦ" }
                )
            }

            if (isAdmin) {
                SidebarMenuItem(
                    title = "Администрирование",
                    isSelected = expandedAdmin,
                    onClick = { expandedAdmin = !expandedAdmin },
                    trailing = if (expandedAdmin) "▼" else "▶"
                )

                if (expandedAdmin) {
                    SidebarSubMenuItem("Торговые автоматы", selectedMenuItem == "Торговые автоматы") { selectedMenuItem = "Торговые автоматы" }
                    SidebarSubMenuItem("Компании", false) { }
                    SidebarSubMenuItem("Пользователи", false) { }
                    SidebarSubMenuItem("Модемы", false) { }
                    SidebarSubMenuItem("Дополнительные", false) { }
                }
            }

            Spacer(Modifier.weight(1f))
        }

        // Основной контент
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
            when (selectedMenuItem) {
                "Главная" -> DashboardScreen(user)
                "Монитор ТА" -> if (isManager) MonitorScreen() else Text("Доступ запрещён", modifier = Modifier.align(Alignment.Center))
                "Детальные отчеты" -> if (isManager) ReportsScreen() else Text("Доступ запрещён", modifier = Modifier.align(Alignment.Center))
                "Учет ТМЦ" -> if (isEngineer || isManager) InventoryScreen() else Text("Доступ запрещён", modifier = Modifier.align(Alignment.Center))
                "Торговые автоматы" -> if (isAdmin) VendingMachinesAdminScreen() else Text("Доступ запрещён", modifier = Modifier.align(Alignment.Center))
                else -> DashboardScreen(user)
            }
        }
    }
}

// ====================== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ======================

@Composable
fun SidebarMenuItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    trailing: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) Color(0xFF37474F) else Color.Transparent)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 15.sp, color = Color.White, modifier = Modifier.weight(1f))
            trailing?.let { Text(it, fontSize = 13.sp, color = Color.White.copy(0.8f)) }
        }
    }
}

@Composable
fun SidebarSubMenuItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) Color(0xFF455A64) else Color.Transparent)
            .padding(start = 32.dp, end = 14.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Text(title, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 14.sp, color = color)
    }
}