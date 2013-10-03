package com.tempodb.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import com.tempodb.MultiDataPoint;


public class MultiDataPointModule extends SimpleModule {
  public MultiDataPointModule() {
    addSerializer(MultiDataPoint.class, new MultiDataPointSerializer());
  }

  private static class MultiDataPointSerializer extends StdScalarSerializer<MultiDataPoint> {
    public MultiDataPointSerializer() { super(MultiDataPoint.class); }

    @Override
    public void serialize(MultiDataPoint value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      jgen.writeStartObject();
      jgen.writeStringField("key", value.getSeries().getKey());
      jgen.writeObjectField("t", value.getTimestamp());
      jgen.writeObjectField("v", value.getValue());
      jgen.writeEndObject();
    }
  }

  @Override
  public String getModuleName() {
    return "multidatapoint";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
