package alexander.repository;

import java.sql.SQLException;

public interface TaxRateRepository {
    Double findRate() throws SQLException;
    void update(Double tax) throws SQLException;
}
