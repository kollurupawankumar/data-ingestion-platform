package com.dataplatform.os.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class BackoffService {

    private final List<Integer> backoffSeconds;

    public BackoffService(
            @Value("${orchestrator.retry.backoff-seconds}") List<Integer> backoffSeconds
    ) {
        this.backoffSeconds = backoffSeconds;
    }

    public Duration getBackoffDelay(int retryCount) {

        if (retryCount <= 0) {
            return Duration.ZERO;
        }

        int index = retryCount - 1;

        if (index >= backoffSeconds.size()) {
            index = backoffSeconds.size() - 1;
        }

        return Duration.ofSeconds(backoffSeconds.get(index));
    }
}
