package com.futurekawa.service;

import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Stock;
import com.futurekawa.repository.AlertRepository;
import com.futurekawa.strategy.AlertingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertingStrategy alertingStrategy;
    private final StockService stockService;

    public void evaluateStockAlerts(Stock stock) {
        List<Alert> newAlerts = alertingStrategy.evaluateAlerts(stock);

        for (Alert newAlert : newAlerts) {
            // Check if alert already exists
            boolean exists = stock.getAlerts().stream()
                .anyMatch(a -> a.getType() == newAlert.getType());

            if (!exists) {
                alertRepository.save(newAlert);
                stock.getAlerts().add(newAlert);
            }
        }

        // Update stock status based on alerts
        if (!newAlerts.isEmpty()) {
            stockService.updateStockStatus(stock.getId(), Stock.Status.ALERT);
        }
    }

    public List<Alert> getAlertsByStock(UUID stockId) {
        return alertRepository.findByStockIdOrderByAlertedAtDesc(stockId);
    }

    public List<Alert> getUnsentAlerts() {
        return alertRepository.findByEmailSentFalse();
    }

    public void evaluateAllStocks() {
        List<Stock> allStocks = stockService.getAllStocks();
        for (Stock stock : allStocks) {
            evaluateStockAlerts(stock);
        }
    }

    @Transactional(readOnly = true)
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Alert> getAlertById(UUID id) {
        return alertRepository.findById(id);
    }

    public Alert markAlertAsSent(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        alert.setEmailSent(true);
        return alertRepository.save(alert);
    }

    public void deleteAlert(UUID id) {
        if (!alertRepository.existsById(id)) {
            throw new IllegalArgumentException("Alert not found: " + id);
        }
        alertRepository.deleteById(id);
    }
}
