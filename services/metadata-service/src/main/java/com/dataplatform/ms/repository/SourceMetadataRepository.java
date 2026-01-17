package com.dataplatform.ms.repository;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.SourceMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SourceMetadataRepository extends JpaRepository<SourceMetadata, UUID> {
    Optional<SourceMetadata> findByDataset(Dataset dataset);
    List<SourceMetadata> findAllByDatasetId(Long datasetId);
}