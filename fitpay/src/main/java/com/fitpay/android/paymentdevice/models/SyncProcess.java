package com.fitpay.android.paymentdevice.models;

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.sync.MetricsData;
import com.fitpay.android.api.models.sync.SyncMetricsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Support class for sync logic
 */
public final class SyncProcess {

    private final SyncRequest request;
    private List<Commit> commits;
    private List<MetricsData> commitsData;

    private long syncStartTime;

    private Commit pendingCommit;
    private MetricsData pendingCommitMD;

    public SyncProcess(final SyncRequest request) {
        this.request = request;
    }

    public void start() {
        syncStartTime = System.currentTimeMillis();
    }

    public void finish() {
        final SyncMetricsData smd = new SyncMetricsData.Builder()
                .readDataFromRequest(request)
                .setMetricsData(commitsData)
                .setTotalProcessingTime(System.currentTimeMillis() - syncStartTime)
                .build();

        smd.sendData(request);
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits == null ? Collections.emptyList() : commits;
        commitsData = new ArrayList<>(this.commits.size());
    }

    public Commit startCommitProcessing() {
        pendingCommit = commits.remove(0);
        pendingCommitMD = new MetricsData(pendingCommit.getCommitId());
        return pendingCommit;
    }

    public void finishCommitProcessing() {
        finishCommitProcessing(null, null);
    }

    public void finishCommitProcessing(String error, String errorDescription) {
        pendingCommitMD.setEndTime();
        pendingCommitMD.setError(error);
        pendingCommitMD.setErrorDescription(errorDescription);

        commitsData.add(pendingCommitMD);
    }

    public String getPendingCommitId() {
        return pendingCommit != null ? pendingCommit.getCommitId() : "";
    }

    public int size() {
        return commits.size();
    }
}
