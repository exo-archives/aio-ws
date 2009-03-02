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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StringEntityProviderTest extends BaseTest {

  // 
  private static final String TEST_CYR = "\u041f\u0440\u0438\u0432\u0456\u0442";

//  private RequestHandler requestHandler;
  
  private MediaType mediaType;

  private RuntimeDelegateImpl rd;

  public void setUp() throws Exception {
    super.setUp();
//    requestHandler = (RequestHandler) container.getComponentInstanceOfType(RequestHandler.class);
//    assertNotNull(requestHandler);
    Map<String, String> p = new HashMap<String, String>(1);
    p.put("charset", "windows-1251");
    mediaType = new MediaType("text", "plain", p);
    rd = (RuntimeDelegateImpl)RuntimeDelegate.getInstance();
  }
  
  @SuppressWarnings("unchecked")
  public void testRead() throws IOException {

    MessageBodyReader reader =  /*requestHandler*/rd.getMessageBodyReader(String.class, null, null, mediaType);
    byte[] data = TEST_CYR.getBytes("windows-1251");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle(HttpHeaders.CONTENT_LENGTH, "" + data.length);
    InputStream in = new ByteArrayInputStream(data);
    String res = (String) reader.readFrom(String.class, String.class, null, mediaType, h, in);
//    System.out.println(res);
    assertTrue(TEST_CYR.equals(res));
    
    // not set character set then UTF-8 should be used
    mediaType = new MediaType("text", "plain");
    in = new ByteArrayInputStream(data);
    res = (String) reader.readFrom(String.class, null, null, mediaType, h, in);
//    System.out.println(res);
    // string is wrong encoded 
    assertFalse(TEST_CYR.equals(res));
  }
  
  @SuppressWarnings("unchecked")
  public void testWrite() throws IOException {
    MessageBodyWriter writer = /*requestHandler*/rd.getMessageBodyWriter(String.class, null, null, mediaType);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writer.writeTo(TEST_CYR, String.class, String.class, null, mediaType, null, out);
    String res = out.toString("windows-1251");
//    System.out.println(res);
    assertTrue(TEST_CYR.equals(res));

    out.reset();
    
    // not set character set then UTF-8 should be used
    mediaType = new MediaType("text", "plain");
    writer.writeTo(TEST_CYR, String.class, String.class, null, mediaType, null, out);
    res = out.toString("windows-1251");
//    System.out.println(res);  
    // string is wrong encoded 
    assertFalse(TEST_CYR.equals(res));
  }
}
