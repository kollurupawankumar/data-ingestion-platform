package com.dataplatform.ms.service;


import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.EnrichmentMetadata;
import com.dataplatform.ms.repository.DatasetRepository;
import com.dataplatform.ms.repository.EnrichmentMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrichmentMetadataService {

    private final EnrichmentMetadataRepository enrichRepo;
    private final DatasetRepository datasetRepo;

    public EnrichmentMetadataService(EnrichmentMetadataRepository enrichRepo, DatasetRepository datasetRepo) {
        this.enrichRepo = enrichRepo;
        this.datasetRepo = datasetRepo;
    }

    public EnrichmentMetadata addEnrichment(Long datasetId, Integer stepOrder, String ruleType, String ruleConfig) {
        Dataset dataset = datasetRepo.findById((datasetId))
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));

        EnrichmentMetadata enrichment = new EnrichmentMetadata();
        enrichment.setDataset(dataset);
        enrichment.setStepOrder(stepOrder);
        enrichment.setRuleType(ruleType);
        enrichment.setRuleConfig(ruleConfig);

        return enrichRepo.save(enrichment);
    }

    public List<EnrichmentMetadata> getEnrichments(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));
        return enrichRepo.findByDatasetOrderByStepOrderAsc(dataset);
    }
}

