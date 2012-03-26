package com.tempodb.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


public class DataPointManager {
	public static ArrayList<DataPoint> createDataPointList(JSONArray jsonArray) throws Exception {
		ArrayList<DataPoint> out = new ArrayList<DataPoint>();

		for( int i = 0; i < jsonArray.length(); i++ ) {
			JSONObject jsonDp = jsonArray.getJSONObject(i);
			
			out.add(new DataPoint(
						Client.iso8601.parse(jsonDp.getString(DataPoint.tName)), 
						jsonDp.getDouble(DataPoint.vName)
				));
		}

		return out;
	}
}
