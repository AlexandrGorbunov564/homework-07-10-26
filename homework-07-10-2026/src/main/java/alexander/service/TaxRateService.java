package alexander.service;

import java.sql.SQLException;

public interface TaxRateService {
    boolean isValidRate(Double rate);
    Double getRate() throws SQLException;
    void updateRate(double rate) throws SQLException;
}
