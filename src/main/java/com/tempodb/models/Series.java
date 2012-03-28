package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Series {
    private final String id;
    private final String key;
    private final String name;
    private final ArrayList<String> tags;
    private final HashMap<String, String> attributes;

    @JsonCreator
    public Series(@JsonProperty("id") String id, @JsonProperty("key") String key,
            @JsonProperty("name") String name, @JsonProperty("tags") ArrayList<String> tags,
            @JsonProperty("attributes") HashMap<String, String> attributes) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.tags = tags;
        this.attributes = attributes;
    }

    public Series(String id, String key) {
        this(id, key, "", new ArrayList<String>(), new HashMap<String, String>());
    }

    public String getId() { return id; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public ArrayList<String> getTags() { return tags; }
    public HashMap<String, String> getAttributes() { return attributes; }
}
