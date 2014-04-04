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

import com.tempodb.SingleValue;
import com.tempodb.SingleValueSegment;


public class SingleValueSegmentModule extends SimpleModule {

  public SingleValueSegmentModule() {
    addDeserializer(SingleValueSegment.class, new SingleValueSegmentDeserializer());
  }

  private static class SingleValueSegmentDeserializer extends StdScalarDeserializer<SingleValueSegment> {
    public SingleValueSegmentDeserializer() { super(SingleValueSegment.class); }

    @Override
    public SingleValueSegment deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
        List<SingleValue> series = parser.readValueAs(new TypeReference<List<SingleValue>>() {});
        return new SingleValueSegment(series);
      }
      throw context.mappingException("Expected JSON array");
    }
  }

  @Override
  public String getModuleName() {
    return "singlevalue-segment";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
