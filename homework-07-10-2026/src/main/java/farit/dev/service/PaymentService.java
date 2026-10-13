package farit.dev.service;

import farit.dev.dto.Payment;

public interface PaymentService {

    double refreshLastPayment();

    Payment refreshAndFindLatest();
}
