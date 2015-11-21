package enterprises.orbital.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple persistent key-value store. This class is similar to Java Properties except that values may be backed by a persistent property provider. The default
 * provider is in-memory, which is not persistent at all and adds little value over simply using OrbitalProperties (or Properties directly). However, by
 * substituting a provider which persists data, you can use properties which persist between sessions.
 * 
 * For convenience, this class is also designed to work with a key generator for arbitrary classes called PersistentPropertyKey. This class allows you to map
 * arbitrary objects to string keys, perhaps associated with a particular name space. See README.md for an example.
 */
public class PersistentProperty {

  /**
   * By default, we provide an in-memory "persistent" provider. You'll want to replace this if you actually want properties to persist between instances. You'll
   * want to change the provider as one of the first init actions taken.
   */
  protected static PersistentPropertyProvider provider = new InMemoryPersistentPropertyProvider();

  public static void setProvider(PersistentPropertyProvider p) {
    if (p == null) {
      // Default back to in-memory provider
      p = new InMemoryPersistentPropertyProvider();
    }
    provider = p;
  }

  /**
   * Property name. This should also be the unique ID for this value when stored.
   */
  protected String propertyName;
  protected String propertyValue;

  public PersistentProperty() {}

  public PersistentProperty(String propertyName, String propertyValue) {
    super();
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }

