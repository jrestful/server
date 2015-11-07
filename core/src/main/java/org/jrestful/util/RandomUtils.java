package org.jrestful.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Operations on randoms.
 */
public final class RandomUtils {

  public static final String NUMBERS = "0123456789";

  public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

  public static String generate(String alphabet, int length) {
    Random random = new SecureRandom();
    StringBuilder out = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      out.append(alphabet.charAt(random.nextInt(alphabet.length())));
    }
    return out.toString();
  }

  public static String generateNumbers(int length) {
    return generate(NUMBERS, length);
  }

  public static String generateUppercaseLetters(int length) {
    return generate(UPPERCASE_LETTERS, length);
  }

  public static String generateLowercaseLetters(int length) {
    return generate(LOWERCASE_LETTERS, length);
  }

  public static String generateLetters(int length) {
    return generate(UPPERCASE_LETTERS + LOWERCASE_LETTERS, length);
  }

  public static String generateNumbersAndUppercaseLetters(int length) {
    return generate(NUMBERS + UPPERCASE_LETTERS, length);
  }

  public static String generateNumbersAndLowercaseLetters(int length) {
    return generate(NUMBERS + LOWERCASE_LETTERS, length);
  }

  public static String generateNumbersAndLetters(int length) {
    return generate(NUMBERS + LOWERCASE_LETTERS + UPPERCASE_LETTERS, length);
  }

  private RandomUtils() {
  }

}
