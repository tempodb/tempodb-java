package com.tempodb.client;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MultiStatusException extends Exception implements Iterable<MultiPointStatus> {
    private List<MultiPointStatus> statuses;

    @JsonCreator
    public MultiStatusException(@JsonProperty("multistatus") List<MultiPointStatus> statuses) {
        this.statuses = statuses;
    }

    public List<MultiPointStatus> getStatuses() { return statuses; }
    public Iterator<MultiPointStatus> iterator() { return statuses.iterator(); }
}
