package com.tempodb.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public abstract class BulkPoint {
    protected Number value;

    @JsonProperty("v")
    public Number getValue() { return value; }
}
