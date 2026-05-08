package com.futurekawa.service;

import com.futurekawa.entity.Stock;
import com.futurekawa.entity.Warehouse;
import com.futurekawa.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock testStock;
    private Warehouse testWarehouse;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testWarehouse = Warehouse.builder()
            .id(UUID.randomUUID())
            .name("Test Warehouse")
            .idealTemperature(22.0f)
            .idealHumidity(60.0f)
            .toleranceTemperature(3.0f)
            .toleranceHumidity(2.0f)
            .build();

        testStock = Stock.builder()
            .id(testId)
            .warehouse(testWarehouse)
            .reference("TEST-001")
            .status(Stock.Status.COMPLIANT)
            .qualityScore(8.5f)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void testCreateStock_Success() {
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        Stock result = stockService.createStock(testStock);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getReference()).isEqualTo("TEST-001");
        assertThat(result.getStatus()).isEqualTo(Stock.Status.COMPLIANT);
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testGetStockById_Success() {
        when(stockRepository.findById(testId)).thenReturn(Optional.of(testStock));

        Stock result = stockService.getStockById(testId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getReference()).isEqualTo("TEST-001");
        verify(stockRepository, times(1)).findById(testId);
    }

    @Test
    void testGetStockById_NotFound() {
        when(stockRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.getStockById(testId))
            .isInstanceOf(RuntimeException.class);
        verify(stockRepository, times(1)).findById(testId);
    }

    @Test
    void testGetAllStocks_Success() {
        Stock stock2 = Stock.builder()
            .id(UUID.randomUUID())
            .warehouse(testWarehouse)
            .reference("TEST-002")
            .status(Stock.Status.ALERT)
            .qualityScore(7.0f)
            .build();

        List<Stock> stocks = Arrays.asList(testStock, stock2);
        when(stockRepository.findAll()).thenReturn(stocks);

        List<Stock> result = stockService.getAllStocks();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testStock, stock2);
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void testUpdateStock_Success() {
        Stock updatedStock = Stock.builder()
            .id(testId)
            .warehouse(testWarehouse)
            .reference("TEST-001-UPDATED")
            .status(Stock.Status.ALERT)
            .qualityScore(7.5f)
            .createdAt(testStock.getCreatedAt())
            .build();

        when(stockRepository.findById(testId)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(updatedStock);

        Stock result = stockService.updateStock(testId, updatedStock);

        assertThat(result).isNotNull();
        assertThat(result.getReference()).isEqualTo("TEST-001-UPDATED");
        assertThat(result.getStatus()).isEqualTo(Stock.Status.ALERT);
        assertThat(result.getQualityScore()).isEqualTo(7.5f);
        verify(stockRepository, times(1)).findById(testId);
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testPartialUpdateStock_Success() {
        Stock partialUpdate = Stock.builder()
            .status(Stock.Status.EXPIRED)
            .build();

        when(stockRepository.findById(testId)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        Stock result = stockService.partialUpdateStock(testId, partialUpdate);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Stock.Status.EXPIRED);
        verify(stockRepository, times(1)).findById(testId);
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testDeleteStock_Success() {
        when(stockRepository.existsById(testId)).thenReturn(true);

        stockService.deleteStock(testId);

        verify(stockRepository, times(1)).existsById(testId);
        verify(stockRepository, times(1)).deleteById(testId);
    }

    @Test
    void testDeleteStock_NotFound() {
        when(stockRepository.existsById(testId)).thenReturn(false);

        assertThatThrownBy(() -> stockService.deleteStock(testId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(stockRepository, times(1)).existsById(testId);
    }
}
