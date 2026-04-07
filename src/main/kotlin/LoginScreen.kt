import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import models.User
import database.Database

@Composable
fun LoginScreen(onLoginSuccess: (User) -> Unit) {
    val scope = rememberCoroutineScope()
    val database = remember { Database() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF263238), Color(0xFF455A64))   // ← Серый градиент
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Логотип
            Card(
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(50.dp)),
                elevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFFF9800)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ВМ", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("ООО Торговые Автоматы", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Система управления вендинговыми аппаратами", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 450.dp),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Вход в личный кабинет", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("франчайзера", fontSize = 16.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = errorMessage != null,
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFFFF9800))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = errorMessage != null,
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFFFF9800))
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val user = database.authenticateUser(email, password)
                                    if (user != null) onLoginSuccess(user)
                                    else errorMessage = "Неверный email или пароль"
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка подключения: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF9800))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Войти", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { /* Восстановление пароля */ }) {
                        Text("Забыли пароль?", fontSize = 12.sp, color = Color(0xFFFF9800))
                    }
                }
            }
        }
    }
}