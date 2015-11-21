package enterprises.orbital.base;

import java.util.List;

/**
 * Provider implementation for PersistentProperty. After implementing this interface, use PersistentProperty.setProvider to change the provider.
 */
public interface PersistentPropertyProvider {

  /**
   * Retrieve the list of all current persisted properties.
   * 
   * @return the list of all current persisted properties.
   */
  public List<PersistentProperty> retrieveAll();

  /**
   * Return the current value of the property with the given name.
   * 
   * @param key
   *          the name of the property to return.
   * @return the current value of the given property, or null if the given property does not exist.
   */
  public PersistentProperty get(String key);

  /**
   * Set the value of the given property.
   * 
   * @param key
   *          the name of the property to set.
   * @param value
   *          the value for the property.
   * @return the previous value of the property, or null if the property did not previously exist.
   */
  public String set(String key, String value);

  /**
   * Remove the property with the given name.
   * 
   * @param key
   *          the name of the property to remove.
   * @return the value of the property, or null if the property does not exist.
   */
  public String remove(String key);

}
