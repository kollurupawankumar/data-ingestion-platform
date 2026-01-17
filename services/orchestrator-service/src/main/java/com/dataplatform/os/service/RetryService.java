package com.dataplatform.os.service;

import com.dataplatform.os.dto.RetryDecision;
import com.dataplatform.os.dto.RetryPlan;
import com.dataplatform.os.entity.StageExecution;
import com.dataplatform.os.repository.StageExecutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RetryService {

    @Value("${orchestrator.retry.max-attempts}")
    private int maxAttempts;

    private final StageExecutionRepository stageRepo;
    private final BackoffService backoffService;

    public RetryService(StageExecutionRepository stageRepo, BackoffService backoffService) {
        this.stageRepo = stageRepo;
        this.backoffService = backoffService;
    }

    public RetryPlan decideRetry(UUID executionId,
                                 String stage,
                                 String errorType) {

        if ("PERMANENT".equalsIgnoreCase(errorType)) {
            return RetryPlan.noRetry();
        }

        int attempts =
                stageRepo.findTopByExecutionIdAndStageNameOrderByStartTimeDesc(
                                executionId, stage)
                        .map(StageExecution::getRetryCount)
                        .orElse(0);

        if (attempts >= maxAttempts) {
            return RetryPlan.noRetry();
        }

        Duration delay =
                backoffService.getBackoffDelay(attempts + 1);

        return RetryPlan.retryWithDelay(delay);
    }
}
