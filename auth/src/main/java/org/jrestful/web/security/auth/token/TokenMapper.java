package org.jrestful.web.security.auth.token;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.jrestful.util.Base64Utils;
import org.jrestful.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper<K extends Serializable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenMapper.class);

  private static final String HMAC = "HmacSHA256";

  private static final String SEPARATOR = ".";

  private final Mac hmac;

  @Autowired
  public TokenMapper(@Value("${auth.tokenSecret}") String secret) {
    try {
      hmac = Mac.getInstance(HMAC);
      hmac.init(new SecretKeySpec(DatatypeConverter.parseBase64Binary(secret), HMAC));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("Failed to initialize HMAC", e);
    }
  }

  private synchronized byte[] createHmac(byte[] content) {
    return hmac.doFinal(content);
  }

  public String serialize(Token<K> tokenObject) {
    byte[] tokenBytes = JsonUtils.toJson(tokenObject).asBytes();
    byte[] hash = createHmac(tokenBytes);
    StringBuilder builder = new StringBuilder();
    builder.append(Base64Utils.encode(tokenBytes).asString());
    builder.append(SEPARATOR);
    builder.append(Base64Utils.encode(hash).asString());
    return builder.toString();
  }

  public Token<K> deserialize(String tokenString) {
    String[] parts = tokenString.split(Pattern.quote(SEPARATOR), -1);
    if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
      try {
        byte[] tokenBytes = Base64Utils.decode(parts[0]).asBytes();
        byte[] hash = Base64Utils.decode(parts[1]).asBytes();
        boolean validHash = Arrays.equals(createHmac(tokenBytes), hash);
        if (validHash) {
          @SuppressWarnings("unchecked")
          Token<K> tokenObject = JsonUtils.fromJson(tokenBytes, Token.class);
          if (tokenObject != null && tokenObject.isValid()) {
            return tokenObject;
          }
        }
      } catch (IllegalArgumentException e) {
        LOGGER.error("Authentication tampering attempt");
      }
    }
    return null;
  }

}
