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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.generated.Book;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonEntityTest extends AbstractResourceTest {

  @Path("/")
  public static class Resource1 {
    @POST
    @Consumes("application/json")
    public void m1(Book book) {
      assertEquals("Hamlet", book.getTitle());
      assertEquals("William Shakespeare", book.getAuthor());
      assertTrue(book.isSendByPost());
    }
  }

  @Path("/")
  public static class Resource2 {
    @GET
    @Produces("application/json")
    public Book m1() {
      Book book = new Book();
      book.setTitle("Hamlet");
      book.setAuthor("William Shakespeare");
      book.setSendByPost(true);
      return book;
    }

    // Without @Produces annotation also should work.
    @POST
    public Book m2() {
      Book book = new Book();
      book.setTitle("Hamlet\n");
      book.setAuthor("William Shakespeare\n");
      book.setSendByPost(false);
      return book;
    }
  }

  private byte[] jsonData;
  
  public void setUp() throws Exception {
    super.setUp();
    jsonData = ("{\"title\":\"Hamlet\","
      + "\"author\":\"William Shakespeare\","
      + "\"sendByPost\":true}").getBytes("UTF-8");    
  }

  public void testJsonEntityParameter() throws Exception {
    Resource1 r1 = new Resource1();
    registry(r1);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    // Object is transfered via JSON
    h.putSingle("content-type", "application/json");
    // with JSON transformation for Book have restriction can't pass BigDecimal
    // (has not simple constructor and it is not in JSON known types)
    h.putSingle("content-length", "" + jsonData.length);
    assertEquals(204, service("POST", "/", "", h, jsonData).getStatus());
    unregistry(r1);
  }

  public void testJsonReturn() throws Exception {
    Resource2 r2 = new Resource2();
    registry(r2);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("accept", "application/json");
    ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

    // Resource2#m1()
    ContainerResponse response = service("GET", "/", "", h, null, writer);
    assertEquals(200, response.getStatus());
    assertEquals("application/json", response.getContentType().toString());
    Book book = (Book) response.getEntity();
    assertEquals("Hamlet", book.getTitle());
    assertEquals("William Shakespeare", book.getAuthor());
    assertTrue(book.isSendByPost());
    
    // Resource2#m2()
    response = service("POST", "/", "", h, null, writer);
    assertEquals(200, response.getStatus());
    assertEquals("application/json", response.getContentType().toString());
    book = (Book) response.getEntity();
    assertEquals("Hamlet\n", book.getTitle());
    assertEquals("William Shakespeare\n", book.getAuthor());
    assertFalse(book.isSendByPost());
    writer = new ByteArrayContainerResponseWriter();
    unregistry(r2);
  }

}
