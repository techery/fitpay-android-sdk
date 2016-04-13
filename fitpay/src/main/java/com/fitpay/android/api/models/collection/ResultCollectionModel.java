package com.fitpay.android.api.models.collection;

import com.fitpay.android.api.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic result collection model
 *
 * @param <T> type of result
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

    public void addCollection(List<T> collection){
        if(results == null){
            results = new ArrayList<>();
        }

        results.addAll(collection);
    }
}
