package com.tempodb.demo;

import java.util.List;

import com.tempodb.client2.Client;
import com.tempodb.client2.ClientBuilder;
import com.tempodb.models.Filter;
import com.tempodb.models.Series;

public class TestDemo {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .build();

        Filter filter = new Filter();
        filter.addKey("myagley-1");
        filter.addKey("myagley-2");

        List<Series> response = client.getSeries(filter);
        System.out.println(response);
    }
}
