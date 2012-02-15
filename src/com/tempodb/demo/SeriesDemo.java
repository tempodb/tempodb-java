package com.tempodb.demo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.tempodb.client.Client;
import com.tempodb.client.Series;

public class SeriesDemo {
	
	public static void main(String[] args) throws Exception {
		Client client = new Client("your-api-key", "your-api-secret");

    	ArrayList<Series> series = client.getSeries();

    	for ( Series s : series ) {
			System.out.println( "id: " + s.getId() );
			System.out.println( "key: " + s.getKey() );
			System.out.println( "tags: " + s.getTags() );
			System.out.println( "attributes: " + s.getAttributes() );
			System.out.println();
		}
    }


}
