package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *  The abstract parent class representing a datapoint used in a bulk write. Bulk
 *  writing allows values for different series to be written for the same timestamp in one
 *  Rest call. The series can be referenced by series id or series key. The two subclasses
 *  represent these two options.
 */
public abstract class BulkPoint {
    protected Number value;

    @JsonProperty("v")
    public Number getValue() { return value; }
}
