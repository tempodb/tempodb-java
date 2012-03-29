package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;


public class Filter {
    private final List<String> ids = new ArrayList<String>();
    private final List<String> keys = new ArrayList<String>();
    private final List<String> tags = new ArrayList<String>();
    private final Map<String, String> attributes = new HashMap<String, String>();

    public synchronized void addId(String id) { ids.add(id); }

    public synchronized void addKey(String key) { keys.add(key); }

    public synchronized void addTag(String tag) { tags.add(tag); }

    public synchronized void addAttribute(String key, String value) { attributes.put(key, value); }

    public List<NameValuePair> getParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Add the ids
        for (String id : ids)
            params.add(new BasicNameValuePair("id", id));

        // Add the keys
        for (String key : keys)
            params.add(new BasicNameValuePair("key", key));

        // Add the tags
        for (String tag : tags)
            params.add(new BasicNameValuePair("tag", tag));

        // Add the attributes
        for (Map.Entry<String, String> attribute : attributes.entrySet())
            params.add(new BasicNameValuePair(String.format("attr[%s]", attribute.getKey()), attribute.getValue()));

        return params;
    }

    @Override
    public String toString() {
        return URLEncodedUtils.format(getParams(), "UTF-8");
    }

    public List<String> getIds() { return ids; }
    public List<String> getKeys() { return keys; }
    public List<String> getTags() { return tags; }
    public Map<String, String> getAttributes() { return attributes; }
}
