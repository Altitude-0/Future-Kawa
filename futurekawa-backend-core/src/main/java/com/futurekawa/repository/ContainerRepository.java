package com.futurekawa.repository;

import com.futurekawa.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContainerRepository extends JpaRepository<Container, UUID> {

    List<Container> findByWarehouseIdOrderByEntryDateAsc(UUID warehouseId);

    List<Container> findByStatus(Container.Status status);

    @Query("""
        SELECT c FROM Container c
        WHERE c.warehouse.id = :warehouseId
        AND c.status = :status
        ORDER BY c.entryDate ASC
    """)
    List<Container> findByWarehouseAndStatus(UUID warehouseId, Container.Status status);

    @Query(nativeQuery = true, value = """
        SELECT c.* FROM containers c
        WHERE c.entry_date < CURRENT_TIMESTAMP - INTERVAL '365 days'
    """)
    List<Container> findExpiredContainers();
}
