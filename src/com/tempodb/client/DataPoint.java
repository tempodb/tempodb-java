package com.tempodb.client;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class DataPoint {
	private Date t;
	private Double v;
	public static final String tName = "t";
	public static final String vName = "v";

	public DataPoint(Date t, Double v) {
		this.t = t;
		this.v = v;
	}
	
	public Date getT() {
		return this.t;
	}
	
	public Double getV() {
		return this.v;
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
		jsonObject.put(DataPoint.tName, Client.iso8601.format(this.t));
		jsonObject.put(DataPoint.vName, this.v);
		return jsonObject;
	}
	
	public String toJSONString() throws Exception {
		return this.toJSONObject().toString();
	}
}
