package com.dataplatform.os.dto;

import java.time.Duration;

public class RetryPlan {

    private final boolean retry;
    private final Duration delay;

    private RetryPlan(boolean retry, Duration delay) {
        this.retry = retry;
        this.delay = delay;
    }

    public static RetryPlan retryWithDelay(Duration delay) {
        return new RetryPlan(true, delay);
    }

    public static RetryPlan noRetry() {
        return new RetryPlan(false, null);
    }

    public boolean shouldRetry() {
        return retry;
    }

    public Duration getDelay() {
        return delay;
    }
}
