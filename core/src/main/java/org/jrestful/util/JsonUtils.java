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

  public static class Json {

    private final Object object;

    private Json(Object object) {
      this.object = object;
    }

    public String asString() {
      try {
        return new ObjectMapper().writeValueAsString(object);
      } catch (JsonProcessingException e) {
        LOGGER.debug("An error occurred while serializing an object", e);
        return null;
      }
    }

    public byte[] asBytes() {
      try {
        return new ObjectMapper().writeValueAsString(object).getBytes(UTF_8);
      } catch (JsonProcessingException e) {
        LOGGER.debug("An error occurred while serializing an object", e);
        return null;
      }
    }

  }

  public static Json toJson(Object object) {
    return new Json(object);
  }

  public static <T> T fromJson(InputStream json, Class<T> type) {
    try {
      return new ObjectMapper().readValue(json, type);
    } catch (IOException e) {
      LOGGER.debug("An error occurred while deserializing an object", e);
      return null;
    }
  }

  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return new ObjectMapper().readValue(json, type);
    } catch (IOException e) {
      LOGGER.debug("An error occurred while deserializing an object", e);
      return null;
    }
  }

  public static <T> T fromJson(byte[] json, Class<T> type) {
    try {
      return new ObjectMapper().readValue(new String(json, UTF_8), type);
    } catch (IOException e) {
      LOGGER.debug("An error occurred while deserializing an object", e);
      return null;
    }
  }

  private JsonUtils() {
  }

}
