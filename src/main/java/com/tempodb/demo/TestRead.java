package com.tempodb.demo;

import org.joda.time.DateTime;

import com.tempodb.client2.Client;
import com.tempodb.client2.ClientBuilder;
import com.tempodb.models.DataSet;


public class TestRead {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .build();

        DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0);

        DataSet dataset = client.readKey("myagley-1", start, end);
        System.out.println(dataset);
        System.out.println(dataset.getData());
    }
}
