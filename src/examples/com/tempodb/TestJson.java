package com.tempodb;

import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.tempodb.models.DataPoint;
import com.tempodb.models.DataSet;


public class TestJson {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        String json = "{\"series\":{\"id\":\"9e40006140ab4cf6a07495fe41d00a0a\",\"key\":\"myagley-1\",\"name\":\"\",\"attributes\":{},\"tags\":[]},\"start\":\"2013-01-01T06:00:00.000+0000\",\"end\":\"2013-01-02T06:00:00.000+0000\",\"data\":[],\"summary\":{\"sum\":0,\"mean\":0,\"max\":0,\"min\":0,\"count\":0}}";
        DataSet dataset = mapper.readValue(json, DataSet.class);
        System.out.println(dataset);
        System.out.println(dataset.getSeries());
        System.out.println(dataset.getStart());
        System.out.println(dataset.getEnd());
        System.out.println(dataset.getData());
        System.out.println(dataset.getSummary());
    }
}
