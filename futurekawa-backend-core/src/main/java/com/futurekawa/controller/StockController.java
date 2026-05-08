package com.futurekawa.controller;

import com.futurekawa.dto.StockDTO;
import com.futurekawa.entity.Stock;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Tag(name = "Stocks", description = "Coffee lot management endpoints")
public class StockController {

    private final StockService stockService;
    private final EntityMapper mapper;

    @PostMapping
    @Operation(summary = "Create a new stock lot")
    public ResponseEntity<StockDTO> createStock(@Valid @RequestBody Stock stock) {
        Stock created = stockService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toStockDTO(created));
    }

    @GetMapping
    @Operation(summary = "List all stocks")
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks.stream().map(mapper::toStockDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a stock by ID")
    public ResponseEntity<StockDTO> getStockById(@PathVariable UUID id) {
        try {
            Stock stock = stockService.getStockById(id);
            return ResponseEntity.ok(mapper.toStockDTO(stock));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace entire stock (PUT - full replacement)")
    public ResponseEntity<StockDTO> updateStockFull(
            @PathVariable UUID id,
            @Valid @RequestBody Stock updatedStock) {
        try {
            Stock stock = stockService.updateStock(id, updatedStock);
            return ResponseEntity.ok(mapper.toStockDTO(stock));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update stock (PATCH - partial update)")
    public ResponseEntity<StockDTO> updateStockPartial(
            @PathVariable UUID id,
            @RequestBody Stock partialStock) {
        try {
            Stock stock = stockService.partialUpdateStock(id, partialStock);
            return ResponseEntity.ok(mapper.toStockDTO(stock));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a stock")
    public ResponseEntity<Void> deleteStock(@PathVariable UUID id) {
        try {
            stockService.deleteStock(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
