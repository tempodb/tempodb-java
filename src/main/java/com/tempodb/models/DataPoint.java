package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;


/**
 *  Represents one timestamp/value pair. This class uses a Joda Time DateTime.
 */
public class DataPoint {
    private final DateTime timestamp;
    private final Number value;

    /**
     *  @param timestamp A Joda Time DateTime representing the data point's timstamp
     *  @param value The value of the measurement (long or double)
     */
    @JsonCreator
    public DataPoint(@JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @JsonProperty("t")
    public DateTime getTimestamp() { return timestamp; }

    @JsonProperty("v")
    public Number getValue() { return value; }

    @Override
    public String toString() {
        return String.format("DataPoint(%s, %s)", timestamp, value);
    }
}
