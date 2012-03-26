package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;


public class Series {
    private String id;
    private String key;
    private ArrayList<String> tags;
    private HashMap<String, String> attributes;

    public Series(String id, String key, ArrayList<String> tags, HashMap<String, String> attributes) {
        this.id = id;
        this.key = key;
        this.tags = tags;
        this.attributes = attributes;
    }

    public Series(String id, String key, HashMap<String, String> attributes) {
        this(id, key, new ArrayList<String>(), attributes);
    }

    public Series(String id, String key, ArrayList<String> tags) {
        this(id, key, tags, new HashMap<String, String>());
    }

    public Series(String id, String key) {
        this(id, key, new ArrayList<String>(), new HashMap<String, String>());
    }
}
