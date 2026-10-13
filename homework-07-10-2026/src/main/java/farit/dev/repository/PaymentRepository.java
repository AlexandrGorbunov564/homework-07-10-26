package farit.dev.repository;

import alexander.config.ApplicationProperties;
import farit.dev.dto.Payment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private static final String DB_URL = ApplicationProperties.getRequired("db.url");
    private static final String DB_USER = ApplicationProperties.getRequired("db.user");
    private static final String DB_PASSWORD = ApplicationProperties.getRequired("db.password");

    public void save(double income, double percent, double tax) {
        String sql = "insert into payments (income, percent, tax) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, income);
            ps.setDouble(2, percent);
            ps.setDouble(3, tax);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("не получилось сохранить оплату", e);
        }
    }

    public void recalculateLastPaymentIfNeeded(double currentRate) {
        String sql = """
                UPDATE payments
                SET percent = ?, tax = ROUND(income * CAST(? AS NUMERIC) / 100, 2)
                WHERE id = (SELECT id FROM payments ORDER BY id DESC LIMIT 1)
                  AND percent IS DISTINCT FROM CAST(? AS NUMERIC)
                """;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            BigDecimal rate = BigDecimal.valueOf(currentRate);
            ps.setBigDecimal(1, rate);
            ps.setBigDecimal(2, rate);
            ps.setBigDecimal(3, rate);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Не получилось пересчитать последний платёж", e);
        }
    }

    public Payment findLatest() {
        String sql = "select id, income, percent, tax, created_at from payments order by id desc limit 1";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? readPayment(rs) : null;
        } catch (SQLException e) {
            throw new IllegalStateException("Не получилось прочитать последний платёж", e);
        }
    }

    public List<Payment> findAll() {
        String sql = "select id, income, percent, tax, created_at from payments order by id desc";
        List<Payment> payments = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                payments.add(readPayment(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не получилось прочитать историю оплат", e);
        }
        return payments;
    }

    private Payment readPayment(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getLong("id"),
                rs.getDouble("income"),
                rs.getDouble("percent"),
                rs.getDouble("tax"),
                rs.getTimestamp("created_at")
        );
    }
}
