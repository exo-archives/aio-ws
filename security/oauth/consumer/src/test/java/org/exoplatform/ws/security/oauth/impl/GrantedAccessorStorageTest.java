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

package org.exoplatform.ws.security.oauth.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorage;
import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorageProperties;
import org.exoplatform.ws.security.oauth.storage.impl.InmemoryGrantedAccessorStorage;
import org.exoplatform.ws.security.oauth.storage.impl.TreeFileGrantedAccessorStorage;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GrantedAccessorStorageTest extends TestCase {

  private GrantedAccessorStorage storage;

  private OAuthConsumer          consumer;

  private static final int       SIZE = 1000;

  private File                   rootDir;

  public void setUp() throws Exception {
    rootDir = new File("target/oauth");
    rootDir.mkdirs();
    storage = new TreeFileGrantedAccessorStorage();
//    storage = new InmemoryGrantedAccessorStorage();
    GrantedAccessorStorageProperties props = new GrantedAccessorStorageProperties();
    props.setProperty("path", rootDir.getAbsolutePath());
    props.setProperty("cacheSize", "256");
    storage.init(props);
    consumer = new OAuthConsumer(null,
                                 "consumer",
                                 "secret",
                                 new OAuthServiceProvider("http://localhost/request_token",
                                                          "http://localhost/auth",
                                                          "http://localhost/access_token"));
  }
  
  public void testSimple() throws Exception {
    long start = System.currentTimeMillis();
    List<OAuthAccessor> as = createAccessors();
    initStorage(as);
    for (OAuthAccessor a : as)
      assertNotNull(storage.getAccessor(a.accessToken, a.tokenSecret));
    for (OAuthAccessor a : as)
      storage.removeAccessor(a.accessToken);
    for (OAuthAccessor a : as)
      assertNull(storage.getAccessor(a.accessToken, a.tokenSecret));
    long end = System.currentTimeMillis();
    System.out.println("testSimple: " + (end - start) + " ms.");
  }

  public void testCache() throws Exception {
    List<OAuthAccessor> as = createAccessors();
    initStorage(as);
    long start = System.currentTimeMillis();
    int iteration = SIZE * 10;
    Random random = new Random();
    for (int i = 0; i < iteration; i++) {
      int indx = random.nextInt(SIZE);
      OAuthAccessor accessor = as.get(indx);
      assertNotNull(storage.getAccessor(accessor.accessToken, accessor.tokenSecret));
    }
    long end = System.currentTimeMillis();
    System.out.println("testCache: " + (end - start) + " ms.");

    // remove created tickets
    for (OAuthAccessor accessor : as)
      storage.removeAccessor(accessor.accessToken);
  }

  public void testConcurrent() throws Exception {
    long start = System.currentTimeMillis();
    int threads = 100;

    CountDownLatch cdl = new CountDownLatch(threads);
    for (int i = 0; i < threads; i++)
      new ThreadLuncher(cdl).start();

    cdl.await();
    long end = System.currentTimeMillis();
    System.out.println("testConcurrent: " + (end - start) + " ms.");
  }

  private class ThreadLuncher extends Thread {
    private CountDownLatch cdl;

    public ThreadLuncher(CountDownLatch cdl) {
      this.cdl = cdl;
    }

    @Override
    public void run() {
      List<OAuthAccessor> as = new ArrayList<OAuthAccessor>();
      for (int i = 0; i < 100/*SIZE*/; i++)
        as.add(createAccessor());
      
      try {
        for (OAuthAccessor a : as)
          storage.addAccessor(a);
        for (OAuthAccessor a : as)
          storage.getAccessor(a.accessToken, a.tokenSecret);
        for (OAuthAccessor accessor : as)
          storage.removeAccessor(accessor.accessToken);
        cdl.countDown();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  private List<OAuthAccessor> createAccessors() {
    List<OAuthAccessor> as = new ArrayList<OAuthAccessor>(SIZE);
    for (int i = 0; i < SIZE; i++)
      as.add(createAccessor());
    return as;
  }

  private void initStorage(List<OAuthAccessor> accessors) throws Exception {
    for (OAuthAccessor accessor : accessors)
      storage.addAccessor(accessor);
  }

  private OAuthAccessor createAccessor() {
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    String userId = "exo" + System.currentTimeMillis();
    accessor.accessToken = UUID.randomUUID().toString();
    accessor.tokenSecret = UUID.randomUUID().toString();
    accessor.setProperty("userId", userId);
    List<String> r = new ArrayList<String>(2);
    r.add("exo1");
    r.add("exo2");
    r.add("exo3");
    accessor.setProperty("roles", r);
    return accessor;
  }
}
