/**
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ws.security.oauth.storage.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthProblemException;

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorage;
import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorageProperties;

/**
 * Tree file accessors storage. Each accessor keeps in separate file, part of
 * <tt>accessor.accessToken</tt> will be used for generate file path for storing
 * accessor, see {@link #buidlFilePath(String)}.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class TreeFileGrantedAccessorStorage extends GrantedAccessorStorage {

  /**
   * Logger.
   */
  private static final Log LOG            = ExoLogger.getLogger(TreeFileGrantedAccessorStorage.class.getName());

  /**
   * Max in memory cache size.
   */
  private static final int MAX_CACHE_SIZE = 1024;

  /**
   * Root directory for storing accessors file.
   */
  private File             rootDir;

  /**
   * True if storage already initialized, see
   * {@link #init(GrantedAccessorStorageProperties)}
   */
  private boolean          initialized    = false;

  /**
   * In memory cache for skipping most often requested accessors at the moment.
   */
  private CacheMap         cache;

  /**
   * {@inheritDoc}
   */
  public synchronized void addAccessor(OAuthAccessor accessor) throws OAuthProblemException {
    File file = new File(rootDir, buidlFilePath(accessor.accessToken));
    file.getParentFile().mkdirs();
    try {
      writeAccessor(file, accessor);
      addInCache(accessor);
    } catch (IOException e) {
      if (LOG.isDebugEnabled())
        e.printStackTrace();
      throw new OAuthProblemException("Supplied accessor can not be saved in storage.");
    }
  }

  /**
   * {@inheritDoc}
   */
  public /*synchronized*/ OAuthAccessor getAccessor(String token, String secret) throws OAuthProblemException {
    // Try obtain from cache first
    OAuthAccessor accessor = fromCache(token);

    if (accessor == null) {
      File file = new File(rootDir, buidlFilePath(token));

      if (!file.exists())
        return null;

      try {
        accessor = readAccessor(file);
      } catch (IOException e) {
        if (LOG.isDebugEnabled())
          e.printStackTrace();
        throw new OAuthProblemException("Can't restore accessor from storage");
      }
    }
    // validate secret token
    checkSecretToken(accessor, secret);
    return accessor;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void init(GrantedAccessorStorageProperties properties) throws OAuthProblemException {
    // Avoid twice initialization
    if (initialized)
      return;

    int cacheSize = properties.getProperty("cacheSize") != null ? Integer.parseInt(properties.getProperty("cacheSize"))
                                                               : MAX_CACHE_SIZE;
    if (cacheSize > 0)
      cache = new CacheMap(cacheSize);
    else
      LOG.info("Initialized without cache.");

    String rootPath = properties.getProperty("path");
    if (rootPath == null)
      throw new OAuthProblemException("Root directory for saving granted tickets is not specified,"
          + " check configuration.");

    rootDir = new File(rootPath);
    if (rootDir.exists()) {
      if (!rootDir.isDirectory())
        throw new OAuthProblemException("File " + rootDir.getAbsolutePath()
            + " exists but it is not directory.");
    } else {
      if (rootDir.mkdirs())
        LOG.info("Create root directory for oauth tickets " + rootDir.getAbsolutePath());
      else
        LOG.warn("Can't create root directory for oauth tickets " + rootDir.getAbsolutePath());
    }

    initialized = true;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void removeAccessor(String token) throws OAuthProblemException {
    removeFromCache(token);

    File file = new File(rootDir, buidlFilePath(token));
    if (file.exists()) {
      if (file.delete())
        deleteParent(file.getParentFile());
    }
  }

  /**
   * Try obtain accessor from cache if cache is used.
   * 
   * @param token access token
   * @return accessor or null if it not found in cache or cache is not used
   */
  private OAuthAccessor fromCache(String token) {
    if (cache != null)
      return cache.get(token);
    return null;
  }

  /**
   * Add accessor in cache if cache is in use.
   * 
   * @param accessor accessor
   */
  private void addInCache(OAuthAccessor accessor) {
    if (cache != null)
      cache.put(accessor.accessToken, accessor);
  }

  /**
   * Remove accessor from cache if cache is in use.
   * 
   * @param token access token
   */
  private void removeFromCache(String token) {
    if (cache != null)
      cache.remove(token);
  }

  /**
   * Write supplied accessor in file.
   * 
   * @param file file for writing
   * @param accessor accessor
   * @throws IOException if any i/o errors occurs
   */
  private static void writeAccessor(File file, OAuthAccessor accessor) throws IOException {
    ObjectOutputStream oos = null;
    try {
      OutputStream out = new FileOutputStream(file);
      oos = new ObjectOutputStream(out);
      oos.writeObject(accessor);
    } catch (FileNotFoundException e) {
      if (LOG.isDebugEnabled())
        e.printStackTrace();
      throw new IOException("Can't open file " + file.getAbsolutePath());
    } finally {
      if (oos != null)
        oos.close();
    }
  }

  /**
   * Read accessor from file.
   * 
   * @param file file for reading
   * @return accessor
   * @throws IOException if any i/o errors occurs
   */
  private static OAuthAccessor readAccessor(File file) throws IOException {
    ObjectInputStream ois = null;
    try {
      InputStream in = new FileInputStream(file);
      ois = new ObjectInputStream(in);
      OAuthAccessor accessor = (OAuthAccessor) ois.readObject();
      return accessor;
    } catch (ClassNotFoundException e) {
      if (LOG.isDebugEnabled())
        e.printStackTrace();
      throw new IOException("Can't restore accessor from file " + file.getAbsolutePath()
          + ", not found class " + OAuthAccessor.class.getName());
    } finally {
      if (ois != null)
        ois.close();
    }
  }

  /**
   * Build file path for store accessor.
   * 
   * @param token OAuthAccessor access token
   * @return file path, IllegalArgumentException can be thrown if
   *         accessor.accessToken has invalid form
   */
  protected String buidlFilePath(String token) {
    // Build file path for uuid:urn representation of access token.
    // For details see http://www.ietf.org/rfc/rfc4122.txt .
    // Example string representation of UUID:
    // 68aef511-15f2-44ec-a592-4c7e8c5fdf60
    if (token.split("-").length != 5)
      throw new IllegalArgumentException("Invalid UUID string: " + token);

    StringBuffer sb = new StringBuffer();
    int strlen = token.length();
    for (int c = 0, i = 0; i < strlen; i++) {
      // For directory path use first 8 characters, before first '-'.
      // With UUID example above directory structure:
      // root_directory/6/8/a/e/f/5/1/1
      c = token.charAt(i);
      if (c == '-')
        break;
      if (sb.length() > 0)
        sb.append(File.separatorChar);

      sb.append((char) c);
    }
    // Finally add accessToken as file name
    sb.append(token);
    return sb.toString();
  }

  protected boolean deleteParent(File file) {
    boolean res = false;
    String path = file.getAbsolutePath();
    String rootPath = rootDir.getAbsolutePath();
    if (path.startsWith(rootPath) && path.length() > rootPath.length())
      if (file.isDirectory()) {
        String[] ls = file.list();
        if (ls.length <= 0) {
          if (res = file.delete())
            res = deleteParent(new File(file.getParent()));
          else
            LOG.warn("Can't remove file " + path);
        }
      } else
        LOG.warn("Parent is a file " + path);
    return res;
  }

  /**
   * In memory cache. NOTE Cache is not synchronized.
   */
  private static class CacheMap extends LinkedHashMap<String, OAuthAccessor> {

    /**
     * Generated byEclipse.
     */
    private static final long  serialVersionUID = 4043563002416887500L;

    /**
     * Map load-factor.
     */
    private static final float LOAD_FACTOR      = 0.75F;

    /**
     * Max cache size.
     */
    private final int          cacheSize;

    /**
     * @param cacheSize max cache size
     */
    public CacheMap(int cacheSize) {
      // create LinkedHashMap with access-order ordering
      super(cacheSize, LOAD_FACTOR, true);
      this.cacheSize = cacheSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<String, OAuthAccessor> eldest) {
      // Cache must not grow, least recently accessed entry will be removed
      return size() >= cacheSize;
    }

  }

}
