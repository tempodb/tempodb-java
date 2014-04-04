package com.tempodb.json;

import java.io.IOException;
import java.util.List;

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

import org.joda.time.DateTimeZone;
import com.tempodb.DataPoint;
import com.tempodb.SingleValue;
import com.tempodb.Series;


public class SingleValueModule extends SimpleModule {

  public SingleValueModule() {
    addDeserializer(SingleValue.class, new SingleValueDeserializer());
  }

  private static class SingleValueDeserializer extends StdScalarDeserializer<SingleValue> {
    public SingleValueDeserializer() { super(SingleValue.class); }

    @Override
    public SingleValue deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      JsonNode root = parser.readValueAsTree();
      JsonNode tzNode = root.get("tz");
      JsonNode dataNode = root.get("data");
      JsonNode seriesNode = root.get("series");

      if(tzNode == null) {
        throw context.mappingException("Missing 'tz' field in SingleValue.");
      }
      if(dataNode == null) {
        throw context.mappingException("Missing 'data' field in SingleValue.");
      }
      if(seriesNode == null) {
        throw context.mappingException("Missing 'series' field in SingleValue.");
      }

      DateTimeZone timezone = Json.getObjectMapper()
                                  .reader()
                                  .withType(DateTimeZone.class)
                                  .readValue(tzNode);
      DataPoint datapoint = Json.getObjectMapper()
                                .reader()
                                .with(timezone.toTimeZone())
                                .withType(DataPoint.class)
                                .readValue(dataNode);

      Series series = Json.getObjectMapper()
                          .reader()
                          .withType(Series.class)
                          .readValue(seriesNode);

      return new SingleValue(series, datapoint);
    }
  }

  @Override
  public String getModuleName() {
    return "singlevalue";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}

