package com.dataplatform.is.spark;

import com.dataplatform.is.model.JobSubmissionRequest;

public interface SparkJobRunner {
    void submit(JobSubmissionRequest request);
}
