package com.tempodb.demo;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.tempodb.client.BulkIdPoint;
import com.tempodb.client.BulkKeyPoint;
import com.tempodb.client.BulkPoint;
import com.tempodb.client.Client;

public class BulkWriteDemo {

	public static void main(String[] args) throws Exception {
    	Client client = new Client("myagley", "opensesame");

    	Date t = Client.iso8601.parse("2009-01-03T13:42:00.000+0000");
    	ArrayList<BulkPoint> data = new ArrayList<BulkPoint>();

    	/* you can mix and match Id and Key */
    	data.add(new BulkIdPoint("your-series-key1", 27.234));
    	data.add(new BulkIdPoint("your-series-key2", 15.947));
    	data.add(new BulkKeyPoint("your-series-key3", 79.0));
    	data.add(new BulkKeyPoint("your-series-key4", 41.12));

    	JSONObject bulkWrite = client.bulkWrite(t, data);
    	System.out.println(bulkWrite);
    }
}
