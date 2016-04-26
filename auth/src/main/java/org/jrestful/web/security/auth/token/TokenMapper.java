package org.jrestful.web.security.auth.token;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.jrestful.util.Base64Utils;
import org.jrestful.util.JsonUtils;
import org.jrestful.web.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenMapper.class);

  private static final String HMAC = "HmacSHA256";

  private static final String SEPARATOR = ".";

  private final Mac hmac;

  @Autowired
  public TokenMapper(@Value("#{secProps['auth.tokenSecret']}") String secret) {
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

  public String serialize(Token tokenObject) {
    byte[] tokenBytes = JsonUtils.toJson(tokenObject).asBytes();
    return new StringBuilder() //
        .append(Base64Utils.encode(tokenBytes).asString()) //
        .append(SEPARATOR) //
        .append(Base64Utils.encode(createHmac(tokenBytes)).asString()) //
        .toString();
  }

  public <T extends Token> T deserialize(String tokenString, Class<T> tokenType, HttpServletRequest request) {
    String[] parts = tokenString.split(Pattern.quote(SEPARATOR), -1);
    if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
      try {
        byte[] tokenBytes = Base64Utils.decode(parts[0]).asBytes();
        if (Arrays.equals(createHmac(tokenBytes), Base64Utils.decode(parts[1]).asBytes())) {
          T tokenObject = JsonUtils.fromJson(tokenBytes, tokenType);
          if (tokenObject != null) {
            return tokenObject;
          } else {
            LOGGER.warn("Cannot deserialize " + tokenType.getSimpleName() + " from JSON: " + tokenString + " | " + HttpUtils.serialize(request));
          }
        } else {
          LOGGER.warn(tokenType.getSimpleName() + " embedded hash does not match with regenerated one: " + tokenString + " | " + HttpUtils.serialize(request));
        }
      } catch (IllegalArgumentException e) {
        LOGGER.warn("Cannot decode " + tokenType.getSimpleName() + " from Base64: " + tokenString + " | " + HttpUtils.serialize(request), e);
      }
    } else {
      LOGGER.warn("Cannot split " + tokenType.getSimpleName() + " into two parts: " + tokenString + " | " + HttpUtils.serialize(request));
    }
    return null;
  }

}
