package com.dataplatform.ms.service;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.SourceMetadata;
import com.dataplatform.ms.repository.DatasetRepository;
import com.dataplatform.ms.repository.SourceMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SourceMetadataService {

    private final SourceMetadataRepository sourceRepo;
    private final DatasetRepository datasetRepo;

    public SourceMetadataService(SourceMetadataRepository sourceRepo, DatasetRepository datasetRepo) {
        this.sourceRepo = sourceRepo;
        this.datasetRepo = datasetRepo;
    }

    public SourceMetadata createSource(Long datasetId, SourceMetadata.SourceType type, String config) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));

        Optional<SourceMetadata> existing = sourceRepo.findByDataset(dataset);
        if (existing.isPresent()) {
            throw new RuntimeException("Source already exists for dataset: " + datasetId);
        }

        SourceMetadata source = new SourceMetadata();
        source.setDataset(dataset);
        source.setSourceType(type);
        source.setSourceConfig(config);

        return sourceRepo.save(source);
    }

    public Optional<SourceMetadata> getByDataset(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));
        return sourceRepo.findByDataset(dataset);
    }

    public List<SourceMetadata> getAllSources(Long datasetId) {
        return sourceRepo.findAllByDatasetId(datasetId);
    }
}
