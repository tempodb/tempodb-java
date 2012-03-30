package com.tempodb;

import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;

import com.tempodb.models.DataPoint;
import com.tempodb.models.DataSet;


public class TestJsonSer {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(new DataPoint(new DateTime(2012, 3, 29, 1, 0, 0, 0), 12.34));
        datapoints.add(new DataPoint(new DateTime(2012, 3, 29, 2, 0, 0, 0), 56.34));

        String json = mapper.writeValueAsString(datapoints);
        System.out.println(json);
    }
}
