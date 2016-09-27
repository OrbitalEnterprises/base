package enterprises.orbital.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utilities for interacting with resource files in the class path.
 */
public class ResourceUtil {

  /**
   * Interface to implement for iteration of a collection of resources.
   */
  public static interface OnEntry {
    public void nextEntry(
                          String name,
                          InputStream entry)
      throws IOException;
  }

  /**
   * Assume a given path is a directory and retrieve it's children.
   * 
   * @param path
   *          resource path
   * @return an array of immediate children, or null
   * @throws URISyntaxException
   *           if an error occurs handling a file URI
   * @throws IOException
   *           if an IO error occurs while reading children list
   */
  protected static String[] getResourceListing(
                                               String path)
    throws URISyntaxException, IOException {
    URL dirURL = Thread.currentThread().getContextClassLoader().getResource(path);
    if (dirURL != null) {
      if (dirURL.getProtocol().equals("file")) {
        /* A file path: easy enough */
        return new File(dirURL.toURI()).list();
      }

      if (dirURL.getProtocol().equals("jar")) {
        /* A JAR path */
        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out only the JAR file
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
        Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
        while (entries.hasMoreElements()) {
          String name = entries.nextElement().getName();
          if (name.startsWith(path)) { // filter according to the path
            String entry = name.substring(path.length());
            int checkSubdir = entry.indexOf("/");
            if (checkSubdir >= 0) {
              // if it is a subdirectory, we just return the directory name
              entry = entry.substring(0, checkSubdir);
            }
            if (entry.length() > 0) result.add(entry);
          }
        }
        jar.close();
        return result.toArray(new String[result.size()]);
      }
    }

    // If we can't find the path, or we can't handle the protocol, then return null.
    return null;
  }

  /**
   * Iterate over all non-directory resources from a given resource directory root.
   * 
   * @param root
   *          root resource directory. This must always be the name of a directory.
   * @param fileSuffix
   *          unique suffix indicating a file instead of a directory.
   * @param toDo
   *          callback to invoke on each non-directory resource.
   * @throws IOException
   *           if an error occurs while reading resources
   * @throws URISyntaxException
   *           if an error occurs attempting to construct the path for the resource listing root (this should never happen).
   */
  public static void forAllEntries(
                                   String root,
                                   String fileSuffix,
                                   OnEntry toDo)
    throws IOException, URISyntaxException {
    Queue<String> path = new LinkedList<String>();
    if (!root.endsWith("/")) root = root + "/";
    String[] paths = getResourceListing(root);
    if (paths != null) {
      for (String i : paths) {
        path.add(root + i);
      }
    }
    while (!path.isEmpty()) {
      String next = path.remove();
      if (!next.endsWith(fileSuffix)) {
        // This MAY be a directory, but it's definitely not a file we're looking for.
        String nextRoot = next.endsWith("/") ? next : next + "/";
        paths = getResourceListing(nextRoot);
        if (paths != null) {
          for (String i : paths) {
            path.add(nextRoot + i);
          }
        }
      } else {
        // Ends with the suffix we want, read it as a file if we can open the stream properly.
        InputStream nextData = Thread.currentThread().getContextClassLoader().getResourceAsStream(next);
        if (nextData != null) {
          toDo.nextEntry(next, nextData);
          nextData.close();
        }
      }
    }
  }

}
