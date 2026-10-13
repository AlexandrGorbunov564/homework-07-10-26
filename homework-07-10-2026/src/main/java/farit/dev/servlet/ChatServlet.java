package farit.dev.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"USER".equals(session.getAttribute("role"))
                || session.getAttribute("username") == null
                || session.getAttribute("username").toString().isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/sign-in");
            return;
        }
        req.getRequestDispatcher("/jsp/chat.jsp").forward(req, resp);
    }
}
