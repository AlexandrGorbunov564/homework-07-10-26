package alexander.servlet;

import alexander.repository.TaxRateRepository;
import alexander.repository.impl.TaxRateRepositoryImpl;
import alexander.service.TaxRateService;
import alexander.service.impl.TaxRateServiceImpl;
import alexander.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/tax/rate")
public class TaxRateServlet extends HttpServlet {

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
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "no-store");
        try {
            Double rate = service.getRate();
            resp.getWriter().write(JsonUtil.convertRateToJson(rate));
        } catch (SQLException | IllegalStateException e) {
            log("Не удалось получить ставку", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Не удалось получить налоговую ставку\"}");
        }
    }
}
