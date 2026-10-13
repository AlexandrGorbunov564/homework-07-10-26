package alexander.service.impl;

import alexander.repository.TaxRateRepository;
import alexander.service.TaxRateService;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TaxRateServiceImpl implements TaxRateService {

    private final TaxRateRepository repository;

    public TaxRateServiceImpl(TaxRateRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValidRate(Double rate) {
        if (rate == null || !Double.isFinite(rate) || rate <= 0 || rate >= 100) {
            return false;
        }
        return BigDecimal.valueOf(rate).stripTrailingZeros().scale() <= 2;
    }

    @Override
    public Double getRate() throws SQLException {
        Double rate = repository.findRate();
        if (!isValidRate(rate)) {
            throw new IllegalStateException("Ставка в БД некорректна");
        }
        return rate;
    }

    @Override
    public void updateRate(double rate) throws SQLException {
        if (!isValidRate(rate)) {
            throw new IllegalArgumentException("Введено некорректное значение ставки");
        }
        repository.update(rate);
    }
}
