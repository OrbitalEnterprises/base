package enterprises.orbital.base;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Convenience class for hashing strings or byte buffers. Regular digests use SHA-256. "Fast" digests use MD5.
 */
public class Stamper {
  private static final Logger                     log             = Logger.getLogger(Stamper.class.getName());
  private static final ThreadLocal<MessageDigest> digestCache     = new ThreadLocal<MessageDigest>() {
                                                                    @Override
                                                                    protected MessageDigest initialValue() {
                                                                      try {
                                                                        return MessageDigest.getInstance("SHA-256");
                                                                      } catch (NoSuchAlgorithmException e) {
                                                                        // This should never happen.
                                                                        log.severe(e.toString());
                                                                        return null;
                                                                      }
                                                                    }
                                                                  };

  private static final ThreadLocal<MessageDigest> fastDigestCache = new ThreadLocal<MessageDigest>() {
                                                                    @Override
                                                                    protected MessageDigest initialValue() {
                                                                      try {
                                                                        return MessageDigest.getInstance("MD5");
                                                                      } catch (NoSuchAlgorithmException e) {
                                                                        // This should never happen.
                                                                        log.severe(e.toString());
                                                                        return null;
                                                                      }
                                                                    }
                                                                  };

  public static String digest(String input) {
    return innerDigest(getDigest(), "%032x", input.getBytes());
  }

  public static String digest(ByteBuffer input) {
    return innerDigest(getDigest(), "%032x", input);
  }

  public static String digest(byte[] input) {
    return innerDigest(getDigest(), "%032x", input);
  }

  public static String fastDigest(String input) {
    return innerDigest(getFastDigest(), "%016x", input.getBytes());
  }

  public static String fastDigest(ByteBuffer input) {
    return innerDigest(getFastDigest(), "%016x", input);
  }

  public static String fastDigest(byte[] input) {
    return innerDigest(getFastDigest(), "%016x", input);
  }

  private static String innerDigest(MessageDigest md, String fmt, byte[] input) {
    md.reset();
    md.update(input);
    return String.format(fmt, new BigInteger(1, md.digest())).toUpperCase();
  }

  private static String innerDigest(MessageDigest md, String fmt, ByteBuffer input) {
    md.reset();
    md.update(input);
    return String.format(fmt, new BigInteger(1, md.digest())).toUpperCase();
  }

  public static MessageDigest getDigest() {
    return digestCache.get();
  }

  public static MessageDigest getFastDigest() {
    return fastDigestCache.get();
  }
}
