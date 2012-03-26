package com.tempodb.demo;

import com.tempodb.client2.Client;
import com.tempodb.client2.ClientBuilder;


public class TestDemo {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        String response = client.request("/v1/series/");
        System.out.println(response);
    }
}
