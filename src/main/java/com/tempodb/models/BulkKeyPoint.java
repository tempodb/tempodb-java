package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class BulkKeyPoint extends BulkPoint {
    private final String key;

    @JsonCreator
    public BulkKeyPoint(@JsonProperty("key") String key, @JsonProperty("v") Number value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return key; }
}
