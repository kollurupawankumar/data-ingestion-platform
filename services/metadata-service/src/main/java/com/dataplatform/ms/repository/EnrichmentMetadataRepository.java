package com.dataplatform.ms.repository;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.EnrichmentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrichmentMetadataRepository extends JpaRepository<EnrichmentMetadata, Long> {
    List<EnrichmentMetadata> findByDatasetOrderByStepOrderAsc(Dataset dataset);
}
