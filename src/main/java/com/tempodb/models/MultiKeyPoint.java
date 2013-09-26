package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

/**
 *  Represents a datapoint for a series referenced by key. This class is used to represent
 *  datapoints in a multi write.
 */
public class MultiKeyPoint extends MultiPoint {
    private final String key;

    /**
     *  @param timestamp The timestamp of the datapoint
     *  @param key The key of the Series
     *  @param value The datapoint value
     */
    @JsonCreator
    public MultiKeyPoint(@JsonProperty("key") String key, @JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
        this.key = key;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getKey() { return key; }
}
