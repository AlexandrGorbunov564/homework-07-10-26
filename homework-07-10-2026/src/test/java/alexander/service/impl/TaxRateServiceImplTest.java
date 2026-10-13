package alexander.service.impl;

import alexander.repository.TaxRateRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaxRateServiceImplTest {

    private final FakeRepository repository = new FakeRepository();
    private final TaxRateServiceImpl service = new TaxRateServiceImpl(repository);

    @Test
    void validRate() {
        assertTrue(service.isValidRate(13.5));
    }

    @Test
    void negativeRateIsInvalid() {
        assertFalse(service.isValidRate(-5.0));
    }

    @Test
    void tooManyDecimalPlacesIsInvalid() {
        assertFalse(service.isValidRate(13.555));
    }

    @Test
    void getRateReturnsStoredRate() throws Exception {
        assertEquals(13.0, service.getRate().doubleValue());
    }

    @Test
    void updateRateChangesStoredRate() throws Exception {
        service.updateRate(15.0);
        assertEquals(15.0, repository.rate.doubleValue());
    }

    @Test
    void updateRateRejectsInvalidValue() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateRate(101.0));
    }

    // Поддельный репозиторий: не обращается к реальной БД.
    private static class FakeRepository implements TaxRateRepository {
        private Double rate = 13.0;

        @Override
        public Double findRate() {
            return rate;
        }

        @Override
        public void update(Double tax) {
            rate = tax;
        }
    }
}
