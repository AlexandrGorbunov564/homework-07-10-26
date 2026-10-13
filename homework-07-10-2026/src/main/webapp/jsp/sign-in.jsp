<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Вход в систему</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages.css">
</head>
<body>
<main class="page narrow">
    <div class="card">
        <p class="eyebrow">Налоговый сервис</p>
        <h1>Вход в систему</h1>
        <p class="muted">Войдите как обычный пользователь или админ</p>

        <% if ("1".equals(request.getParameter("error"))) { %>
        <p class="notice error" role="alert">Неверный логин или пароль.</p>
        <% } %>

        <form action="${pageContext.request.contextPath}/sign-in" method="post" accept-charset="UTF-8">
            <label for="username">Логин</label>
            <input id="username" type="text" name="username" autocomplete="username" maxlength="100" required autofocus>

            <label for="password">Пароль</label>
            <input id="password" type="password" name="password" autocomplete="current-password" required>

            <button type="submit">Войти</button>
        </form>

        <p class="footer-links muted">Расчёт налога доступен после входа</p>
    </div>
</main>
</body>
</html>
