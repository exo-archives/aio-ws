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

package org.exoplatform.services.rest.impl.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ContextResolverTest extends org.exoplatform.services.rest.AbstractResourceTest {

  @Provider
  @Produces("text/plain")
  public static class ContextResolver1 implements javax.ws.rs.ext.ContextResolver<String>{

    public String getContext(Class<?> type) {
      return "text";
    }
    
  }
  
  @Provider
  public static class ContextResolver2 implements javax.ws.rs.ext.ContextResolver<String>{

    public String getContext(Class<?> type) {
      return "*";
    }
    
  }
  
  @Provider
  @Produces("text/xml")
  public static class ContextResolver3 implements javax.ws.rs.ext.ContextResolver<String>{

    public String getContext(Class<?> type) {
      return "xml";
    }
    
  }

  @Provider
  @Produces("text/html")
  public static class ContextResolver4 implements javax.ws.rs.ext.ContextResolver<String>{

    public String getContext(Class<?> type) {
      return "html";
    }
    
  }
  
  @Provider
  @Produces("text/*")
  public static class ContextResolver5 implements javax.ws.rs.ext.ContextResolver<String>{

    public String getContext(Class<?> type) {
      return "anytext";
    }
    
  }

  public void setUp() throws Exception {
    super.setUp();
    rd.addContextResolver(ContextResolver1.class); 
    rd.addContextResolver(ContextResolver2.class); 
    rd.addContextResolver(ContextResolver3.class); 
    rd.addContextResolver(ContextResolver4.class); 
    rd.addContextResolver(ContextResolver5.class); 
  }
  
  
  public void testContextResolver() {
    assertEquals("text", rd.getContextResolver(String.class, new MediaType("text", "plain")).getContext(String.class));
    assertEquals("*", rd.getContextResolver(String.class, new MediaType("xxx", "xxx")).getContext(String.class));
    assertEquals("xml", rd.getContextResolver(String.class, new MediaType("text", "xml")).getContext(String.class));
    assertEquals("html", rd.getContextResolver(String.class, new MediaType("text", "html")).getContext(String.class));
    assertEquals("anytext", rd.getContextResolver(String.class, new MediaType("text", "xxx")).getContext(String.class));
  }
  
}
