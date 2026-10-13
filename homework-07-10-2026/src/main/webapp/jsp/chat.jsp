<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Страница пользователя</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages.css">
</head>
<body>
<main class="page">
    <div class="card">
        <nav class="top-nav" aria-label="Навигация">
            <span class="eyebrow">Личный раздел</span>
            <a href="${pageContext.request.contextPath}/logout">Выйти</a>
        </nav>
        <h1>Добро пожаловать</h1>
        <p class="muted">Авторизация пройдена. Выберите доступный раздел.</p>

        <div class="link-grid">
            <a class="feature-link" href="${pageContext.request.contextPath}/tax">
                <strong>Расчёт налога</strong>
                <span>Ввести сумму и посмотреть историю расчётов</span>
            </a>
            <a class="feature-link" href="${pageContext.request.contextPath}/color">
                <strong>Настройка цвета</strong>
                <span>Выбрать и сохранить цвет в cookie</span>
            </a>
        </div>
    </div>
</main>
</body>
</html>
