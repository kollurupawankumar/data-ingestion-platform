package com.dataplatform.ms.service;

import com.datafabric.common.dto.*;
import com.datafabric.common.utils.JacksonObjectMapper;
import com.dataplatform.ms.domain.*;
import com.dataplatform.ms.dto.PipelineMetadata;
import com.dataplatform.ms.exception.InvalidPipelineException;
import com.dataplatform.ms.repository.*;
import com.dataplatform.ms.utils.utils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PipelineMetadataService {

    private final DatasetRepository datasetRepo;
    private final SourceMetadataRepository sourceRepo;
    private final SinkMetadataRepository sinkRepo;
    private final TransformMetadataRepository transformRepo;
    private final EnrichmentMetadataRepository enrichmentRepo;

    public PipelineMetadataService(DatasetRepository datasetRepo, SourceMetadataRepository sourceRepo,
                                   SinkMetadataRepository sinkRepo, TransformMetadataRepository transformRepo,
                                   EnrichmentMetadataRepository enrichmentRepo) {
        this.datasetRepo = datasetRepo;
        this.sourceRepo = sourceRepo;
        this.sinkRepo = sinkRepo;
        this.transformRepo = transformRepo;
        this.enrichmentRepo = enrichmentRepo;
    }

    public PipelineMetadata getPipeline(Long datasetId) {
        List<TransformMetadataDTO> transformMetadataDTOs = new ArrayList<>();
        List<EnrichmentMetadataDTO> enrichmentMetadataDTOs = new ArrayList<>();
        try{
            Dataset dataset = datasetRepo.findById(datasetId)
                    .orElseThrow(() -> new RuntimeException("Dataset not found"));
            DatasetDTO datasetDTO = utils.convertFromEntity(dataset);

            SourceMetadata source = sourceRepo.findByDataset(dataset)
                    .orElseThrow(() -> new RuntimeException("Source metadata missing"));
            SourceMetadataDTO sourceMetadataDTO = utils.convertFromEntity(source);


            //SinkMetadata sink = sinkRepo.findByDataset(dataset)
            //        .orElseThrow(() -> new RuntimeException("Sink metadata missing"));
            //SinkMetadataDTO sinkMetadataDTO = utils.convertFromEntity(sink);
            SinkMetadataDTO sinkMetadataDTO = null;

                    List<TransformMetadata> transforms =
                    transformRepo.findByDatasetOrderByStepOrderAsc(dataset);
            if (transforms != null){

                for (TransformMetadata transform : transforms){
                    transformMetadataDTOs.add(utils.convertFromEntity(transform));
                }
            }




            List<EnrichmentMetadata> enrichments =
                    enrichmentRepo.findByDatasetOrderByStepOrderAsc(dataset);
            if (enrichments != null){

                for (EnrichmentMetadata enrichmentMetadata : enrichments){
                    enrichmentMetadataDTOs.add(utils.convertFromEntity(enrichmentMetadata));
                }
            }

            return new PipelineMetadata(
                    datasetDTO,
                    sourceMetadataDTO,
                    transformMetadataDTOs,
                    enrichmentMetadataDTOs,
                    sinkMetadataDTO
            );
        }catch (Exception e){
            throw new InvalidPipelineException("Pipeline creation failed.."+e.getMessage());
        }

    }
}
