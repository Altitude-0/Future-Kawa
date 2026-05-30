package com.futurekawa.service;

import com.futurekawa.entity.Container;
import com.futurekawa.entity.Warehouse;
import com.futurekawa.repository.ContainerRepository;
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
class ContainerServiceTest {

    @Mock
    private ContainerRepository containerRepository;

    @InjectMocks
    private ContainerService containerService;

    private Container testContainer;
    private Warehouse testWarehouse;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testWarehouse = Warehouse.builder()
            .id(UUID.randomUUID())
            .name("Test Warehouse")
            .build();

        testContainer = Container.builder()
            .id(testId)
            .warehouse(testWarehouse)
            .status(Container.Status.compliant)
            .entryDate(LocalDateTime.now())
            .build();
    }

    @Test
    void testCreateContainer_Success() {
        when(containerRepository.save(any(Container.class))).thenReturn(testContainer);

        Container result = containerService.createContainer(testContainer);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getStatus()).isEqualTo(Container.Status.compliant);
        verify(containerRepository, times(1)).save(any(Container.class));
    }

    @Test
    void testGetContainerById_Success() {
        when(containerRepository.findById(testId)).thenReturn(Optional.of(testContainer));

        Container result = containerService.getContainerById(testId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        verify(containerRepository, times(1)).findById(testId);
    }

    @Test
    void testGetContainerById_NotFound() {
        when(containerRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> containerService.getContainerById(testId))
            .isInstanceOf(RuntimeException.class);
        verify(containerRepository, times(1)).findById(testId);
    }

    @Test
    void testGetAllContainers_Success() {
        Container container2 = Container.builder()
            .id(UUID.randomUUID())
            .warehouse(testWarehouse)
            .status(Container.Status.warning)
            .build();

        List<Container> containers = Arrays.asList(testContainer, container2);
        when(containerRepository.findAll()).thenReturn(containers);

        List<Container> result = containerService.getAllContainers();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testContainer, container2);
        verify(containerRepository, times(1)).findAll();
    }

    @Test
    void testUpdateContainer_Success() {
        Container updatedContainer = Container.builder()
            .id(testId)
            .warehouse(testWarehouse)
            .status(Container.Status.warning)
            .entryDate(testContainer.getEntryDate())
            .build();

        when(containerRepository.findById(testId)).thenReturn(Optional.of(testContainer));
        when(containerRepository.save(any(Container.class))).thenReturn(updatedContainer);

        Container result = containerService.updateContainer(testId, updatedContainer);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Container.Status.warning);
        verify(containerRepository, times(1)).findById(testId);
        verify(containerRepository, times(1)).save(any(Container.class));
    }

    @Test
    void testDeleteContainer_Success() {
        when(containerRepository.existsById(testId)).thenReturn(true);

        containerService.deleteContainer(testId);

        verify(containerRepository, times(1)).existsById(testId);
        verify(containerRepository, times(1)).deleteById(testId);
    }
}
