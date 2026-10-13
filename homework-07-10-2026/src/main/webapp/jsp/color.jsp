<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String color = (String) request.getAttribute("color");
    if (color == null || !java.util.Set.of("black", "blue", "green", "red", "purple").contains(color)) {
        color = "black";
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Выбор цвета</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages.css">
</head>
<body>
<main class="page narrow">
    <div class="card">
        <nav class="top-nav" aria-label="Навигация">
            <span class="eyebrow">Настройки</span>
            <a href="${pageContext.request.contextPath}/chat">К личному разделу</a>
        </nav>
        <h1>Выбор цвета</h1>
        <p class="muted">Цвет сохраняется в cookie браузера.</p>
        <p class="color-preview" style="color: <%= color %>;">Пример выбранного цвета</p>

        <form method="post" action="${pageContext.request.contextPath}/color" accept-charset="UTF-8">
            <label for="selected-color">Выберите цвет</label>
            <select id="selected-color" name="selected-color">
                <option value="black" <%= "black".equals(color) ? "selected" : "" %>>Чёрный</option>
                <option value="blue" <%= "blue".equals(color) ? "selected" : "" %>>Синий</option>
                <option value="green" <%= "green".equals(color) ? "selected" : "" %>>Зелёный</option>
                <option value="red" <%= "red".equals(color) ? "selected" : "" %>>Красный</option>
                <option value="purple" <%= "purple".equals(color) ? "selected" : "" %>>Фиолетовый</option>
            </select>
            <button type="submit">Сохранить цвет</button>
        </form>
    </div>
</main>
</body>
</html>
