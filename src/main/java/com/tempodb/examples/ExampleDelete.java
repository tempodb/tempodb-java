package com.tempodb.examples;

import java.util.List;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;


public class ExampleDelete {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        DateTime start = new DateTime(2012, 3, 30, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 4, 1, 0, 0, 0, 0);

        String key = "my-key";
        client.deleteKey(key, start, end);
    }
}
