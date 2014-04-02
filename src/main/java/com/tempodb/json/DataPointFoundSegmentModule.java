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
import com.tempodb.DataPointFound;
import com.tempodb.DataPointFoundSegment;
import com.tempodb.Predicate;


public class DataPointFoundSegmentModule extends SimpleModule {

  public DataPointFoundSegmentModule() {
    addDeserializer(DataPointFoundSegment.class, new DataPointFoundSegmentDeserializer());
  }

  private static class DataPointFoundSegmentDeserializer extends StdScalarDeserializer<DataPointFoundSegment> {
    public DataPointFoundSegmentDeserializer() { super(DataPointFoundSegment.class); }

    @Override
    public DataPointFoundSegment deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      JsonNode root = parser.readValueAsTree();
      JsonNode tzNode = root.get("tz");
      JsonNode dataNode = root.get("data");
      JsonNode predicateNode = root.get("predicate");

      if(tzNode == null) {
        throw context.mappingException("Missing 'tz' field in DataPointFoundSegment.");
      }
      if(dataNode == null) {
        throw context.mappingException("Missing 'data' field in DataPointFoundSegment.");
      }

      DateTimeZone timezone = Json.getObjectMapper()
                                  .reader()
                                  .withType(DateTimeZone.class)
                                  .readValue(tzNode);
      List<DataPointFound> data = Json.getObjectMapper()
                                 .reader()
                                 .with(timezone.toTimeZone())
                                 .withType(new TypeReference<List<DataPointFound>>() {})
                                 .readValue(dataNode);

      Predicate predicate = null;
      if(predicateNode != null) {
        predicate = Json.getObjectMapper()
                     .reader()
                     .withType(Predicate.class)
                     .readValue(predicateNode);
      }

      return new DataPointFoundSegment(data, timezone, predicate);
    }
  }

  @Override
  public String getModuleName() {
    return "datapointfound-segment";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
