package farit.dev.servlet;

import alexander.service.AuthAdminService;
import alexander.service.impl.AuthAdminServiceImpl;
import farit.dev.service.AuthUserService;
import farit.dev.service.impl.AuthUserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/sign-in")
public class SignInServlet extends HttpServlet {

    private AuthAdminService adminService;
    private AuthUserService userService;

    @Override
    public void init() throws ServletException {
        adminService = new AuthAdminServiceImpl();
        userService = new AuthUserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/sign-in.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/sign-in?error=1");
            return;
        }

        if (adminService.isValidAdmin(username, password)) {
            HttpSession session = prepareSession(req);
            session.setAttribute("role", "ADMIN");
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }

        if (userService.isValidUser(username, password)) {
            HttpSession session = prepareSession(req);
            session.setAttribute("role", "USER");
            session.setAttribute("username", username);
            resp.sendRedirect(req.getContextPath() + "/chat");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/sign-in?error=1");
    }

    private HttpSession prepareSession(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        req.changeSessionId();
        session.removeAttribute("username");
        session.removeAttribute("role");
        return session;
    }
}
