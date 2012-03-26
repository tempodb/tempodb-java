package com.tempodb.demo;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.tempodb.client.Client;
import com.tempodb.client.DataPoint;

public class WriteIdDemo {
	
	public static void main(String[] args) throws Exception {
		Client client = new Client("your-api-key", "your-api-secret");

    	Date datetime1 = Client.iso8601.parse("2009-01-03T13:42:00+0000");
    	Date datetime2 = Client.iso8601.parse("2009-01-04T13:42:00+0000");
    	
    	ArrayList<DataPoint> data = new ArrayList<DataPoint>();
    	data.add(new DataPoint(datetime1, 24.3));
    	data.add(new DataPoint(datetime2, 3.14159));
    	
    	JSONObject writeResults = client.writeId("your-series-id", data);
    	System.out.println(writeResults);
    }
}
