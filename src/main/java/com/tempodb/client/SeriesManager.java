package com.tempodb.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class SeriesManager {
	public static ArrayList<Series> createSeriesList(JSONArray jsonArray) throws Exception {
		ArrayList<Series> out = new ArrayList<Series>();

		for( int i = 0; i < jsonArray.length(); i++ ) {
			JSONObject jsonSeries = jsonArray.getJSONObject(i);

			/* setup tags */
			JSONArray jsonTags = jsonSeries.getJSONArray(Series.tagsName);
			String[] tags = new String[jsonTags.length()];
			for ( int j = 0; j < jsonTags.length(); j++ ) {
				tags[j] = jsonTags.getString(j);
			}

			/* setup attributes */
			JSONObject jsonAttributes = jsonSeries.getJSONObject(Series.attributesName);
			Iterator<String> iter = jsonAttributes.keys();
			HashMap<String, String> attributes = new HashMap<String, String>();
			while ( iter.hasNext() ) {
				attributes.put(iter.next(), jsonAttributes.getString(iter.next()));
			}

			out.add(new Series(
						jsonSeries.getString(Series.idName),
						jsonSeries.getString(Series.keyName),
						tags,
						attributes
				));
		}

		return out;
	}
}
