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
import com.tempodb.MultiDataPoint;
import com.tempodb.MultiDataPointSegment;
import com.tempodb.Rollup;


public class MultiDataPointSegmentModule extends SimpleModule {

  public MultiDataPointSegmentModule() {
    addDeserializer(MultiDataPointSegment.class, new MultiDataPointSegmentDeserializer());
  }

  private static class MultiDataPointSegmentDeserializer extends StdScalarDeserializer<MultiDataPointSegment> {
    public MultiDataPointSegmentDeserializer() { super(MultiDataPointSegment.class); }

    @Override
    public MultiDataPointSegment deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      JsonNode root = parser.readValueAsTree();
      JsonNode tzNode = root.get("tz");
      JsonNode dataNode = root.get("data");
      JsonNode rollupNode = root.get("rollup");

      if(tzNode == null) {
        throw context.mappingException("Missing 'tz' field in MultiDataPointSegment.");
      }
      if(dataNode == null) {
        throw context.mappingException("Missing 'data' field in MultiDataPointSegment.");
      }

      DateTimeZone timezone = Json.getObjectMapper()
                                  .reader()
                                  .withType(DateTimeZone.class)
                                  .readValue(tzNode);
      List<MultiDataPoint> data = Json.getObjectMapper()
                                 .reader()
                                 .with(timezone.toTimeZone())
                                 .withType(new TypeReference<List<MultiDataPoint>>() {})
                                 .readValue(dataNode);

      Rollup rollup = null;
      if(rollupNode != null) {
        rollup = Json.getObjectMapper()
                     .reader()
                     .withType(Rollup.class)
                     .readValue(rollupNode);
      }

      return new MultiDataPointSegment(data, timezone, rollup);
    }
  }

  @Override
  public String getModuleName() {
    return "multidatapoint-segment";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
