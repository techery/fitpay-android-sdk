package com.fitpay.android.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vlad on 11.03.2016.
 */
final class ObjectConverter {

    public static <T> Map<String, Object> convertToSimpleMap(T object) {
        Gson gson = new Gson();

        JsonElement objectAsJson = gson.toJsonTree(object);
        LinkedTreeMap objectAsMap = gson.fromJson(objectAsJson, LinkedTreeMap.class);

        Map<String, Object> resultMap = new HashMap<>();
        iterateThroughMap(0, "", objectAsMap, resultMap);

        return resultMap;
    }

    private static void iterateThroughMap(int deepLevel, String keyName, LinkedTreeMap treeMap, Map<String, Object> resultMap) {
        for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>) treeMap.entrySet()) {
            if (entry.getValue() instanceof LinkedTreeMap) {
                if (deepLevel++ > 0) {
                    keyName = new StringBuilder()
                            .append(keyName)
                            .append("/")
                            .append(entry.getKey())
                            .append("/")
                            .toString();
                }
                iterateThroughMap(deepLevel, keyName, (LinkedTreeMap) entry.getValue(), resultMap);
            } else {
                if (StringUtils.isEmpty(keyName)) {
                    keyName = "/";
                }
                resultMap.put(keyName + entry.getKey(), entry.getValue());
            }
        }
    }
}
