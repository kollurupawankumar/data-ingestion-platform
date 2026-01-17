package com.dataplatform.ms.repository;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.SinkMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SinkMetadataRepository extends JpaRepository<SinkMetadata, Long> {
    Optional<SinkMetadata> findByDataset(Dataset dataset);
    List<SinkMetadata> findAllByDataset(Dataset dataset);
}