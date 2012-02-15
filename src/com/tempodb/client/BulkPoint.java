package com.tempodb.client;

import org.json.JSONObject;

public abstract class BulkPoint {
	private String seriesType;
	private String seriesValue;
	private Double v;
	
	public String getSeriesType() {
		return this.seriesType;
	}
	
	public void setSeriesType(String seriesType) {
		this.seriesType = seriesType;
	}
	
	public String getSeriesValue() {
		return this.seriesValue;
	}
	
	public void setSeriesValue(String seriesValue) {
		this.seriesValue = seriesValue;
	}
	
	public Double getV() {
		return this.v;
	}
	
	public void setV(Double v) {
		this.v = v;
	}
	
	public JSONObject toJSONObject() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(this.getSeriesType(), this.getSeriesValue());
		jsonObject.put("v", this.getV());
		
		return jsonObject;
	}
	
	public String toJSONString() throws Exception {
		return this.toJSONObject().toString();
	}
}
