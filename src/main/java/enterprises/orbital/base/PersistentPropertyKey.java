package enterprises.orbital.base;

/**
 * Map an instance of an arbitrary class to a property name which should be used for that class.
 *
 * @param <A>
 *          a class which should behave like a key when used with PersistentProperty.
 */
public interface PersistentPropertyKey<A> {
  /**
   * Return the key which should be used for this object.
   * 
   * @param field
   *          the object to be mapped to a key.
   * @return string key value.
   */
  public String getPeristentPropertyKey(A field);
}
