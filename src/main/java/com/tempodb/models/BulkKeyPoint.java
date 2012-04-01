package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *  Represents a datapoint for a series referenced by key. This class is used to represent
 *  datapoints in a bulk write.
 */
public class BulkKeyPoint extends BulkPoint {
    private final String key;

    /**
     *  @param id The id of the Series
     *  @param value The datapoint value
     */
    @JsonCreator
    public BulkKeyPoint(@JsonProperty("key") String key, @JsonProperty("v") Number value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return key; }
}
