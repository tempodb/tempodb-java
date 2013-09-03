package com.tempodb.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import com.tempodb.Fold;


public class FoldModule extends SimpleModule {
  public FoldModule() {
    addDeserializer(Fold.class, new FoldDeserializer());
    addSerializer(Fold.class, new FoldSerializer());
  }

  private static class FoldDeserializer extends StdScalarDeserializer<Fold> {
    private final Fold[] constants;
    private final List<String> acceptedValues;

    public FoldDeserializer() {
      super(Fold.class);
      this.constants = Fold.values();
      this.acceptedValues = new ArrayList();
      for (Fold constant : constants) {
        acceptedValues.add(constant.name());
      }
    }

    @Override
    public Fold deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      final String text = jp.getText().toUpperCase();
      for (Fold constant : constants) {
        if (constant.name().equals(text)) {
          return constant;
        }
      }

      throw ctxt.mappingException(text + " was not one of " + acceptedValues);
    }
  }

  private static class FoldSerializer extends StdScalarSerializer<Fold> {
    public FoldSerializer() { super(Fold.class); }

    @Override
    public void serialize(Fold value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      String e = value.name().toLowerCase();
      jgen.writeString(e);
    }
  }

  @Override
  public String getModuleName() {
    return "fold";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
