package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;


/**
 *  Represents a filter on the set of Series. This is used to query a set of series with specific
 *  properties. A filter can include ids, keys, tags and attributes. An empty filter is created
 *  and filter predicates are added. For example a filter to return series with keys myagley-1 and
 *  myagley-2 looks like this:
 *  <pre>
 *  {@code
 *  Filter filter = new Filter();
 *  filter.addKey("myagley-1");
 *  filter.addKey("myagley-1");
 *  }
 *  </pre>
 */
public class Filter {
    private final List<String> ids = new ArrayList<String>();
    private final List<String> keys = new ArrayList<String>();
    private final List<String> tags = new ArrayList<String>();
    private final Map<String, String> attributes = new HashMap<String, String>();

    /**
     *  Adds an id to the filter.
     *
     *  @param id The id to add
     */
    public synchronized void addId(String id) { ids.add(id); }

    /**
     *  Adds a key to the filter.
     *
     *  @param key The key to add
     */
    public synchronized void addKey(String key) { keys.add(key); }

    /**
     *  Adds a tag to the filter.
     *
     *  @param tag The tag to add
     */
    public synchronized void addTag(String tag) { tags.add(tag); }

    /**
     *  Adds an attribute to the filter.
     *
     *  @param key The attribute key
     *  @param value The attribute value
     */
    public synchronized void addAttribute(String key, String value) { attributes.put(key, value); }

    /**
     *  Returns a list of NameValuePairs in the format required for the Rest API.
     *  This is mainly used internally by the client.
     */
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
