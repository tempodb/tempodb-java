package com.tempodb.demo;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.tempodb.client2.Client;
import com.tempodb.client2.ClientBuilder;
import com.tempodb.models.BulkDataSet;
import com.tempodb.models.BulkPoint;
import com.tempodb.models.BulkIdPoint;
import com.tempodb.models.BulkKeyPoint;


public class TestBulkWrite {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("myagley")
            .secret("opensesame")
            .host("localhost")
            .port(4242)
            .secure(false)
            .build();

        DateTime ts = new DateTime(2012, 3, 30, 1, 0, 0, 0);

        ArrayList<BulkPoint> points = new ArrayList<BulkPoint>();
        points.add(new BulkKeyPoint("myagley-1", 123.6));
        points.add(new BulkKeyPoint("myagley-2", 3.4));

        BulkDataSet dataset = new BulkDataSet(ts, points);
        client.bulkWrite(dataset);
    }
}
