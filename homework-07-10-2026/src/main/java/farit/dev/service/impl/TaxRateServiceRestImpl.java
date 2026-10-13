package farit.dev.service.impl;

import farit.dev.dto.TaxRateResponse;
import farit.dev.service.TaxRateService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TaxRateServiceRestImpl implements TaxRateService {
    private static final URI TAX_RATE_URI = URI.create(System.getProperty(
            "tax.rate.url", "http://localhost:8080/homework-07-10-2026/api/tax/rate"));

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    @Override
    public TaxRateResponse getTaxRate() {
        HttpRequest request = HttpRequest.newBuilder(TAX_RATE_URI)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("API ставки вернул HTTP " + response.statusCode());
            }
            return new TaxRateResponse(parseRate(response.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Получение ставки прервано", e);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось связаться с API ставки", e);
        }
    }

    private double parseRate(String json) {
        String clean = json.replaceAll("\\s+", "");
        String prefix = "{\"rate\":";
        if (!clean.startsWith(prefix) || !clean.endsWith("}")) {
            throw new IllegalStateException("Неожиданный формат ответа API ставки");
        }
        String number = clean.substring(prefix.length(), clean.length() - 1);
        try {
            double rate = Double.parseDouble(number);
            if (!Double.isFinite(rate) || rate <= 0 || rate >= 100) {
                throw new IllegalStateException("Некорректная ставка из API");
            }
            return rate;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("В ответе API ставка не является числом", e);
        }
    }
}
