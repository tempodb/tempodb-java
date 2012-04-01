package com.tempodb.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;


public class DataSet {
    private final Series series;
    private final DateTime start;
    private final DateTime end;
    private final List<DataPoint> data;
    private final Map<String, Number> summary;

    @JsonCreator
    public DataSet(@JsonProperty("series") Series series, @JsonProperty("start") DateTime start,
            @JsonProperty("end") DateTime end, @JsonProperty("data") List<DataPoint> data,
            @JsonProperty("summary") Map<String, Number> summary) {
        this.series = series;
        this.start = start;
        this.end = end;
        this.data = data;
        this.summary = summary;
    }

    public Series getSeries() { return series; }
    public DateTime getStart() { return start; }
    public DateTime getEnd() { return end; }
    public List<DataPoint> getData() { return data; }
    public Map<String, Number> getSummary() { return summary; }
}
