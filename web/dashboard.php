<?php
session_start();
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit;
}

require 'config.php';

$role = $_SESSION['role'] ?? 'user';
$full_name = $_SESSION['full_name'] ?? 'Пользователь';

$role_title = match($role) {
    'admin' => 'Администратор',
    'manager' => 'Менеджер',
    'engineer' => 'Инженер',
    default => 'Пользователь'
};

// Получаем данные торговых автоматов из базы
$stmt = $pdo->query("
    SELECT 
        vm.serial_code,
        vm.inventory_code,
        vm.intall_location AS location,
        b.brand_name,
        m.model_name,
        s.status_name,
        vm.total_earnings,
        vm.last_maintanance_date,
        vm.date_next_maintanance
    FROM vending_machines vm
    JOIN models m ON vm.id_model = m.id
    JOIN brands b ON m.id_brand = b.id
    JOIN statuses s ON vm.id_status = s.id
    ORDER BY vm.serial_code
");
$machines = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Панель — КПТ Vending</title>
  <link rel="stylesheet" href="style.css">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="app">
  <!-- Sidebar -->
  <div class="sidebar">
    <div class="logo">
      <div class="logo-icon">☕</div>
      <div>
        <h2>КПТ Vending</h2>
        <p>Панель <?= $role_title ?></p>
      </div>
    </div>

    <div class="menu">
      <div class="menu-item active" data-screen="ta"><i class="fas fa-coffee"></i> Торговые автоматы</div>
      
      <?php if ($role === 'admin' || $role === 'manager'): ?>
        <div class="menu-item" data-screen="calendar"><i class="fas fa-calendar-alt"></i> Календарь обслуживания</div>
      <?php endif; ?>

      <?php if ($role === 'admin' || $role === 'engineer'): ?>
        <div class="menu-item" data-screen="schedule"><i class="fas fa-tasks"></i> График работ</div>
      <?php endif; ?>

      <?php if ($role === 'admin'): ?>
        <div class="menu-item" data-screen="admin"><i class="fas fa-cog"></i> Администрирование</div>
      <?php endif; ?>
    </div>

    <div class="user-info">
      <div class="avatar"><?= mb_substr($full_name, 0, 2) ?></div>
      <div>
        <strong><?= htmlspecialchars($full_name) ?></strong><br>
        <small><?= $role_title ?></small>
      </div>
    </div>
  </div>

  <!-- Main Content -->
  <div class="main-content">
    <header>
      <h1 id="page-title">Торговые автоматы</h1>
      <button class="btn btn-secondary" onclick="logout()">Выйти</button>
    </header>

    <!-- Экран Торговые автоматы -->
    <div id="screen-ta" class="screen active">
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Серийный №</th>
              <th>Инв. №</th>
              <th>Местоположение</th>
              <th>Модель</th>
              <th>Статус</th>
              <th>Выручка</th>
              <th>Последнее ТО</th>
              <th>Следующее ТО</th>
            </tr>
          </thead>
          <tbody>
            <?php foreach ($machines as $m): ?>
            <tr>
              <td><strong><?= htmlspecialchars($m['serial_code']) ?></strong></td>
              <td><?= htmlspecialchars($m['inventory_code']) ?></td>
              <td><?= htmlspecialchars($m['location']) ?></td>
              <td><?= htmlspecialchars($m['brand_name'] . ' ' . $m['model_name']) ?></td>
              <td>
                <span class="status <?= $m['status_name'] === 'Работает' ? 'status-green' : 'status-yellow' ?>">
                  <?= htmlspecialchars($m['status_name']) ?>
                </span>
              </td>
              <td><?= number_format($m['total_earnings'], 2, ',', ' ') ?> ₽</td>
              <td><?= $m['last_maintanance_date'] ? htmlspecialchars($m['last_maintanance_date']) : '—' ?></td>
              <td><?= $m['date_next_maintanance'] ? htmlspecialchars($m['date_next_maintanance']) : '—' ?></td>
            </tr>
            <?php endforeach; ?>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Остальные экраны -->
    <div id="screen-calendar" class="screen">
      <h2>Календарь обслуживания</h2>
      <?php if ($role === 'admin' || $role === 'manager'): ?>
        <p>Здесь будет годовой календарь технического обслуживания.</p>
      <?php else: ?>
        <p style="color:red;">Доступ запрещён для вашей роли.</p>
      <?php endif; ?>
    </div>

    <div id="screen-schedule" class="screen">
      <h2>График работ</h2>
      <?php if ($role === 'admin' || $role === 'engineer'): ?>
        <p>Здесь будет drag & drop распределение заявок.</p>
      <?php else: ?>
        <p style="color:red;">Доступ запрещён для вашей роли.</p>
      <?php endif; ?>
    </div>

    <div id="screen-admin" class="screen">
      <h2>Администрирование</h2>
      <?php if ($role === 'admin'): ?>
        <p>Управление системой (пользователи, компании и т.д.).</p>
      <?php else: ?>
        <p style="color:red;">Доступ только для администратора.</p>
      <?php endif; ?>
    </div>
  </div>
</div>

<script>
// Переключение экранов
document.querySelectorAll('.menu-item').forEach(item => {
  item.addEventListener('click', () => {
    document.querySelectorAll('.menu-item').forEach(i => i.classList.remove('active'));
    item.classList.add('active');
    
    document.querySelectorAll('.screen').forEach(screen => screen.classList.remove('active'));
    document.getElementById('screen-' + item.dataset.screen).classList.add('active');
  });
});

function logout() {
  if (confirm("Выйти из системы?")) {
    window.location.href = 'logout.php';
  }
}
</script>
</body>
</html>