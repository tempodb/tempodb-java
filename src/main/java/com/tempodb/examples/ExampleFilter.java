package com.tempodb.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tempodb.models.Filter;


public class ExampleFilter {

    public static void main(String[] args) throws Exception {
        Filter filter = new Filter();
        filter.addId("id1");
        filter.addId("id2");
        filter.addKey("key2");
        filter.addTag("tag343");
        filter.addAttribute("hello", "world");

        System.out.println(filter);
    }
}
