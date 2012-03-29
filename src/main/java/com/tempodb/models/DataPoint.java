package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;


public class DataPoint {
    private final DateTime timestamp;
    private final Number value;

    @JsonCreator
    public DataPoint(@JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public DateTime getTimestamp() { return timestamp; }
    public Number getValue() { return value; }
}
