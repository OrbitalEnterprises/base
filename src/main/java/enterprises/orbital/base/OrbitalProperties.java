package enterprises.orbital.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Simple java properties management. Property files can be added dynamically and will be loaded into an internal global Properties object. This class also
 * provides other utilities such as:
 * <ul>
 * <li>A call to determine whether asserts have been enabled at runtime.
 * <li>A time generator to serve time stamps. Can be replaced for unit tests.
 * <li>Typed property getters for boolean and longs.
 * </ul>
 * This class is often used in conjunction with PersistentProperty to provide for persistent properties which can be backed by default values provided from
 * properties files.
 */
public abstract class OrbitalProperties {

  private static final Set<String> loadedFiles      = new HashSet<String>();
  private static final Properties  globalProperties = new Properties();
  private static boolean           assertsEnabled   = false;

  {
    // Intentional side effect if asserts are enabled
    assert assertsEnabled = true;
  }

  public static boolean isAssertEnabled() {
    return assertsEnabled;
  }

  /**
   * Add a properties file to the global set of properties. Other modules should normally call this method early in their life cycle to add properties as
   * needed. Note that we only allow each unique path to be loaded once to avoid excessive loading and overwrites. As a result, modules should use unique names
   * for their load files.
   * 
   * @param path
   *          path to properties file to load.
   * @throws IOException
   *           if an error occurs while loading the file
   */
  public static final void addPropertyFile(String path) throws IOException {
    synchronized (loadedFiles) {
      if (loadedFiles.contains(path)) return;
      loadedFiles.add(path);
    }
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      InputStream build = loader.getResourceAsStream(path);
      if (build != null) {
        globalProperties.load(build);
        build.close();
      }
    } catch (IOException e) {
      synchronized (loadedFiles) {
        loadedFiles.remove(path);
        throw e;
      }
    }
  }

  /**
   * Generate a standard property name for some attribute of a given class. This is used to normalize all class-specific property names to the form:
   * "FQCN.attribute". For example, calling this method with (org.evekit.model.corporation.MemberTracking.class, "max_results") will yield:
   * org.evekit.model.corporation.MemberTracking.attr.max_results
   * 
   * @param caller
   *          the class to be used to form the fully qualified named bit.
   * @param attribute
   *          the attribute name to tag onto the class.
   * @return a normalized class-specific property name for the given class and attribute.
   */
  public static final String getPropertyName(Class<?> caller, String attribute) {
    return caller.getName() + ".attr." + attribute;
  }

  /**
   * Convenience function to return a value in the range [1, max] where:
   * <ul>
   * <li>max is returned if "provided" &lt; 1 (that is, &lt; 1 is treated as use available max);
   * <li>max is returned if "provided" &gt; max;
   * <li>otherwise, "provided" is returned.
   * </ul>
   * 
   * @param provided
   *          value provided by a caller
   * @param max
   *          maximum value to allow.
   * @return a value in the range [1, max] as defined above.
   */
  public static final int getNonzeroLimited(int provided, int max) {
    if (provided < 1) provided = max;
    return Math.min(provided, max);
  }

  /**
   * Interface to generate a time stamp. Can be replaced for unit tests.
   */
  public static interface TimeGenerator {
    public long getTime();
  }

  private static TimeGenerator timeGenerator;

  public static void setTimeGenerator(TimeGenerator tg) {
    timeGenerator = tg;
  }

  static {
    setTimeGenerator(new TimeGenerator() {

      @Override
      public long getTime() {
        return System.currentTimeMillis();
      }

    });
  }

  /**
   * Return current time as returned by the current time generator.
   * 
   * @return current time.
   */
  public static long getCurrentTime() {
    return timeGenerator.getTime();
  }

  public static Date getCurrentDate() {
    return new Date(getCurrentTime());
  }

  public static String getGlobalProperty(String key) {
    return globalProperties.getProperty(key);
  }

  public static String getGlobalProperty(String key, String def) {
    return globalProperties.getProperty(key, def);
  }

  public static boolean getBooleanGlobalProperty(String key) {
    return Boolean.valueOf(globalProperties.getProperty(key));
  }

  public static boolean getBooleanGlobalProperty(String key, boolean def) {
    return Boolean.valueOf(globalProperties.getProperty(key, String.valueOf(def)));
  }

  public static long getLongGlobalProperty(String key) {
    return Long.valueOf(globalProperties.getProperty(key));
  }

  public static long getLongGlobalProperty(String key, long def) {
    return Long.valueOf(globalProperties.getProperty(key, String.valueOf(def)));
  }

}
