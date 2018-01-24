package com.fitpay.android.api.models.sync;

/**
 * Metrics data for {@link SyncMetricsData}
 */
public final class MetricsData {
    private final String commitId;
    private long processingTimeMS;
    private String error;
    private String errorDescription;

    private transient long startTime;

    public MetricsData(String commitId) {
        this.commitId = commitId;
        startTime = System.currentTimeMillis();
    }

    /**
     * Processing current {@link MetricsData#commitId} commit was done
     */
    public void setEndTime() {
        processingTimeMS = System.currentTimeMillis() - startTime;
    }

    /**
     * Set error.
     *
     * @param error readable error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Set error description.
     *
     * @param errorDescription description
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return "MetricsData{" +
                "commitId=" + commitId +
                ", totalProcessingTimeMS=" + processingTimeMS +
                ", error=" + error +
                ", errorDescription=" + errorDescription +
                '}';
    }


}
