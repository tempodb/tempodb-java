package com.tempodb.client;

public class BulkIdPoint extends BulkPoint {
	
	public BulkIdPoint(String id, Double v) {
		this.setSeriesType("id");
		this.setSeriesValue(id);
		this.setV(v);
	}
}
