package com.futurekawa.repository;

import com.futurekawa.entity.ConfigurationAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigurationAuditRepository extends JpaRepository<ConfigurationAudit, UUID> {

    List<ConfigurationAudit> findByConfigurationIdOrderByChangedAtDesc(UUID configurationId);
}
