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

import com.tempodb.Series;
import com.tempodb.SeriesSegment;


public class SeriesSegmentModule extends SimpleModule {

  public SeriesSegmentModule() {
    addDeserializer(SeriesSegment.class, new SeriesSegmentDeserializer());
  }

  private static class SeriesSegmentDeserializer extends StdScalarDeserializer<SeriesSegment> {
    public SeriesSegmentDeserializer() { super(SeriesSegment.class); }

    @Override
    public SeriesSegment deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
        List<Series> series = parser.readValueAs(new TypeReference<List<Series>>() {});
        return new SeriesSegment(series);
      }
      throw context.mappingException("Expected JSON array");
    }
  }

  @Override
  public String getModuleName() {
    return "series-segment";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
