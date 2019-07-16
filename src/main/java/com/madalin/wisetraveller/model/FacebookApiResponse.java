package com.madalin.wisetraveller.model;

import java.util.Map;

public class FacebookApiResponse {
    private String id;
    private Map<String, String> values;

    public FacebookApiResponse(Map<String, String> values) {
        this.values = values;
        if (values.get("id") == null)
            throw new RuntimeException();
        id = values.get("id");
    }

    public String getId() {
        return id;
    }

    public String get(String name) {
        return values.get(name);
    }

    public boolean hasValue(String field) {
        return values.containsKey(field);
    }
}
