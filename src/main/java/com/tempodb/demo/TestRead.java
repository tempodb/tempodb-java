package com.tempodb.demo;

import java.util.List;

import org.joda.time.DateTime;

import com.tempodb.client2.Client;
import com.tempodb.client2.ClientBuilder;
import com.tempodb.models.DataSet;
import com.tempodb.models.Filter;


public class TestRead {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .build();

        DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0);
        Filter filter = new Filter();
        filter.addKey("myagley-1");
        filter.addKey("myagley-2");

        List<DataSet> datasets = client.read(start, end, filter);

        System.out.println(datasets);
        System.out.println(datasets.get(0).getSeries());
        System.out.println(datasets.get(1).getSeries());
    }
}
