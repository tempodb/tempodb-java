package com.tempodb.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;


/**
 *  Set of data to send for a bulk write. This encapsulates the timestamp and list of BulkPoints
 */
public class BulkDataSet {
    private final DateTime timestamp;
    private final List<BulkPoint> data;

    /**
     *  @param timestamp The timestamp to write the datapoints at
     *  @param data A list of BulkPoints to write
     */
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
