package com.tempodb.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;


/**
 *  Respresents data from a time range of a series. This is essentially a list
 *  of DataPoints with some added metadata. This is the object returned from a query.
 *  The DataSet contains series metadata, the start/end times for the queried range,
 *  a list of the DataPoints and a statistics summary table. The Summary table contains
 *  statistics for the time range (sum, mean, min, max, count, etc.)
 */
public class DataSet {
    private final Series series;
    private final DateTime start;
    private final DateTime end;
    private final List<DataPoint> data;
    private final Map<String, Number> summary;

    /**
     *  @param series series metadata (Series)
     *  @param start start time for the queried range (DateTime)
     *  @param end end time for the queried range (DateTime)
     *  @param data datapoints (List of DataPoints)
     *  @param summary a summary table of statistics for the queried range (Map)
     */
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
