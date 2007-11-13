/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest;

import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class URIPatternTest extends TestCase {
  public void testParametrizedURIPattern0() throws Exception {
    // no parameters
    URIPattern pattern = new URIPattern("/level1/level2/");
    assertEquals(0, pattern.getParamNames().size());
    assertTrue(pattern.matches("/level1/level2/"));
    assertFalse(pattern.matches("/level11/level2/"));
    assertFalse(pattern.matches("/level11/level2/level3/"));
  }

  public void testParametrizedURIPattern() throws Exception {
    URIPattern pattern1 = new URIPattern("/{id1}/");
    assertTrue(pattern1.matches("/1/2/3/"));
    pattern1 = new URIPattern("/id1/");
    assertFalse(pattern1.matches("/id1/1/"));
    assertTrue(pattern1.matches("/id1/"));
    pattern1 = new URIPattern("/id1/{1}/2/");
    assertTrue(pattern1.matches("/id1/1/2/"));
    assertFalse(pattern1.matches("/id1/1/"));
    pattern1 = new URIPattern("/{id1}/1/");
    assertFalse(pattern1.matches("/id1/"));
  }

  public void testParametrizedURIPattern1() throws Exception {
    // one parameter
    URIPattern pattern = new URIPattern("/level1/level2/{id}/");
    assertEquals(1, pattern.getParamNames().size());
    assertEquals("id", pattern.getParamNames().iterator().next());
    assertTrue(pattern.matches("/level1/level2/test/"));
    assertFalse(pattern.matches("/level1/level2/test"));
    assertFalse(pattern.matches("/level1/level2"));
    assertFalse(pattern.matches("/level1/"));
    Map<String, String> params = pattern.parse("/level1/level2/test/");
    assertEquals(1, params.size());
    assertEquals("test", params.get("id"));
  }

  public void testParametrizedURIPattern2() throws Exception {
    // two parameters
    URIPattern pattern = new URIPattern("/level1/level2/{id}/level4/{id2}/");
    assertEquals(2, pattern.getParamNames().size());
    Iterator<String> it = pattern.getParamNames().iterator();
    while (it.hasNext()) {
      String s = it.next();
      if (!s.equals("id") && !s.equals("id2"))
        fail("Key is not id or id2");
    }
    assertFalse(pattern.matches("/level1/level2/test/level4/"));
    assertTrue(pattern.matches("/level1/level2/test3/level4/test5/"));
    assertFalse(pattern.matches("/level1/level2"));
    assertFalse(pattern.matches("/level1/level2/test/"));
    Map<String, String> params = pattern
        .parse("/level1/level2/test3/level4/test5/");
    assertEquals(2, params.size());
    assertEquals("test3", params.get("id"));
    assertEquals("test5", params.get("id2"));
  }

  public void testParametrizedURIPattern3() throws Exception {
    // three parameters
    URIPattern pattern = new URIPattern("/level1/{id1}/{id2}/{id3}/");
    assertEquals(3, pattern.getParamNames().size());

    assertTrue(pattern.matches("/level1/t/e/st/"));
    assertTrue(pattern.matches("/level1/level/2/te/st/"));
    assertTrue(pattern.matches("/level1/le/vel/2/"));
    assertTrue(pattern.matches("/level1/level2/te/st/"));

    Map<String, String> params = pattern.parse("/level1/t/e/s/t/");
    assertEquals(3, params.size());
    assertEquals("t", params.get("id1"));
    assertEquals("e", params.get("id2"));
    assertEquals("s/t", params.get("id3"));
    try {
      params = pattern.parse("/level1/tes/t/");
      fail("Exception should be here!");
    } catch (Exception e) {
    }
  }

  public void testParametrizedURIPattern4() throws Exception {
    URIPattern pattern = new URIPattern("/level1/{id1}/");
    assertEquals(1, pattern.getParamNames().size());
    assertTrue(pattern.matches("/level1/l/e/v/e/l/2/t/e/s/t/"));
  }

  public void testMatcheTwoPatters() throws Exception {
    URIPattern pattern1 = new URIPattern("/level1/{id1}/");
    URIPattern pattern2 = new URIPattern("/{id1}/");
    assertFalse(pattern1.matches(pattern2));
    assertTrue(pattern2.matches(pattern1));
  }

  public void testMatcheTwoPatters2() throws Exception {
    URIPattern pattern1 = new URIPattern("/level1/{id1}/{id2}/level2/");
    URIPattern pattern2 = new URIPattern("/level1/{id1}/");
    assertFalse(pattern1.matches(pattern2));
    assertTrue(pattern2.matches(pattern1));
  }
  
}
