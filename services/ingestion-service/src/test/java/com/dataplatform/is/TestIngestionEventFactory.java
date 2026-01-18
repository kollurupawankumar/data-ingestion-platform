package com.dataplatform.is;

import com.dataplatform.is.model.IngestionRequestEvent;

import java.util.HashMap;
import java.util.Map;

public class TestIngestionEventFactory {

    public static IngestionRequestEvent validDbEvent() {
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setDataset("customer");
        event.setSourceType("DB");
        event.setPipelineRunId("run-001");

        Map<String, String> params = new HashMap<>();
        params.put("table", "customer");
        params.put("fetchSize", "1000");

        event.setParams(params);
        return event;
    }
}

