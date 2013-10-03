package com.tempodb.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import com.tempodb.WriteRequest;


public class WriteRequestModule extends SimpleModule {
  public WriteRequestModule() {
    addSerializer(WriteRequest.class, new WriteRequestSerializer());
  }

  private static class WriteRequestSerializer extends StdScalarSerializer<WriteRequest> {
    public WriteRequestSerializer() { super(WriteRequest.class); }

    @Override
    public void serialize(WriteRequest value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      jgen.writeObject(value.iterator());
    }
  }

  @Override
  public String getModuleName() {
    return "write-request";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
