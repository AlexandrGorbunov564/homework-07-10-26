package farit.dev.servlet;

import farit.dev.dto.Payment;
import farit.dev.repository.PaymentRepository;
import farit.dev.service.impl.PaymentServiceImpl;
import farit.dev.service.TaxRateService;
import farit.dev.service.impl.TaxRateServiceRestImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/tax", "/api/payments/latest"})
public class TaxServlet extends HttpServlet {

    private PaymentServiceImpl paymentService;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("PostgreSQL Driver не найден", e);
        }
        TaxRateService taxRateService = new TaxRateServiceRestImpl();
        paymentService = new PaymentServiceImpl(new PaymentRepository(), taxRateService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean jsonRequest = "/api/payments/latest".equals(req.getServletPath());
        if (!requireUser(req, resp, jsonRequest)) {
            return;
        }
        if (jsonRequest) {
            sendLatestPaymentJson(resp);
            return;
        }
        fillPage(req, null);
        req.getRequestDispatcher("/WEB-INF/jsp/tax.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/tax".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (!requireUser(req, resp, false)) {
            return;
        }
        req.setCharacterEncoding("UTF-8");
        String incomeParam = req.getParameter("income");
        String error = validateIncome(incomeParam);
        if (error != null) {
            fillPage(req, error);
            req.getRequestDispatcher("/WEB-INF/jsp/tax.jsp").forward(req, resp);
            return;
        }

        double income = Double.parseDouble(incomeParam.replace(",", "."));
        try {
            paymentService.calculateAndSave(income);
            resp.sendRedirect(req.getContextPath() + "/tax");
        } catch (Exception e) {
            log("Не удалось рассчитать и сохранить платёж", e);
            fillPage(req, "Не удалось рассчитать налог или сохранить платёж");
            req.getRequestDispatcher("/WEB-INF/jsp/tax.jsp").forward(req, resp);
        }
    }

    private void sendLatestPaymentJson(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        try {
            Payment payment = paymentService.refreshAndFindLatest();
            if (payment == null) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }
            String date = payment.createdAt() == null ? "null" : "\"" + payment.createdAt() + "\"";
            String json = "{\"id\":" + payment.id()
                    + ",\"income\":" + payment.income()
                    + ",\"percent\":" + payment.percent()
                    + ",\"tax\":" + payment.tax()
                    + ",\"createdAt\":" + date + "}";
            resp.getWriter().write(json);
        } catch (Exception e) {
            log("Не удалось синхронизировать последний платёж", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Не удалось получить платёж\"}");
        }
    }

    private boolean requireUser(HttpServletRequest req, HttpServletResponse resp, boolean jsonRequest) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"USER".equals(session.getAttribute("role"))
                || !(session.getAttribute("username") instanceof String username)
                || username.isBlank()) {
            if (jsonRequest) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{\"error\":\"Требуется авторизация\"}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/sign-in");
            }
            return false;
        }
        return true;
    }

    private void fillPage(HttpServletRequest req, String error) {
        req.setAttribute("error", error);
        try {
            double rate = paymentService.refreshLastPayment();
            req.setAttribute("percent", rate);
        } catch (Exception e) {
            log("Не удалось получить ставку / обновить платёж", e);
            req.setAttribute("percent", null);
            if (error == null) {
                req.setAttribute("error", "Не удалось получить налоговую ставку");
            }
        }
        try {
            req.setAttribute("payments", paymentService.findAll());
        } catch (Exception e) {
            log("Не удалось загрузить историю", e);
            req.setAttribute("payments", java.util.List.of());
            if (req.getAttribute("error") == null) {
                req.setAttribute("error", "Нет подключения к базе данных");
            }
        }
    }

    private String validateIncome(String incomeParam) {
        if (incomeParam == null || incomeParam.isBlank()) {
            return "Введите сумму дохода";
        }
        try {
            double income = Double.parseDouble(incomeParam.replace(",", "."));
            if (!Double.isFinite(income)) {
                return "Сумма должна быть конечным числом";
            }
            if (income <= 0) {
                return "Сумма должна быть больше 0";
            }
            if (income > 1_000_000_000) {
                return "Слишком большая сумма";
            }
        } catch (NumberFormatException e) {
            return "Сумма должна быть числом";
        }
        return null;
    }
}
