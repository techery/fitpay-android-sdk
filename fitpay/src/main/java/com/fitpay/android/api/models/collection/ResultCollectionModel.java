package com.fitpay.android.api.models.collection;

import com.fitpay.android.api.models.BaseModel;

import java.util.List;

/**
 * Created by Vlad on 14.03.2016.
 */
public class ResultCollectionModel<T> extends BaseModel {

    private int limit;
    private int offset;
    private int totalResults;
    private List<T> results;

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
