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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonEntityProviderTest extends BaseTest {

  private static final String DATA = "{\"name\":\"andrew\", \"password\":\"hello\"}";

  private RequestHandler      requestHandler;

  private MediaType           mediaType;
  RuntimeDelegateImpl rd;

  public void setUp() throws Exception {
    super.setUp();
    requestHandler = (RequestHandler) container.getComponentInstanceOfType(RequestHandler.class);
    assertNotNull(requestHandler);
    mediaType = new MediaType("application", "json");
    rd = (RuntimeDelegateImpl)RuntimeDelegate.getInstance();
  }

  @SuppressWarnings("unchecked")
  public void testRead() throws Exception {
    RuntimeDelegateImpl rd = (RuntimeDelegateImpl)RuntimeDelegate.getInstance();
    MessageBodyReader reader = rd/*requestHandler*/.getMessageBodyReader(Bean.class,
                                                                   null,
                                                                   null,
                                                                   mediaType);
    assertNotNull(reader);
    assertTrue(reader.isReadable(Bean.class, Bean.class, null, mediaType));
    byte[] data = DATA.getBytes("UTF-8");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle(HttpHeaders.CONTENT_LENGTH, "" + data.length);
    Bean bean = (Bean) reader.readFrom(Bean.class,
                                       Bean.class,
                                       null,
                                       mediaType,
                                       h,
                                       new ByteArrayInputStream(data));
    assertEquals("andrew", bean.getName());
    assertEquals("hello", bean.getPassword());
  }

  @SuppressWarnings("unchecked")
  public void testWrite() throws Exception {
    MessageBodyWriter writer = /*requestHandler*/rd.getMessageBodyWriter(Bean.class,
                                                                   null,
                                                                   null,
                                                                   mediaType);
    assertNotNull(writer);
    assertTrue(writer.isWriteable(Bean.class, Bean.class, null, mediaType));
    Bean bean = new Bean();
    bean.setName("andrew");
    bean.setPassword("test");
    writer.writeTo(bean, Bean.class, Bean.class, null, mediaType, null, new ByteArrayOutputStream());
  }

  //

  public static class Bean {
    private String name;

    private String password;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String toString() {
      return "name=" + name + "; password=" + password;
    }
  }

}
