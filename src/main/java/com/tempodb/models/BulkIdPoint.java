package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *  Represents a datapoint for a series referenced by id. This class is used to represent
 *  datapoints in a bulk write.
 */
public class BulkIdPoint extends BulkPoint {
    private final String id;

    /**
     *  @param id The id of the Series
     *  @param value The datapoint value
     */
    @JsonCreator
    public BulkIdPoint(@JsonProperty("id") String id, @JsonProperty("v") Number value) {
        this.id = id;
        this.value = value;
    }

    public String getId() { return id; }
}
