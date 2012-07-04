package com.tempodb;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.DataPoint;
import com.tempodb.models.DataSet;


public class TestIncrement {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .host("localhost")
            .port(4242)
            .secure(false)
            .build();

        String key = "increment-java";

        ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(new DataPoint(new DateTime(2012, 7, 4, 15, 0, 0, 0), 1));
        datapoints.add(new DataPoint(new DateTime(2012, 7, 4, 15, 1, 0, 0), 2));

        client.incrementKey(key, datapoints);

        DateTime start = new DateTime(2012, 7, 4, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 7, 5, 0, 0, 0, 0);

        DataSet dataset = client.readKey(key, start, end);

        System.out.println(dataset);
        System.out.println(dataset.getData());
    }
}
