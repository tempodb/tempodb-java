package com.tempodb.examples;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.tempodb.client.Client;
import com.tempodb.client.ClientBuilder;
import com.tempodb.models.MultiPoint;
import com.tempodb.models.MultiIdPoint;
import com.tempodb.models.MultiKeyPoint;


public class ExampleMultiIncrement{

    public static void main(String[] args) throws Exception {
        Client client = new ClientBuilder()
            .key("your-api-key")
            .secret("your-api-secret")
            .build();

        DateTime ts = new DateTime(2012, 3, 30, 1, 0, 0, 0);
        DateTime ts1 = new DateTime(2012, 3, 30, 2, 0, 0, 0);
        DateTime ts2 = new DateTime(2012, 3, 30, 3, 0, 0, 0);

        ArrayList<MultiPoint> points = new ArrayList<MultiPoint>();
        points.add(new MultiKeyPoint("myagley-1", ts, 1));
        points.add(new MultiKeyPoint("myagley-2", ts1, 3));
        points.add(new MultiKeyPoint("myagley-3", ts2, 2));

        client.multiIncrement(points);
    }
}
