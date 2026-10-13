package farit.dev.service.impl;

import farit.dev.dto.Payment;
import farit.dev.dto.TaxRateResponse;
import farit.dev.repository.PaymentRepository;
import farit.dev.service.TaxRateService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentServiceImplTest {

    private final FakePaymentRepository repository = new FakePaymentRepository();
    private final TaxRateService taxRateService = () -> new TaxRateResponse(13.0);
    private final PaymentServiceImpl service = new PaymentServiceImpl(repository, taxRateService);

    @Test
    void refreshReturnsCurrentRate() {
        assertEquals(13.0, service.refreshLastPayment());
    }

    @Test
    void refreshPassesRateToRepository() {
        service.refreshLastPayment();
        assertEquals(13.0, repository.lastRecalculatedRate.doubleValue());
    }

    @Test
    void refreshAndFindLatestReturnsLastPayment() {
        repository.latestPayment = new Payment(3L, 1000.0, 13.0, 130.0, null);
        assertEquals(3L, service.refreshAndFindLatest().id().longValue());
    }

    @Test
    void latestPaymentMayBeAbsent() {
        assertNull(service.refreshAndFindLatest());
    }

    @Test
    void calculateAndSaveUsesCurrentRate() {
        service.calculateAndSave(1000.0);
        assertEquals(130.0, repository.lastSavedTax.doubleValue());
    }

    // Поддельный репозиторий: в тестах не открывает соединения с PostgreSQL.
    private static class FakePaymentRepository extends PaymentRepository {
        private Double lastRecalculatedRate;
        private Double lastSavedTax;
        private Payment latestPayment;

        @Override
        public void recalculateLastPaymentIfNeeded(double currentRate) {
            lastRecalculatedRate = currentRate;
        }

        @Override
        public Payment findLatest() {
            return latestPayment;
        }

        @Override
        public void save(double income, double percent, double tax) {
            lastSavedTax = tax;
        }
    }
}
