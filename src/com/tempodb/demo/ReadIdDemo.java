package com.tempodb.demo;

import java.util.ArrayList;
import java.util.Date;

import com.tempodb.client.Client;
import com.tempodb.client.DataPoint;

public class ReadIdDemo {
	
	public static void main(String[] args) throws Exception {
    	Client client = new Client("myagley", "opensesame");

    	Date start = Client.iso8601.parse("2012-01-01T13:42:00.000+0000");
    	Date end = Client.iso8601.parse("2012-01-02T13:42:00.000+0000");
    	ArrayList<DataPoint> readId = client.readId("8f14b50a05ab4f8f9e85416d61c5ef6e", start, end);

    	System.out.println(readId);
    }
}
