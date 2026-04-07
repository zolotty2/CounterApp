<?php
// index.php — Главная точка входа

session_start();
require 'config.php';

// Если пользователь уже вошёл — сразу переходим в панель
if (isset($_SESSION['user_id'])) {
    header("Location: dashboard.php");
    exit;
}

// Если не вошёл — показываем страницу входа
header("Location: login.php");
exit;
?>