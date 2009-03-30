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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ReaderEntityProviderTest extends BaseTest {

  private static final String TEST_CYR = "\u041f\u0440\u0438\u0432\u0456\u0442";

  @SuppressWarnings("unchecked")
  public void testRead() throws Exception {
    MessageBodyReader reader = providers.getMessageBodyReader(Reader.class,
                                                       null,
                                                       null,
                                                       MediaTypeHelper.DEFAULT_TYPE);
    assertNotNull(reader);
    assertNotNull(providers.getMessageBodyReader(Reader.class, null, null, null));
    assertTrue(reader.isReadable(Reader.class, null, null, null));
    byte[] data = TEST_CYR.getBytes("windows-1251");
    InputStream in = new ByteArrayInputStream(data);
    Map<String, String> p = new HashMap<String, String>(1);
    p.put("charset", "windows-1251");
    MediaType mediaType = new MediaType("text", "plain", p);
    Reader result = (Reader) reader.readFrom(Reader.class, null, null, mediaType, null, in);
    char[] c = new char[1024];
    int b = result.read(c);
    String resstr = new String(c, 0, b);
    System.out.println(getClass().getName() + " : " + resstr);
    assertEquals(TEST_CYR, resstr);
    // Provoke encoding error, doesn't set encoding in media type
    mediaType = new MediaType("text", "plain");
    in = new ByteArrayInputStream(data);
    result = (Reader) reader.readFrom(Reader.class, null, null, mediaType, null, in);
    c = new char[1024];
    b = result.read(c);
    resstr = new String(c, 0, b);
    System.out.println(getClass().getName() + " : " + resstr);
    assertFalse(TEST_CYR.equals(resstr));
  }

  @SuppressWarnings("unchecked")
  public void testWrite() throws Exception {
    MessageBodyWriter writer = providers.getMessageBodyWriter(Reader.class,
                                                       null,
                                                       null,
                                                       MediaTypeHelper.DEFAULT_TYPE);
    assertNotNull(writer);
    assertNotNull(providers.getMessageBodyWriter(Reader.class, null, null, null));
    assertTrue(writer.isWriteable(Reader.class, null, null, null));
    Reader source = new InputStreamReader(new ByteArrayInputStream(TEST_CYR.getBytes("windows-1251")),
                                          "windows-1251");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writer.writeTo(source, source.getClass(), null, null, null, null, out);
    int b = out.size();
    String resstr = new String(out.toByteArray(), 0, b);
    System.out.println(getClass().getName() + " : " + resstr);
    assertEquals(TEST_CYR, resstr);
  }

}
