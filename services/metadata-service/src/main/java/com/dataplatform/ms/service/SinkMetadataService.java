package com.dataplatform.ms.service;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.SinkMetadata;
import com.dataplatform.ms.repository.DatasetRepository;
import com.dataplatform.ms.repository.SinkMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SinkMetadataService {
    private final SinkMetadataRepository sinkRepo;
    private final DatasetRepository datasetRepo;

    public SinkMetadataService(SinkMetadataRepository sinkRepo, DatasetRepository datasetRepo) {
        this.sinkRepo = sinkRepo;
        this.datasetRepo = datasetRepo;
    }

    public SinkMetadata createSink(Long datasetId, SinkMetadata.SinkType type, String config) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));

        Optional<SinkMetadata> existing = sinkRepo.findByDataset(dataset);
        if (existing.isPresent()) {
            throw new RuntimeException("Sink already exists for dataset: " + datasetId);
        }

        SinkMetadata sink = new SinkMetadata();
        sink.setDataset(dataset);
        sink.setSinkType(type);
        sink.setSinkConfig(config);

        return sinkRepo.save(sink);
    }

    public Optional<SinkMetadata> getByDataset(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));
        return sinkRepo.findByDataset(dataset);
    }

    public List<SinkMetadata> getAllSinks(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));
        return sinkRepo.findAllByDataset(dataset);
    }
}
