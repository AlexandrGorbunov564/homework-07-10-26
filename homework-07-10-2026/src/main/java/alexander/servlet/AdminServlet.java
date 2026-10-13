package alexander.servlet;

import alexander.repository.TaxRateRepository;
import alexander.repository.impl.TaxRateRepositoryImpl;
import alexander.service.TaxRateService;
import alexander.service.impl.TaxRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private TaxRateService service;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("PostgreSQL Driver не найден", e);
        }
        TaxRateRepository repository = new TaxRateRepositoryImpl();
        service = new TaxRateServiceImpl(repository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = requireAdmin(req, resp);
        if (session == null) {
            return;
        }
        showAdmin(req, resp, session);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = requireAdmin(req, resp);
        if (session == null) {
            return;
        }

        try {
            String rawRate = req.getParameter("rate");
            if (rawRate == null || rawRate.isBlank()) {
                throw new IllegalArgumentException("Ставка не указана");
            }
            double rate = Double.parseDouble(rawRate.trim().replace(',', '.'));
            service.updateRate(rate);
            resp.sendRedirect(req.getContextPath() + "/admin?saved=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Укажите корректную ставку.");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            showAdmin(req, resp, session);
        } catch (SQLException | IllegalStateException e) {
            log("Не удалось обновить ставку", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось сохранить ставку");
        }
    }

    private HttpSession requireAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/sign-in");
            return null;
        }
        return session;
    }

    private void showAdmin(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        try {
            req.setAttribute("currentRate", service.getRate());
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Cache-Control", "no-store");
            req.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(req, resp);
        } catch (SQLException | IllegalStateException e) {
            log("Не удалось получить ставку", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось получить ставку");
        }
    }
}
