package alexander.repository.impl;

import alexander.config.ApplicationProperties;
import alexander.repository.TaxRateRepository;

import java.sql.*;

public class TaxRateRepositoryImpl implements TaxRateRepository {

    private static final String DB_URL = ApplicationProperties.getRequired("db.url");
    private static final String DB_USER = ApplicationProperties.getRequired("db.user");
    private static final String DB_PASSWORD = ApplicationProperties.getRequired("db.password");

    private static final String SQL_FIND = "SELECT rate FROM taxRate WHERE id = 1";
    private static final String SQL_UPDATE = "UPDATE taxRate set rate = ? WHERE id = 1";

    @Override
    public Double findRate() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(SQL_FIND);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("rate");
            }
        }
        throw new IllegalStateException("Не найдено подходящей строки");
    }

    @Override
    public void update(Double tax) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setDouble(1, tax);
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new IllegalStateException("Налоговая ставка не найдена");
            }
        }
    }
}