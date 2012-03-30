package com.tempodb;

import java.util.List;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.DataSet;
import com.tempodb.models.Filter;


public class TestRead {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        DateTime start = new DateTime(2012, 3, 30, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 4, 1, 0, 0, 0, 0);
        Filter filter = new Filter();
        filter.addKey("myagley-1");
        filter.addKey("myagley-2");

        List<DataSet> datasets = client.read(start, end, filter);

        System.out.println(datasets);
        System.out.println(datasets.get(0).getData());
        System.out.println(datasets.get(1).getData());
    }
}
