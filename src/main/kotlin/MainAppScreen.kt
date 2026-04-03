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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.User

@Composable
fun MainAppScreen(user: User, onLogout: () -> Unit) {
    var selectedMenuItem by remember { mutableStateOf("Главная") }
    var expandedMenuItem by remember { mutableStateOf<String?>(null) }
    var showProfileMenu by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(Color(0xFF1a237e))
        ) {
            // Логотип
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFF0d47a1))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ООО Торговые Автоматы",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            // Информация о пользователе
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFF0d47a1))
                    .clickable { showProfileMenu = !showProfileMenu }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color(0xFFFF9800)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.take(2).uppercase(),
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = user.fullName,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                        Text(
                            text = when (user.role) {
                                "ADMIN" -> "Администратор"
                                "OPERATOR" -> "Оператор"
                                else -> "Пользователь"
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            // Dropdown меню профиля
            if (showProfileMenu) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Основные пункты меню
            SidebarMenuItemText(
                title = "Главная",
                isSelected = selectedMenuItem == "Главная",
                onClick = { selectedMenuItem = "Главная" }
            )

            SidebarMenuItemText(
                title = "Монитор ТА",
                isSelected = selectedMenuItem == "Монитор ТА",
                onClick = { selectedMenuItem = "Монитор ТА" }
            )

            SidebarMenuItemText(
                title = "Детальные отчеты",
                isSelected = selectedMenuItem == "Детальные отчеты",
                onClick = { selectedMenuItem = "Детальные отчеты" }
            )

            SidebarMenuItemText(
                title = "Учет ТМЦ",
                isSelected = selectedMenuItem == "Учет ТМЦ",
                onClick = { selectedMenuItem = "Учет ТМЦ" }
            )

            // Администрирование с подменю
            var isAdminExpanded by remember { mutableStateOf(expandedMenuItem == "Администрирование") }

            SidebarMenuItemText(
                title = "Администрирование",
                isSelected = selectedMenuItem == "Администрирование" || expandedMenuItem == "Администрирование",
                onClick = {
                    expandedMenuItem = if (isAdminExpanded) null else "Администрирование"
                    isAdminExpanded = !isAdminExpanded
                },
                trailingText = if (isAdminExpanded) "▼" else "▶"
            )

            if (isAdminExpanded) {
                SidebarSubMenuItemText(
                    title = "Торговые автоматы",
                    isSelected = selectedMenuItem == "Торговые автоматы",
                    onClick = { selectedMenuItem = "Торговые автоматы" }
                )
                SidebarSubMenuItemText(
                    title = "Компании",
                    isSelected = selectedMenuItem == "Компании",
                    onClick = { selectedMenuItem = "Компании" }
                )
                SidebarSubMenuItemText(
                    title = "Пользователи",
                    isSelected = selectedMenuItem == "Пользователи",
                    onClick = { selectedMenuItem = "Пользователи" }
                )
                SidebarSubMenuItemText(
                    title = "Модемы",
                    isSelected = selectedMenuItem == "Модемы",
                    onClick = { selectedMenuItem = "Модемы" }
                )
                SidebarSubMenuItemText(
                    title = "Дополнительные",
                    isSelected = selectedMenuItem == "Дополнительные",
                    onClick = { selectedMenuItem = "Дополнительные" }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Основной контент
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            when (selectedMenuItem) {
                "Главная" -> DashboardScreen(user)
                "Монитор ТА" -> MonitorScreen()
                "Детальные отчеты" -> ReportsScreen()
                "Учет ТМЦ" -> InventoryScreen()
                "Торговые автоматы" -> VendingMachinesAdminScreen()
                else -> DashboardScreen(user)
            }
        }
    }
}

@Composable
fun SidebarMenuItemText(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    trailingText: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                if (isSelected) Color(0xFF0d47a1) else Color.Transparent
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 14.sp, color = Color.White, modifier = Modifier.weight(1f))
            trailingText?.let {
                Text(it, fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun SidebarSubMenuItemText(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                if (isSelected) Color(0xFF0d47a1) else Color.Transparent
            )
            .padding(start = 24.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Text(title, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
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
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, fontSize = 14.sp, color = color)
    }
}