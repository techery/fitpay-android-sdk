package com.fitpay.android.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generated server links. HATEOS representation
 */
public class Links {
    private Map<String, String> links;

    public Links(){
        links = new HashMap<>();
    }

    public Set<String> getLinks() {
        return links.keySet();
    }

    public void setLink(String key, String value){
        links.put(key, value);
    }

    public String getLink(String key){
        if(links.containsKey(key)){
            return links.get(key);
        }

        return null;
    }
}
