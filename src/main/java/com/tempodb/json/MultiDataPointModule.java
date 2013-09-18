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
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.joda.time.DateTime;

import com.tempodb.MultiDataPoint;


public class MultiDataPointModule extends SimpleModule {
  public MultiDataPointModule() {
    addDeserializer(MultiDataPoint.class, new MultiDataPointDeserializer());
    addSerializer(MultiDataPoint.class, new MultiDataPointSerializer());
  }

  private static class MultiDataPointDeserializer extends StdScalarDeserializer<MultiDataPoint> {
    public MultiDataPointDeserializer() { super(MultiDataPoint.class); }

    @Override
    public MultiDataPoint deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      JsonNode root = parser.readValueAsTree();
      JsonNode idNode = root.get("id");
      JsonNode keyNode = root.get("key");
      JsonNode timestampNode = root.get("t");
      JsonNode valueNode = root.get("v");

      if(idNode == null && keyNode == null) {
        throw context.mappingException("Missing 'id' and 'key' field in MultiDataPoint. One of these fields must be present.");
      }

      if(timestampNode == null) {
        throw context.mappingException("Missing 't' field in MultiDataPoint.");
      }

      if(valueNode == null) {
        throw context.mappingException("Missing 'v' field in MultiDataPoint.");
      }

      String id = null;
      if(idNode != null) {
        if(!idNode.isTextual()) {
          throw context.mappingException("'id' field should be a string");
        }
        id = idNode.asText();
      }

      String key = null;
      if(keyNode != null) {
        if(!keyNode.isTextual()) {
          throw context.mappingException("'key' field should be a string");
        }
        key = keyNode.asText();
      }

      if(!valueNode.isNumber()) {
        throw context.mappingException("'v' field should be a number");
      }

      Number value = valueNode.numberValue();
      DateTime timestamp = Json.getObjectMapper()
                               .reader()
                               .with(context.getTimeZone())
                               .withType(DateTime.class)
                               .readValue(timestampNode);

      if(id == null && key == null) {
        throw context.mappingException("One of either the 'id' and 'key' fields must be non-null.");
      }

      MultiDataPoint datapoint = null;
      if(id != null) {
        datapoint = MultiDataPoint.forId(id, timestamp, value);
      } else {
        datapoint = MultiDataPoint.forKey(key, timestamp, value);
      }
      return datapoint;
    }
  }

  private static class MultiDataPointSerializer extends StdScalarSerializer<MultiDataPoint> {
    public MultiDataPointSerializer() { super(MultiDataPoint.class); }

    @Override
    public void serialize(MultiDataPoint mdp, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      String id = mdp.getId();
      String key = mdp.getKey();
      DateTime timestamp = mdp.getTimestamp();
      Number value = mdp.getValue();

      jgen.writeStartObject();
      if(id != null) {
        jgen.writeObjectField("id", id);
      }

      if(key != null) {
        jgen.writeObjectField("key", key);
      }

      jgen.writeObjectField("t", timestamp);
      jgen.writeObjectField("v", value);
      jgen.writeEndObject();
    }
  }
}
