package com.tempodb.json;

import java.io.IOException;
import java.util.List;
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
import org.joda.time.Interval;


public class IntervalModule extends SimpleModule {

  public IntervalModule() {
    addDeserializer(Interval.class, new IntervalDeserializer());
  }

  private static class IntervalDeserializer extends StdScalarDeserializer<Interval> {
    public IntervalDeserializer() { super(Interval.class); }

    @Override
    public Interval deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      TimeZone timezone = context.getTimeZone();
      JsonNode root = parser.readValueAsTree();
      JsonNode startNode = root.get("start");
      JsonNode endNode = root.get("end");

      if(startNode == null) {
        throw context.mappingException("Missing 'start' field in Interval.");
      }
      if(endNode == null) {
        throw context.mappingException("Missing 'end' field in Interval.");
      }

      DateTime start = Json.reader()
                           .with(timezone)
                           .withType(DateTime.class)
                           .readValue(startNode);

      DateTime end = Json.reader()
                         .with(timezone)
                         .withType(DateTime.class)
                         .readValue(endNode);

      return new Interval(start, end);
    }
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
