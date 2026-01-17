package com.dataplatform.ms.repository;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.TransformMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransformMetadataRepository extends JpaRepository<TransformMetadata, Long> {
    List<TransformMetadata> findByDatasetOrderByStepOrderAsc(Dataset dataset);
}