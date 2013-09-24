package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

/**
 *  Represents a datapoint for a series referenced by id. This class is used to represent
 *  datapoints in a multi write.
 */
public class MultiIdPoint extends MultiPoint {
    private final String id;

    /**
     *  @param timestamp The timestamp of the datapoint
     *  @param id The id of the Series
     *  @param value The datapoint value
     */
    @JsonCreator
    public MultiIdPoint(@JsonProperty("id") String id, @JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getId() { return id; }
}
