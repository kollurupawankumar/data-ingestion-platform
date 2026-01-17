package com.datafabric.common.dto;

import java.util.List;

public class PipelineMetadataDTO {

    private DatasetDTO dataset;

    private SourceMetadataDTO source;

    private List<TransformMetadataDTO> transforms;

    private List<EnrichmentMetadataDTO> enrichments;

    private SinkMetadataDTO sink;

    public DatasetDTO getDataset() {
        return dataset;
    }

    public void setDataset(DatasetDTO dataset) {
        this.dataset = dataset;
    }

    public SourceMetadataDTO getSource() {
        return source;
    }

    public void setSource(SourceMetadataDTO source) {
        this.source = source;
    }

    public List<TransformMetadataDTO> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<TransformMetadataDTO> transforms) {
        this.transforms = transforms;
    }

    public List<EnrichmentMetadataDTO> getEnrichments() {
        return enrichments;
    }

    public void setEnrichments(List<EnrichmentMetadataDTO> enrichments) {
        this.enrichments = enrichments;
    }

    public SinkMetadataDTO getSink() {
        return sink;
    }

    public void setSink(SinkMetadataDTO sink) {
        this.sink = sink;
    }
}
