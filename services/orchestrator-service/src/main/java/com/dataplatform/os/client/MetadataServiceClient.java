package com.dataplatform.os.client;

import com.datafabric.common.dto.PipelineMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "metadata-service",
        url = "${orchestrator.metadata-service.base-url}"
)
public interface MetadataServiceClient {

    @GetMapping("/metadata/pipelines/{datasetId}")
    PipelineMetadataDTO getPipelineMetadata(@PathVariable("datasetId") Long datasetId);
}