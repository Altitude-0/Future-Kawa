package com.futurekawa.repository;

import com.futurekawa.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    List<Stock> findByWarehouseIdOrderByCreatedAtAsc(UUID warehouseId);

    List<Stock> findByStatus(Stock.Status status);

    @Query("""
        SELECT s FROM Stock s
        WHERE s.warehouse.id = :warehouseId
        AND s.status = :status
        ORDER BY s.createdAt ASC
    """)
    List<Stock> findByWarehouseAndStatus(UUID warehouseId, Stock.Status status);

    @Query(nativeQuery = true, value = """
        SELECT s.* FROM stocks s
        WHERE s.created_at < CURRENT_TIMESTAMP - INTERVAL '365 days'
    """)
    List<Stock> findExpiredStocks();
}
