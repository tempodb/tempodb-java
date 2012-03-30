package com.tempodb;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.DataPoint;
import com.tempodb.models.DataSet;


public class TestWrite {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .host("localhost")
            .port(4242)
            .secure(false)
            .build();


        ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(new DataPoint(new DateTime(2012, 3, 29, 5, 0, 0, 0), 32.34));
        datapoints.add(new DataPoint(new DateTime(2012, 3, 29, 6, 0, 0, 0), 36.34));

        client.writeKey("myagley-1", datapoints);

        DateTime start = new DateTime(2012, 3, 28, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 3, 31, 0, 0, 0, 0);

        DataSet dataset = client.readKey("myagley-1", start, end);

        System.out.println(dataset);
        System.out.println(dataset.getData());
    }
}
