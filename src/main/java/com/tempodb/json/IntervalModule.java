package com.tempodb.json;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;


public class IntervalModule extends SimpleModule {

  public IntervalModule() {
    addDeserializer(Interval.class, new IntervalDeserializer());
  }

  private static class IntervalDeserializer extends StdScalarDeserializer<Interval> {
    public IntervalDeserializer() { super(Interval.class); }

    @Override
    public Interval deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      DateTimeZone timezone = DateTimeZone.forTimeZone(context.getTimeZone());
      Map<String, String> obj = parser.readValueAs(new TypeReference<Map<String, String>>() {});

      String startStr = obj.get("start");
      String endStr = obj.get("end");

      if(startStr == null) {
        throw context.mappingException("Missing 'start' field in Interval.");
      }
      if(endStr == null) {
        throw context.mappingException("Missing 'end' field in Interval.");
      }

      DateTime start = datetimeFromString(startStr, timezone, context);
      DateTime end = datetimeFromString(endStr, timezone, context);
      return new Interval(start, end);
    }
  }

  private static DateTime datetimeFromString(String str, DateTimeZone dtz, DeserializationContext ctxt) {
    if (str.length() == 0) { // [JACKSON-360]
      return null;
    }
    if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE))
      return new DateTime(str, dtz);
    else
      return DateTime.parse(str);
  }

  @Override
  public String getModuleName() {
    return "interval";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
