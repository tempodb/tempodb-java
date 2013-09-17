package com.tempodb.examples;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.MultiPoint;
import com.tempodb.models.MultiIdPoint;
import com.tempodb.models.MultiKeyPoint;


public class ExampleMultiWrite {

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        DateTime ts = new DateTime(2012, 3, 30, 1, 0, 0, 0);
        DateTime ts1 = new DateTime(2012, 3, 30, 2, 0, 0, 0);
        DateTime ts2 = new DateTime(2012, 3, 30, 3, 0, 0, 0);

        ArrayList<MultiPoint> points = new ArrayList<MultiPoint>();
        points.add(new MultiKeyPoint(ts, "myagley-1", 123.6));
        points.add(new MultiKeyPoint(ts1, "myagley-2", 3.4));
        points.add(new MultiKeyPoint(ts2, "myagley-3", 23.4));

        client.multiWrite(points);
    }
}
