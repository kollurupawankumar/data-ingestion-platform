package com.dataplatform.ms.utils;

import com.datafabric.common.dto.*;
import com.dataplatform.ms.domain.*;

public class utils {

    public static DatasetDTO convertFromEntity(Dataset dataset) {
        DatasetDTO datasetDTO = new DatasetDTO();
        datasetDTO.setId(dataset.getId());
        datasetDTO.setDescription(dataset.getDescription());
        datasetDTO.setName(dataset.getSubjectArea().getName());
        datasetDTO.setEnabled(dataset.isEnabled());
        return datasetDTO;
    }

    public static SourceMetadataDTO convertFromEntity(SourceMetadata sourceMetadata){
        SourceMetadataDTO sourceMetadataDTO = new SourceMetadataDTO();
        sourceMetadataDTO.setSourceConfig(sourceMetadata.getSourceConfig());
        sourceMetadataDTO.setSourceType(sourceMetadata.getSourceType().name());
        return sourceMetadataDTO;
    }

    public static SinkMetadataDTO convertFromEntity(SinkMetadata sinkMetadata) {
        SinkMetadataDTO sinkMetadataDTO = new SinkMetadataDTO();
        sinkMetadataDTO.setSinkConfig(sinkMetadata.getSinkConfig());
        sinkMetadataDTO.setSinkType(sinkMetadata.getSinkType().name());
        return sinkMetadataDTO;
    }

    public static TransformMetadataDTO convertFromEntity(TransformMetadata metadata) {
        TransformMetadataDTO transformMetadataDTO = new TransformMetadataDTO();
        transformMetadataDTO.setRuleConfig(metadata.getRuleConfig());
        transformMetadataDTO.setRuleType(metadata.getRuleType());
        transformMetadataDTO.setStepOrder(metadata.getStepOrder());
        return transformMetadataDTO;
    }

    public static EnrichmentMetadataDTO convertFromEntity(EnrichmentMetadata metadata) {
        EnrichmentMetadataDTO enrichmentMetadataDTO = new EnrichmentMetadataDTO();
        enrichmentMetadataDTO.setRuleConfig(metadata.getRuleConfig());
        enrichmentMetadataDTO.setRuleType(metadata.getRuleType());
        enrichmentMetadataDTO.setStepOrder(metadata.getStepOrder());
        return enrichmentMetadataDTO;
    }
}
