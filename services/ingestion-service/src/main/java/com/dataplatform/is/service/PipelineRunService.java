package com.dataplatform.is.service;

import com.dataplatform.is.model.IngestionRequestEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PipelineRunService {

    private final JdbcTemplate jdbcTemplate;

    public PipelineRunService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void startRun(IngestionRequestEvent event) {
        String sql = """
            INSERT INTO pipeline_run(run_id, dataset, load_type, status)
            VALUES (?, ?, ?, 'INGESTION_STARTED')
            ON CONFLICT (run_id) DO NOTHING
        """;

        jdbcTemplate.update(
                sql,
                event.getParams().get("PipelineRunId"),
                event.getDataset(),
                event.getParams().get("LoadType")
        );
    }

    public void markIngested(String runId) {
        String sql = """
            UPDATE pipeline_run
            SET status = 'INGESTION_COMPLETED'
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, runId);
    }

    public void markFailed(String runId) {
        String sql = """
            UPDATE pipeline_run
            SET status = 'INGESTION_FAILED'
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, runId);
    }


    public void markIngestedTriggered(String runId) {
        String sql = """
            UPDATE pipeline_run
            SET status = 'INGESTION_TRIGGERED'
            WHERE run_id = ?
        """;

        jdbcTemplate.update(sql, runId);
    }

    public void updateRawLocation(String runId, String rawLocation) {

        String sql = """
        UPDATE pipeline_run
        SET raw_location = ?, status = 'INGESTION_COMPLETED'
        WHERE run_id = ?
    """;

        jdbcTemplate.update(sql, rawLocation, runId);
    }
}
