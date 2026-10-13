package farit.dev.service.impl;

import farit.dev.dto.Payment;
import farit.dev.repository.PaymentRepository;
import farit.dev.service.PaymentService;
import farit.dev.service.TaxRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TaxRateService taxRateService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, TaxRateService taxRateService) {
        this.paymentRepository = paymentRepository;
        this.taxRateService = taxRateService;
    }

    @Override
    public double refreshLastPayment() {
        double rate = taxRateService.getTaxRate().percent();
        paymentRepository.recalculateLastPaymentIfNeeded(rate);
        return rate;
    }

    @Override
    public Payment refreshAndFindLatest() {
        refreshLastPayment();
        return paymentRepository.findLatest();
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public void calculateAndSave(double income) {
        double rate = refreshLastPayment();
        double tax = BigDecimal.valueOf(income)
                .multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .doubleValue();
        paymentRepository.save(income, rate, tax);
    }
}
