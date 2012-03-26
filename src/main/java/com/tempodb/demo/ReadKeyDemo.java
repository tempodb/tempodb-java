package com.tempodb.demo;

import java.util.ArrayList;
import java.util.Date;

import com.tempodb.client.Client;
import com.tempodb.client.DataPoint;

public class ReadKeyDemo {
	
	public static void main(String[] args) throws Exception {
		Client client = new Client("your-api-key", "your-api-secret");

    	Date start = Client.iso8601.parse("2012-01-03T13:42:00.000+0000");
    	Date end = Client.iso8601.parse("2012-01-04T13:42:00.000+0000");
    	ArrayList<DataPoint> readKey = client.readKey("your-series-key", start, end);

    	System.out.println(readKey);
    }
}
