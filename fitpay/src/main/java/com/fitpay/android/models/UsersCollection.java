package com.fitpay.android.models;


import java.util.List;
public class UsersCollection {

    /**
     * Max number of profiles per page
     * default: 10
     */
    private int limit;

    /**
     * Start index position for list of entities returned
     */
    private int offset;

    private int totalResults;
    private List<User> results;


    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public void setResults(List<User> results) {
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

    public List<User> getResults() {
        return results;
    }

}