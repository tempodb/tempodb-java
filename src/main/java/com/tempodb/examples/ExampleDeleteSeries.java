package com.tempodb.examples;

import java.util.List;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.DeleteSummary;
import com.tempodb.models.Filter;


public class ExampleDeleteSeries {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        Filter filter = new Filter();
        filter.addTag("tag1");

        DeleteSummary summary = client.deleteSeries(filter);
        System.out.println(summary.getDeleted());
    }
}
