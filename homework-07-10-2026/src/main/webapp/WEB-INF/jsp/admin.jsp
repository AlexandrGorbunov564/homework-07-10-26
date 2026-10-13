<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Object currentRate = request.getAttribute("currentRate");
    String shownRate = currentRate instanceof Number ? currentRate.toString() + "%" : "не загружена";
    boolean hasError = request.getAttribute("error") != null;
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Администрирование налоговой ставки</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages.css">
</head>
<body>
<main class="page narrow">
    <div class="card">
        <nav class="top-nav" aria-label="Навигация">
            <span class="eyebrow">Панель администратора</span>
            <a href="${pageContext.request.contextPath}/logout">Выйти</a>
        </nav>
        <h1>Налоговая ставка</h1>
        <p class="muted">Установленная ставка общая для всех пользователей.</p>
        <div class="stat">
            <span>Текущая ставка</span>
            <strong><%= shownRate %></strong>
        </div>

        <% if ("1".equals(request.getParameter("saved"))) { %>
            <p class="notice success" role="status">Новая ставка сохранена.</p>
        <% } %>
        <% if (hasError) { %>
            <p class="notice error" role="alert">Неверная ставка. Введите число больше 0 и меньше 100.</p>
        <% } %>

        <form action="${pageContext.request.contextPath}/admin" method="post" accept-charset="UTF-8">
            <label for="rate">Новая ставка (%)</label>
            <input id="rate" type="text" name="rate" inputmode="decimal"
                   pattern="[0-9]{1,2}([.,][0-9]{1,2})?"
                   title="Введите число от 0,01 до 99,99 (не более двух знаков после запятой)"
                   placeholder="Например, 13,00" autocomplete="off" required>
            <p class="hint">Введите число от 0,01% до 99,99%.</p>
            <button type="submit">Сохранить ставку</button>
        </form>
        <p class="footer-links">
            <a href="${pageContext.request.contextPath}/api/tax/rate">Посмотреть ставку в JSON</a>
        </p>
    </div>
</main>
</body>
</html>
