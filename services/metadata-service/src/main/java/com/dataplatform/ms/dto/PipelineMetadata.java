package com.dataplatform.ms.dto;

import com.datafabric.common.dto.*;
import com.datafabric.common.utils.JacksonObjectMapper;
import com.dataplatform.ms.domain.*;

import java.util.ArrayList;
import java.util.List;

public class PipelineMetadata {




    private DatasetDTO dataset = new DatasetDTO();
    private SourceMetadataDTO source = new SourceMetadataDTO();
    private List<TransformMetadataDTO> transforms = new ArrayList<>();
    private List<EnrichmentMetadataDTO> enrichments = new ArrayList<>();
    private SinkMetadataDTO sink = new SinkMetadataDTO();

    public PipelineMetadata(DatasetDTO dataset, SourceMetadataDTO source, List<TransformMetadataDTO> transforms,
                            List<EnrichmentMetadataDTO> enrichments, SinkMetadataDTO sink) {

        this.dataset = dataset;
        this.source = source;
        this.transforms = transforms;
        this.enrichments = enrichments;
        this.sink = sink;
    }

    public DatasetDTO getDataset() {
        return dataset;
    }

    public SourceMetadataDTO getSource() {
        return source;
    }

    public List<TransformMetadataDTO> getTransforms() {
        return transforms;
    }

    public List<EnrichmentMetadataDTO> getEnrichments() {
        return enrichments;
    }

    public SinkMetadataDTO getSink() {
        return sink;
    }
}