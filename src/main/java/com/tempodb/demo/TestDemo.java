package com.tempodb.demo;

import java.util.List;

import com.tempodb.client2.Client;
import com.tempodb.client2.ClientBuilder;
import com.tempodb.models.Series;

public class TestDemo {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .build();

        List<Series> response = client.getSeries();
        System.out.println(response);
    }
}
