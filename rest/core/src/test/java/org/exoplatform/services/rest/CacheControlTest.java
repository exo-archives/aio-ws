/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

package org.exoplatform.services.rest;

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CacheControlTest extends TestCase
{

   CacheControl cacheControl;

   public void setUp()
   {
      cacheControl = new CacheControl();
   }

   public void testDefaultCacheControl()
   {
      assertTrue(cacheControl.isPublicCacheable());
      assertFalse(cacheControl.isPrivateCacheable());
      assertFalse(cacheControl.isNoCache());
      assertFalse(cacheControl.isNoStore());
      assertTrue(cacheControl.isNoTransform());
      assertFalse(cacheControl.isMustRevalidate());
      assertFalse(cacheControl.isProxyRevalidate());
      assertEquals("public, no-transform", cacheControl.getAsString());
   }

   public void testPrivate()
   {
      cacheControl.setPublicCacheable(false);
      cacheControl.setPrivateCacheable(true);
      cacheControl.setNoTransform(false);
      assertEquals("private", cacheControl.getAsString());
      List<String> params = new ArrayList<String>();
      params.add("param1");
      params.add("param2");
      params.add("param3");
      cacheControl.setPrivateCacheable(true, params);
      assertEquals("private=\"param1, param2, param3\"", cacheControl.getAsString());
   }

   public void testNoCache()
   {
      cacheControl.setPublicCacheable(false);
      cacheControl.setNoCache(true);
      cacheControl.setNoTransform(false);
      assertEquals("no-cache", cacheControl.getAsString());
      List<String> params = new ArrayList<String>();
      params.add("param1");
      params.add("param2");
      params.add("param3");
      cacheControl.setNoCache(true, params);
      assertEquals("no-cache=\"param1, param2, param3\"", cacheControl.getAsString());
   }

   public void testNoCachePrivate()
   {
      List<String> params = new ArrayList<String>();
      params.add("param1");
      params.add("param2");
      params.add("param3");
      cacheControl.setPrivateCacheable(true, params);
      cacheControl.setNoCache(true, params);
      assertEquals("public, private=\"param1, param2, param3\", " + "no-cache=\"param1, param2, param3\", "
         + "no-transform", cacheControl.getAsString());
   }

   public void testOther()
   {
      cacheControl.setMaxAge(10);
      cacheControl.setSMaxAge(100);
      cacheControl.setMustRevalidate(true);
      cacheControl.setProxyRevalidate(true);
      assertEquals("public, no-transform, must-revalidate, proxy-revalidate, max-age=10, s-maxage=100", cacheControl
         .getAsString());
   }

   public void testCacheExtension()
   {
      Map<String, String> map = new HashMap<String, String>();
      map.put("extension1", "value1");
      map.put("extension2", "value2");
      map.put("extension3", "value 3");
      cacheControl.setCacheExtension(map);
      String asString = cacheControl.getAsString();
      //System.out.println(asString);
      assertTrue(asString.contains("extension3=\"value 3\""));
      assertTrue(asString.contains("extension1=value1"));
      assertTrue(asString.contains("extension2=value2"));
   }

}
