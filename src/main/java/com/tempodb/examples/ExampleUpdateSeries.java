package com.tempodb.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.Filter;
import com.tempodb.models.Series;


public class ExampleUpdateSeries {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("temp");

        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("hello", "world");

        Series series = new Series("aa798fb066da4bc28f31eaa004d3ca34", "myagley-1", "Test", tags, attributes);
        client.updateSeries(series);

        Filter filter = new Filter();
        filter.addKey("myagley-1");

        List<Series> seriesList = client.getSeries(filter);
        System.out.println(seriesList);
    }
}
