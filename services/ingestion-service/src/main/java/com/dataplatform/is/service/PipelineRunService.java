package com.dataplatform.is.service;

import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.model.PipelineEvent;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PipelineRunService {

    private final JdbcTemplate jdbcTemplate;

    public PipelineRunService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create pipeline run entry (idempotent)
     */
    public void startRun(IngestionRequestEvent event) {

        String sql = """
            INSERT INTO pipeline_run (
                run_id,
                dataset,
                load_type,
                status,
                started_at,
                last_updated_at
            )
            VALUES (?, ?, ?, 'INGESTION_STARTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT (run_id) DO NOTHING
        """;

        jdbcTemplate.update(
                sql,
                event.getPipelineRunId(),
                event.getDataset(),
                event.getParams().get("LoadType")
        );
    }

    /**
     * Marks ingestion trigger initiated
     */
    public void markIngestedTriggered(String runId) {

        String sql = """
            UPDATE pipeline_run
            SET status = 'INGESTION_TRIGGERED',
                last_updated_at = CURRENT_TIMESTAMP
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, runId);
    }

    public void markIngested(String runId) {

        String sql = """
            UPDATE pipeline_run
            SET status = 'INGESTION_COMPLETED',
                last_updated_at = CURRENT_TIMESTAMP
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, runId);
    }


    /**
     * Marks ingestion completed and updates raw location
     */
    public void updateRawLocation(String runId, String rawLocation) {

        String sql = """
            UPDATE pipeline_run
            SET raw_location = ?,
                status = 'INGESTION_COMPLETED',
                last_updated_at = CURRENT_TIMESTAMP,
                ended_at = CURRENT_TIMESTAMP
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, rawLocation, runId);
    }

    /**
     * Marks pipeline as FAILED with error context
     */
    public void markFailed(String runId, String errorCode, String errorMessage) {

        String sql = """
            UPDATE pipeline_run
            SET status = 'INGESTION_FAILED',
                error_code = ?,
                error_message = ?,
                last_updated_at = CURRENT_TIMESTAMP,
                ended_at = CURRENT_TIMESTAMP
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, errorCode, errorMessage, runId);
    }

    public PipelineEvent getPipelineRunById(String runId) {

        String sql = """
        SELECT run_id,
               dataset,
               load_type,
               status,
               raw_location,
               silver_location,
               gold_location,
               error_code,
               error_message,
               started_at,
               ended_at,
               last_updated_at
        FROM pipeline_run
        WHERE run_id = ?
    """;

        return jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(PipelineEvent.class),
                runId
        );
    }

}
