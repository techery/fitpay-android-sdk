package com.fitpay.android.models;


import java.util.List;

public class ResultCollection<T> {


    private int limit;
    private int offset;
    private int totalResults;
    private List<T> results;

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public List<T> getResults() {
        return results;
    }
}
