package org.jrestful.util;

import java.nio.charset.Charset;

import org.springframework.security.crypto.codec.Base64;

public final class Base64Utils {

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  public abstract static class Base64Processor {

    protected final byte[] bytes;

    protected Base64Processor(byte[] bytes) {
      this.bytes = bytes;
    }

    public abstract byte[] asBytes();

    public String asString() {
      return new String(asBytes(), UTF_8);
    }

  }

  private static class Base64Encoder extends Base64Processor {

    private Base64Encoder(byte[] bytes) {
      super(bytes);
    }

    @Override
    public byte[] asBytes() {
      return Base64.encode(bytes);
    }

  }

  private static class Base64Decoder extends Base64Processor {

    private Base64Decoder(byte[] bytes) {
      super(bytes);
    }

    @Override
    public byte[] asBytes() {
      return Base64.decode(bytes);
    }

  }

  public static Base64Processor encode(String string) {
    return encode(string.getBytes(UTF_8));
  }

  public static Base64Processor encode(byte[] bytes) {
    return new Base64Encoder(bytes);
  }

  public static Base64Processor decode(String string) {
    return decode(string.getBytes(UTF_8));
  }

  public static Base64Processor decode(byte[] bytes) {
    return new Base64Decoder(bytes);
  }

  private Base64Utils() {
  }

}
