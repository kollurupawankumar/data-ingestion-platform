package com.dataplatform.os.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StateTransitionService {

    public Optional<String> nextStage(String currentStage) {

        return switch (currentStage) {
            case "INGESTION" -> Optional.of("TRANSFORMATION");
            case "TRANSFORMATION" -> Optional.of("ENRICHMENT");
            case "ENRICHMENT" -> Optional.of("SINK");
            default -> Optional.empty();
        };
    }
}

