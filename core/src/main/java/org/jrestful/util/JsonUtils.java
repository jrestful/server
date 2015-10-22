package org.jrestful.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON serialization and deserialization.
 */
public final class JsonUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  public interface ObjectMapperDecorator {
    
    void decorate(ObjectMapper objectMapper);
    
  }

  public static class Json {

    private final Object object;
    
    private final ObjectMapperDecorator decorator;

    private Json(Object object, ObjectMapperDecorator decorator) {
      this.object = object;
      this.decorator = decorator;
    }

    public String asString() {
      try {
        return newObjectMapper(decorator).writeValueAsString(object);
      } catch (JsonProcessingException e) {
        LOGGER.debug("An error occurred while serializing an object", e);
        return null;
      }
    }

    public byte[] asBytes() {
      try {
        return newObjectMapper(decorator).writeValueAsString(object).getBytes(UTF_8);
      } catch (JsonProcessingException e) {
        LOGGER.debug("An error occurred while serializing an object", e);
        return null;
      }
    }

  }

  public static Json toJson(Object object) {
    return toJson(object, null);
  }

  public static Json toJson(Object object, ObjectMapperDecorator decorator) {
    return new Json(object, decorator);
  }

  public static <T> T fromJson(InputStream json, Class<T> type) {
    return fromJson(json, null, type);
  }

  public static <T> T fromJson(InputStream json, ObjectMapperDecorator decorator, Class<T> type) {
    try {
      return newObjectMapper(decorator).readValue(json, type);
    } catch (IOException e) {
      LOGGER.debug("An error occurred while deserializing an object", e);
      return null;
    }
  }

  public static <T> T fromJson(String json, Class<T> type) {
    return fromJson(json, null, type);
  }

  public static <T> T fromJson(String json, ObjectMapperDecorator decorator, Class<T> type) {
    try {
      return newObjectMapper(decorator).readValue(json, type);
    } catch (IOException e) {
      LOGGER.debug("An error occurred while deserializing an object", e);
      return null;
    }
  }

  public static <T> T fromJson(byte[] json, Class<T> type) {
    return fromJson(json, null, type);
  }

  public static <T> T fromJson(byte[] json, ObjectMapperDecorator decorator, Class<T> type) {
    try {
      return newObjectMapper(decorator).readValue(new String(json, UTF_8), type);
    } catch (IOException e) {
      LOGGER.debug("An error occurred while deserializing an object", e);
      return null;
    }
  }
  
  private static ObjectMapper newObjectMapper(ObjectMapperDecorator decorator) {
    ObjectMapper mapper = new ObjectMapper();
    if (decorator != null) {
      decorator.decorate(mapper);
    }
    return mapper;
  }

  private JsonUtils() {
  }

}
