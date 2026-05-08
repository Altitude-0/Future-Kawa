package com.futurekawa.service;

import com.futurekawa.entity.Stock;
import com.futurekawa.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    public Stock createStock(Stock stock) {
        stock.setCreatedAt(LocalDateTime.now());
        stock.setStatus(Stock.Status.COMPLIANT);
        return stockRepository.save(stock);
    }

    public Stock getStockById(UUID id) {
        return stockRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Stock not found: " + id));
    }

    public List<Stock> getStocksByWarehouse(UUID warehouseId) {
        return stockRepository.findByWarehouseIdOrderByCreatedAtAsc(warehouseId);
    }

    public List<Stock> getExpiredStocks() {
        return stockRepository.findExpiredStocks();
    }

    public Long getDaysInStorage(Stock stock) {
        return ChronoUnit.DAYS.between(stock.getCreatedAt(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public void updateStockStatus(UUID stockId, Stock.Status newStatus) {
        Stock stock = getStockById(stockId);
        stock.setStatus(newStatus);
        stock.setUpdatedAt(LocalDateTime.now());
        stockRepository.save(stock);
    }

    public Stock updateStock(UUID id, Stock updatedStock) {
        Stock stock = getStockById(id);
        if (updatedStock.getReference() != null) {
            stock.setReference(updatedStock.getReference());
        }
        if (updatedStock.getStatus() != null) {
            stock.setStatus(updatedStock.getStatus());
        }
        if (updatedStock.getQualityScore() != null) {
            stock.setQualityScore(updatedStock.getQualityScore());
        }
        stock.setUpdatedAt(LocalDateTime.now());
        return stockRepository.save(stock);
    }

    public Stock partialUpdateStock(UUID id, Stock partialStock) {
        Stock stock = getStockById(id);
        if (partialStock.getReference() != null) {
            stock.setReference(partialStock.getReference());
        }
        if (partialStock.getStatus() != null) {
            stock.setStatus(partialStock.getStatus());
        }
        if (partialStock.getQualityScore() != null) {
            stock.setQualityScore(partialStock.getQualityScore());
        }
        stock.setUpdatedAt(LocalDateTime.now());
        return stockRepository.save(stock);
    }

    public void deleteStock(UUID id) {
        if (!stockRepository.existsById(id)) {
            throw new IllegalArgumentException("Stock not found: " + id);
        }
        stockRepository.deleteById(id);
    }
}
