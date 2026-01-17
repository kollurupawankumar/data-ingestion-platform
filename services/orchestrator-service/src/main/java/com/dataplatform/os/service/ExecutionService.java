package com.dataplatform.os.service;

import com.dataplatform.os.entity.JobExecution;
import com.dataplatform.os.entity.StageExecution;
import com.dataplatform.os.repository.JobExecutionRepository;
import com.dataplatform.os.repository.StageExecutionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final StageExecutionRepository stageExecutionRepository;

    public ExecutionService(JobExecutionRepository jobExecutionRepository, StageExecutionRepository stageExecutionRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.stageExecutionRepository = stageExecutionRepository;
    }

    public JobExecution startExecution(Long datasetId) {

        JobExecution job = new JobExecution();
        job.setExecutionId(UUID.randomUUID());
        job.setDatasetId(datasetId);
        job.setStatus("STARTED");
        job.setCurrentStage("INGESTION");
        job.setStartTime(Instant.now());

        return jobExecutionRepository.save(job);
    }

    public void markStageStarted(UUID executionId, String stage) {

        StageExecution stageExecution = new StageExecution();
        stageExecution.setExecutionId(executionId);
        stageExecution.setStageName(stage);
        stageExecution.setStatus("STARTED");
        stageExecution.setStartTime(Instant.now());

        stageExecutionRepository.save(stageExecution);
    }

    public void markStageCompleted(UUID executionId, String stage) {

        StageExecution stageExecution =
                stageExecutionRepository.findTopByExecutionIdOrderByStartTimeDesc(executionId)
                        .orElseThrow();

        stageExecution.setStatus("SUCCESS");
        stageExecution.setEndTime(Instant.now());

        stageExecutionRepository.save(stageExecution);

        JobExecution job = jobExecutionRepository.findById(executionId).orElseThrow();
        job.setCurrentStage(stage);
        jobExecutionRepository.save(job);
    }

    public void markExecutionFailed(UUID executionId, String errorMessage) {

        JobExecution job = jobExecutionRepository.findById(executionId).orElseThrow();
        job.setStatus("FAILED");
        job.setErrorMessage(errorMessage);
        job.setEndTime(Instant.now());

        jobExecutionRepository.save(job);
    }

    public void markExecutionSuccess(UUID executionId) {

        JobExecution job = jobExecutionRepository.findById(executionId).orElseThrow();
        job.setStatus("SUCCESS");
        job.setEndTime(Instant.now());

        jobExecutionRepository.save(job);
    }

    @Transactional
    public void incrementRetry(UUID executionId, String stage) {

        StageExecution stageExec =
                stageExecutionRepository.findTopByExecutionIdAndStageNameOrderByStartTimeDesc(
                                executionId, stage)
                        .orElseThrow();

        stageExec.setRetryCount(stageExec.getRetryCount() + 1);
        stageExecutionRepository.save(stageExec);
    }

}

