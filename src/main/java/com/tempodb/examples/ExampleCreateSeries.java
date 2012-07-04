package com.tempodb.examples;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.Series;


public class ExampleCreateSeries {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        Series series1 = client.createSeries("my-custom-key");
        Series series2 = client.createSeries();

        System.out.println(series1);
        System.out.println(series2);
    }
}
