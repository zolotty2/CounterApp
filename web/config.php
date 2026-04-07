<?php
$host = 'localhost';
$dbname = 'vending_db2';
$user = 'postgres';
$pass = '1111';           // ← поменяй, если у тебя другой пароль

try {
    $pdo = new PDO("pgsql:host=$host;dbname=$dbname", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $e) {
    die("Ошибка подключения к базе: " . $e->getMessage());
}
?>