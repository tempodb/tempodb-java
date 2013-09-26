package com.tempodb.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MultiPointStatus {
    private final int status;
    private final List<String> messages;

    /**
     *  @param status The status code
     *  @param messages The error messages associated with this status
     */
    @JsonCreator
    public MultiPointStatus(@JsonProperty("status") int status, @JsonProperty("messages") List<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public int getStatus() { return status; }
    public List<String> getMessages() { return messages; }
}
