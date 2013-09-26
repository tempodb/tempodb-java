package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

/**
 *  The abstract parent class representing a datapoint used in a multi write. Multi
 *  writing allows values for different series to be written for multiple timestamps in one
 *  Rest call. The series can be referenced by series id or series key. The two subclasses
 *  represent these two options.
 */
public abstract class MultiPoint {
    protected Number value;
    protected DateTime timestamp;

    @JsonProperty("t")
    public DateTime getTimestamp() { return timestamp; }

    @JsonProperty("v")
    public Number getValue() { return value; }
}
