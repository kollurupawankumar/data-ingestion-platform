package com.dataplatform.is.service;


import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.model.PipelineState;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PipelineRunTrackingService {

    private final JdbcTemplate jdbcTemplate;

    public PipelineRunTrackingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create run + initial state (idempotent)
     */
    @Transactional
    public void startRun(IngestionRequestEvent event) {

        jdbcTemplate.update("""
            INSERT INTO pipeline_run (
                run_id,
                dataset,
                load_type,
                status,
                started_at,
                last_updated_at
            )
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT (run_id) DO NOTHING
        """,
                event.getPipelineRunId(),
                event.getDataset(),
                event.getSourceConfig().get("loadType"),
                PipelineState.INGESTION_STARTED.name()
        );

        jdbcTemplate.update("""
            INSERT INTO pipeline_run_state (
                run_id,
                state,
                state_started_at
            )
            SELECT ?, ?, CURRENT_TIMESTAMP
            WHERE NOT EXISTS (
                SELECT 1 FROM pipeline_run_state
                WHERE run_id = ?
            )
        """,
                event.getPipelineRunId(),
                PipelineState.INGESTION_STARTED.name(),
                event.getPipelineRunId()
        );
    }

    /**
     * Transition pipeline to a new state
     */
    @Transactional
    public void transitionState(String runId, PipelineState newState) {

        // 1. Close current state
        jdbcTemplate.update("""
            UPDATE pipeline_run_state
            SET state_ended_at = CURRENT_TIMESTAMP,
                duration_ms =
                    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - state_started_at)) * 1000
            WHERE run_id = ?
              AND state_ended_at IS NULL
        """, runId);

        // 2. Insert new state
        jdbcTemplate.update("""
            INSERT INTO pipeline_run_state (
                run_id,
                state,
                state_started_at
            )
            VALUES (?, ?, CURRENT_TIMESTAMP)
        """, runId, newState.name());

        // 3. Update summary table
        jdbcTemplate.update("""
            UPDATE pipeline_run
            SET status = ?,
                last_updated_at = CURRENT_TIMESTAMP,
                ended_at = CASE
                    WHEN ? IN ('COMPLETED', 'FAILED')
                    THEN CURRENT_TIMESTAMP
                    ELSE ended_at
                END
            WHERE run_id = ?
        """,
                newState.name(),
                newState.name(),
                runId
        );
    }

    /**
     * Mark run as failed with error details
     */
    @Transactional
    public void failRun(
            String runId,
            String errorCode,
            String errorMessage
    ) {
        transitionState(runId, PipelineState.FAILED);

        jdbcTemplate.update("""
            UPDATE pipeline_run
            SET error_code = ?,
                error_message = ?
            WHERE run_id = ?
        """, errorCode, errorMessage, runId);
    }





}

