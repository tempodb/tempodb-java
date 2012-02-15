package com.tempodb.client;

public class BulkKeyPoint extends BulkPoint {
	
	public BulkKeyPoint(String key, Double v) {
		this.setSeriesType("key");
		this.setSeriesValue(key);
		this.setV(v);
	}
}
