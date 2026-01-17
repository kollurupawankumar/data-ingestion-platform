package com.dataplatform.ms.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Entity
@Table(name = "sink_metadata")
public class SinkMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGSERIAL
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entity_id", nullable = false)
    private Dataset dataset;

    @Column(name = "sink_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SinkType sinkType;

    @Column(name = "sink_config", nullable = false, columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private String sinkConfig;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public enum SinkType {
        POSTGRES,
        HIVE,
        S3
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public SinkType getSinkType() {
        return sinkType;
    }

    public void setSinkType(SinkType sinkType) {
        this.sinkType = sinkType;
    }

    public String getSinkConfig() {
        return sinkConfig;
    }

    public void setSinkConfig(String sinkConfig) {
        this.sinkConfig = sinkConfig;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }


}
