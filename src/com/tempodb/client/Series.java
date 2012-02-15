package com.tempodb.client;

import java.util.HashMap;

import org.json.JSONObject;

public class Series {
	private String id;
	private String key;
	private String[] tags;
	private HashMap<String, String> attributes;
	
	public static final String idName = "id";
	public static final String keyName = "key";
	public static final String tagsName = "tags";
	public static final String attributesName = "attributes";
	
	public Series(String id, String key, String[] tags, HashMap<String, String> attributes) {
		this.id = id;
		this.key = key;
		this.tags = tags;
		this.attributes = attributes;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String[] getTags() {
		return this.tags;
	}
	
	public HashMap<String, String> getAttributes() {
		return this.attributes;
	}
	
	public String toString() {
		try {
			return this.toJSONString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public JSONObject toJSONObject() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Series.idName, this.id);
		jsonObject.put(Series.keyName, this.key);
		jsonObject.put(Series.tagsName, this.tags);
		jsonObject.put(Series.attributesName, this.attributes);
		return jsonObject;
	}
	
	public String toJSONString() throws Exception {
		return this.toJSONObject().toString();
	}
}
