package com.futurekawa.service;

import com.futurekawa.entity.Container;
import com.futurekawa.repository.ContainerRepository;
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
public class ContainerService {

    private final ContainerRepository containerRepository;

    public Container createContainer(Container container) {
        if (container.getEntryDate() == null) {
            container.setEntryDate(LocalDateTime.now());
        }
        if (container.getStatus() == null) {
            container.setStatus(Container.Status.COMPLIANT);
        }
        return containerRepository.save(container);
    }

    public Container getContainerById(UUID id) {
        return containerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Container not found: " + id));
    }

    public List<Container> getContainersByWarehouse(UUID warehouseId) {
        return containerRepository.findByWarehouseIdOrderByEntryDateAsc(warehouseId);
    }

    public List<Container> getExpiredContainers() {
        return containerRepository.findExpiredContainers();
    }

    public Long getDaysInStorage(Container container) {
        return ChronoUnit.DAYS.between(container.getEntryDate(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Container> getAllContainers() {
        return containerRepository.findAll();
    }

    public void updateContainerStatus(UUID containerId, Container.Status newStatus) {
        Container container = getContainerById(containerId);
        container.setStatus(newStatus);
        containerRepository.save(container);
    }

    public Container updateContainer(UUID id, Container updatedContainer) {
        Container container = getContainerById(id);
        if (updatedContainer.getStatus() != null) {
            container.setStatus(updatedContainer.getStatus());
        }
        if (updatedContainer.getExitDate() != null) {
            container.setExitDate(updatedContainer.getExitDate());
        }
        return containerRepository.save(container);
    }

    public void deleteContainer(UUID id) {
        if (!containerRepository.existsById(id)) {
            throw new IllegalArgumentException("Container not found: " + id);
        }
        containerRepository.deleteById(id);
    }
}
