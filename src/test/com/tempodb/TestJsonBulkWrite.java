package com.tempodb;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.tempodb.models.BulkDataSet;
import com.tempodb.models.BulkPoint;
import com.tempodb.models.BulkIdPoint;
import com.tempodb.models.BulkKeyPoint;
import com.tempodb.models.DataPoint;
import com.tempodb.models.DataSet;


public class TestJsonBulkWrite {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        DateTime now = new DateTime();
        ArrayList<BulkPoint> points = new ArrayList<BulkPoint>();
        points.add(new BulkKeyPoint("myagley-1", 123.6));
        points.add(new BulkIdPoint("id12121", 3));

        BulkDataSet dataset = new BulkDataSet(now, points);
        String json = mapper.writeValueAsString(dataset);
        System.out.println(json);

/*
        String json = "[{\"t\":\"2012-01-01T01:01:00+00:00\",\"v\":23.45},{\"t\":\"2012-01-02T01:01:00+00:00\",\"v\":3.45}]";
        ArrayList<DataPoint> datapoints = mapper.readValue(json, new TypeReference<ArrayList<DataPoint>>(){});

        System.out.println(datapoints);
*/
    }
}
