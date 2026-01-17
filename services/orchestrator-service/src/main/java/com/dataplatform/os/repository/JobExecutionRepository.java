package com.dataplatform.os.repository;

import com.dataplatform.os.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobExecutionRepository
        extends JpaRepository<JobExecution, UUID> {

    List<JobExecution> findByDatasetIdOrderByStartTimeDesc(Long datasetId);

    Optional<JobExecution> findTopByDatasetIdOrderByStartTimeDesc(Long datasetId);
}

