package com.tempodb.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.joda.time.DateTimeZone;


public class DateTimeZoneModule extends SimpleModule {
  public DateTimeZoneModule() {
    addDeserializer(DateTimeZone.class, new DateTimeZoneDeserializer());
    addSerializer(DateTimeZone.class, new DateTimeZoneSerializer());
  }

  private static class DateTimeZoneDeserializer extends StdScalarDeserializer<DateTimeZone> {
    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      if (jsonParser.getCurrentToken() == JsonToken.VALUE_STRING) {
        String tz = jsonParser.getText();
        return DateTimeZone.forID(tz);
      }
      throw deserializationContext.mappingException("Expected JSON String");
    }
  }

  private static class DateTimeZoneSerializer extends StdScalarSerializer<DateTimeZone> {
    public DateTimeZoneSerializer() { super(DateTimeZone.class); }

    public void serialize(DateTimeZone value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      String tz = value.getID();
      jgen.writeString(tz);
    }
  }

  @Override
  public String getModuleName() {
    return "datetimezone";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }
}
