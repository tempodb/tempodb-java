package com.tempodb.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;


public class BulkDataSet {
    private final DateTime timestamp;
    private final List<BulkPoint> data;

    @JsonCreator
    public BulkDataSet(@JsonProperty("t") DateTime timestamp, @JsonProperty("data") List<BulkPoint> data) {
        this.timestamp = timestamp;
        this.data = data;
    }

    @JsonProperty("t")
    public DateTime getTimestamp() { return timestamp; }

    @JsonProperty("data")
    public List<BulkPoint> getData() { return data; }

}
