package com.dataplatform.os.repository;

import com.dataplatform.os.entity.StageExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StageExecutionRepository
        extends JpaRepository<StageExecution, Long> {

    List<StageExecution> findByExecutionIdOrderByStartTime(UUID executionId);

    Optional<StageExecution> findTopByExecutionIdOrderByStartTimeDesc(UUID executionId);

    Optional<StageExecution> findTopByExecutionIdAndStageNameOrderByStartTimeDesc(UUID executionId, String stage);
}
