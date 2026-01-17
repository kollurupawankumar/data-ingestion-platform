package com.dataplatform.ms.service;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.TransformMetadata;
import com.dataplatform.ms.repository.DatasetRepository;
import com.dataplatform.ms.repository.TransformMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransformMetadataService {

    private final TransformMetadataRepository transformRepo;
    private final DatasetRepository datasetRepo;

    public TransformMetadataService(TransformMetadataRepository transformRepo, DatasetRepository datasetRepo) {
        this.transformRepo = transformRepo;
        this.datasetRepo = datasetRepo;
    }

    public TransformMetadata addTransform(Long datasetId, Integer stepOrder, String ruleType, String ruleConfig) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));

        TransformMetadata transform = new TransformMetadata();
        transform.setDataset(dataset);
        transform.setStepOrder(stepOrder);
        transform.setRuleType(ruleType);
        transform.setRuleConfig(ruleConfig);

        return transformRepo.save(transform);
    }

    public List<TransformMetadata> getTransforms(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));
        return transformRepo.findByDatasetOrderByStepOrderAsc(dataset);
    }
}
