<?php
session_start();
require 'config.php';

$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = trim($_POST['email']);
    $password = trim($_POST['password']);

    $stmt = $pdo->prepare("SELECT id, full_name, email, role FROM users WHERE email = ? AND password = ?");
    $stmt->execute([$email, $password]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user) {
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['full_name'] = $user['full_name'];
        $_SESSION['email'] = $user['email'];
        $_SESSION['role'] = $user['role'];

        header("Location: dashboard.php");
        exit;
    } else {
        $error = "Неверный email или пароль";
    }
}
?>

<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Вход — КПТ Vending</title>
  <link rel="stylesheet" href="style.css">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="login-screen">
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-big">☕</div>
        <h1>КПТ Vending</h1>
        <p>Панель управления франчайзера</p>
      </div>

      <h2>Вход в систему</h2>

      <?php if ($error): ?>
        <div class="error-message"><?= htmlspecialchars($error) ?></div>
      <?php endif; ?>

      <form method="POST">
        <div class="form-group">
          <label>Email</label>
          <input type="email" name="email" required placeholder="your@email.com" 
                 value="<?= htmlspecialchars($_POST['email'] ?? '') ?>">
        </div>

        <div class="form-group">
          <label>Пароль</label>
          <input type="password" name="password" required placeholder="••••••••">
        </div>

        <button type="submit" class="btn btn-primary login-btn">
          <i class="fas fa-sign-in-alt"></i> Войти
        </button>
      </form>

      <div class="login-footer">
        <small>Тестовые аккаунты:<br>
        admin@vending.ru / admin123<br>
        manager@vending.ru / manager123<br>
        engineer@vending.ru / engineer123<br>
        user1@vending.ru / user123</small>
      </div>
    </div>
  </div>
</div>

</body>
</html>