  @Override
  public String toString() {
    return "PersistentProperty [propertyName=" + propertyName + ", propertyValue=" + propertyValue + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
    result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PersistentProperty other = (PersistentProperty) obj;
    if (propertyName == null) {
      if (other.propertyName != null) return false;
    } else if (!propertyName.equals(other.propertyName)) return false;
    if (propertyValue == null) {
      if (other.propertyValue != null) return false;
    } else if (!propertyValue.equals(other.propertyValue)) return false;
    return true;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getPropertyValue() {
    return propertyValue;
  }

  public static String getProperty(String key, String defValue) {
    try {
      return getProperty(key);
    } catch (NoPersistentPropertyException e) {
      return defValue;
    }
  }

  public static <B, A extends PersistentPropertyKey<B>> String getProperty(A ns, B key, String defValue) {
    return getProperty(ns.getPeristentPropertyKey(key), defValue);
  }

  public static List<PersistentProperty> getAll() {
    return provider.retrieveAll();
  }

  public static String getProperty(final String key) throws NoPersistentPropertyException {
    PersistentProperty val = provider.get(key);
    if (val == null) throw new NoPersistentPropertyException();
    return val.getPropertyValue();
  }

  public static <B, A extends PersistentPropertyKey<B>> String getProperty(A ns, B key) throws NoPersistentPropertyException {
    return getProperty(ns.getPeristentPropertyKey(key));
  }

  public static String setProperty(final String key, final String value) {
    return provider.set(key, value);
  }

  public static <B, A extends PersistentPropertyKey<B>> String setProperty(A ns, B key, String value) {
    return setProperty(ns.getPeristentPropertyKey(key), value);
  }

  public static String removeProperty(final String key) {
    return provider.remove(key);
  }

  public static <B, A extends PersistentPropertyKey<B>> String removeProperty(A ns, B key) {
    return removeProperty(ns.getPeristentPropertyKey(key));
  }

  public static String getPropertyWithFallback(String key) {
    try {
      return getProperty(key);
    } catch (NoPersistentPropertyException e) {
      // Expected, ignore.
    }
    return OrbitalProperties.getGlobalProperty(key);
  }

  public static <B, A extends PersistentPropertyKey<B>> String getPropertyWithFallback(A ns, B key) {
    return getPropertyWithFallback(ns.getPeristentPropertyKey(key));
  }

  public static String getPropertyWithFallback(String key, String def) {
    return getProperty(key, OrbitalProperties.getGlobalProperty(key, def));
  }

  public static <B, A extends PersistentPropertyKey<B>> String getPropertyWithFallback(A ns, B key, String def) {
    String ky = ns.getPeristentPropertyKey(key);
    return getProperty(ky, OrbitalProperties.getGlobalProperty(ky, def));
  }

  public static boolean getBooleanPropertyWithFallback(String key) {
    return Boolean.valueOf(getProperty(key, OrbitalProperties.getGlobalProperty(key)));
  }

  public static <B, A extends PersistentPropertyKey<B>> boolean getBooleanPropertyWithFallback(A ns, B key) {
    String ky = ns.getPeristentPropertyKey(key);
    return Boolean.valueOf(getProperty(ky, OrbitalProperties.getGlobalProperty(ky)));
  }

  public static boolean getBooleanPropertyWithFallback(String key, boolean def) {
    return Boolean.valueOf(getProperty(key, OrbitalProperties.getGlobalProperty(key, String.valueOf(def))));
  }

  public static <B, A extends PersistentPropertyKey<B>> boolean getBooleanPropertyWithFallback(A ns, B key, boolean def) {
    String ky = ns.getPeristentPropertyKey(key);
    return Boolean.valueOf(getProperty(ky, OrbitalProperties.getGlobalProperty(ky, String.valueOf(def))));
  }

  public static long getLongPropertyWithFallback(String key) {
    return Long.valueOf(getProperty(key, OrbitalProperties.getGlobalProperty(key)));
  }

  public static <B, A extends PersistentPropertyKey<B>> long getLongPropertyWithFallback(A ns, B key) {
    String ky = ns.getPeristentPropertyKey(key);
    return Long.valueOf(getProperty(ky, OrbitalProperties.getGlobalProperty(ky)));
  }

  public static long getLongPropertyWithFallback(String key, long def) {
    return Long.valueOf(getProperty(key, OrbitalProperties.getGlobalProperty(key, String.valueOf(def))));
  }

  public static <B, A extends PersistentPropertyKey<B>> long getLongPropertyWithFallback(A ns, B key, long def) {
    String ky = ns.getPeristentPropertyKey(key);
    return Long.valueOf(getProperty(ky, OrbitalProperties.getGlobalProperty(ky, String.valueOf(def))));
  }

  public static int getIntegerPropertyWithFallback(String key) {
    return Integer.valueOf(getProperty(key, OrbitalProperties.getGlobalProperty(key)));
  }

  public static <B, A extends PersistentPropertyKey<B>> int getIntegerPropertyWithFallback(A ns, B key) {
    String ky = ns.getPeristentPropertyKey(key);
    return Integer.valueOf(getProperty(ky, OrbitalProperties.getGlobalProperty(ky)));
  }

  public static int getIntegerPropertyWithFallback(String key, int def) {
    return Integer.valueOf(getProperty(key, OrbitalProperties.getGlobalProperty(key, String.valueOf(def))));
  }

  public static <B, A extends PersistentPropertyKey<B>> int getIntegerPropertyWithFallback(A ns, B key, int def) {
    String ky = ns.getPeristentPropertyKey(key);
    return Integer.valueOf(getProperty(ky, OrbitalProperties.getGlobalProperty(ky, String.valueOf(def))));
  }

  /**
   * Default provider. You'll want to replace this if you want actual property persistence.
   */
  public static class InMemoryPersistentPropertyProvider implements PersistentPropertyProvider {
    protected Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());

    @Override
    public List<PersistentProperty> retrieveAll() {
      List<PersistentProperty> all = new ArrayList<PersistentProperty>();
      synchronized (properties) {
        for (Entry<String, String> val : properties.entrySet()) {
          all.add(new PersistentProperty(val.getKey(), val.getValue()));
        }
      }
      return all;
    }

    @Override
    public PersistentProperty get(String key) {
      synchronized (properties) {
        if (properties.containsKey(key)) return new PersistentProperty(key, properties.get(key));
        return null;
      }
    }

    @Override
    public String set(String key, String value) {
      return properties.put(key, value);
    }

    @Override
    public String remove(String key) {
      return properties.remove(key);
    }
  }
}
