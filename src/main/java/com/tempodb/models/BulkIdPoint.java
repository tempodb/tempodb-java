package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class BulkIdPoint extends BulkPoint {
    private final String id;

    @JsonCreator
    public BulkIdPoint(@JsonProperty("id") String id, @JsonProperty("v") Number value) {
        this.id = id;
        this.value = value;
    }

    public String getId() { return id; }
}
