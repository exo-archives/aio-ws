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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.InputHeadersMap;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FormEntityTest extends AbstractResourceTest {

  @Path("/")
  public static class Resource1 {
    @POST
    @Path("a")
    @Consumes("application/x-www-form-urlencoded")
    public void m1(@FormParam("foo") String foo,
                   @FormParam("bar") String bar,
                   MultivaluedMap<String, String> form) {
      assertEquals(foo, form.getFirst("foo"));
      assertEquals(bar, form.getFirst("bar"));
    }

    @POST
    @Path("b")
    @Consumes("application/x-www-form-urlencoded")
    public void m2(MultivaluedMap<String, String> form) {
      assertEquals("to be or not to be", form.getFirst("foo"));
      assertEquals("hello world", form.getFirst("bar"));
    }

  }

  public void testFormEntity() throws Exception {
    Resource1 r1 = new Resource1();
    registry(r1);
    byte[] data = "foo=to%20be%20or%20not%20to%20be&bar=hello%20world".getBytes("UTF-8");
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.putSingle("content-type", "application/x-www-form-urlencoded");
    h.putSingle("content-length", "" + data.length);
    assertEquals(204, service("POST", "/a", "", h, data).getStatus());
    assertEquals(204, service("POST", "/b", "", h, data).getStatus());
    unregistry(r1);
  }

  // Multipart form-data
  
  @Path("/")
  public static class Resource2 {

    /**
     * Pattern for comparison with parsed {@link FileItem}.
     */
    private class FileItemTester {

      private boolean isFormField;

      private String  contentType;

      private String  name;

      private String  fieldName;
      
      private String string;

      public FileItemTester(String contentType,
                            boolean isFormField,
                            String fieldName,
                            String name,
                            String string) {
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fieldName = fieldName;
        this.name = name;
        this.string = string;
      }

      public String getContentType() {
        return contentType;
      }

      public boolean isFormField() {
        return isFormField;
      }

      public String getFieldName() {
        return fieldName;
      }

      public String getName() {
        return name;
      }
      
      public String getString() {
        return string;
      }
    }
    
    private Iterator<FileItemTester> pattern;
    
    /**
     * Initialize <tt>pattern</tt>.
     */
    public Resource2() {
      List<FileItemTester> l = new ArrayList<FileItemTester>(3);
      l.add(new FileItemTester("text/xml", false, "xml-file", "foo.xml", XML_DATA));
      l.add(new FileItemTester("application/json", false, "json-file", "foo.json", JSON_DATA));
      l.add(new FileItemTester(null, true, "field", null, "to be or not to be"));
      pattern = l.iterator();
    }
    
    @POST
    @Consumes("multipart/*")
    public void m9(Iterator<FileItem> iter) throws Exception {
      while (iter.hasNext()) {
        if (!pattern.hasNext())
          fail("Wrong number of parsed items");
        FileItem fi = iter.next();
        FileItemTester fit = pattern.next();
        assertEquals(fit.getContentType(), fi.getContentType());
        assertEquals(fit.isFormField(), fi.isFormField());
        assertEquals(fit.getFieldName(), fi.getFieldName());
        assertEquals(fit.getName(), fi.getName());
        assertEquals(fit.getString(), fi.getString());
      }
    }

  }

  private static final String XML_DATA  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                            + "<root><data>hello world</data></root>";

  private static final String JSON_DATA = "{\"data\":\"hello world\"}";

  public void testMultipartForm() throws Exception {
    Resource2 r2 = new Resource2();
    registry(r2);
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintWriter w = new PrintWriter(out);
    w.write("--abcdef\r\n"
        + "Content-Disposition: form-data; name=\"xml-file\"; filename=\"foo.xml\"\r\n"
        + "Content-Type: text/xml\r\n"
        + "\r\n"
        + XML_DATA
        + "\r\n"
        + "--abcdef\r\n"
        + "Content-Disposition: form-data; name=\"json-file\"; filename=\"foo.json\"\r\n"
        + "Content-Type: application/json\r\n"
        + "\r\n"
        + JSON_DATA
        + "\r\n"
        + "--abcdef\r\n"
        + "Content-Disposition: form-data; name=\"field\"\r\n"
        + "\r\n"
        + "to be or not to be"
        + "\r\n"
        + "--abcdef--\r\n");
    w.flush();
    h.putSingle("content-type", "multipart/form-data; boundary=abcdef");
    
    EnvironmentContext envctx = new EnvironmentContext();
    byte[] data = out.toByteArray();
    HttpServletRequest httpRequest = new DummyHttpServeltRequest(new ByteArrayInputStream(data), data.length, "POST", new InputHeadersMap(h));
    envctx.put(HttpServletRequest.class, httpRequest);
    EnvironmentContext.setCurrent(envctx);
    
    assertEquals(204, service("POST", "/", "", h, data).getStatus());
    unregistry(r2);
  }
  
  private static class DummyHttpServeltRequest implements HttpServletRequest {

    private String                         method;

    private int                            length;

    private InputStream                    data;

    private MultivaluedMap<String, String> headers;
    
    public DummyHttpServeltRequest(InputStream data, int length, String method, MultivaluedMap<String, String> headers) {
      this.data = data;
      this.length = length;
      this.method = method;
      this.headers = headers;
    }

    public String getAuthType() {
      return null;
    }

    public String getContextPath() {
      return "test";
    }

    public Cookie[] getCookies() {
      return null;
    }

    public long getDateHeader(String arg0) {
      return 0;
    }

    public String getHeader(String arg0) {
      return headers.getFirst(arg0);
    }

    public Enumeration getHeaderNames() {
      return new EnumerationImpl(headers.keySet().iterator());
    }

    public Enumeration getHeaders(String arg0) {
      return new EnumerationImpl(headers.get(arg0).iterator());
    }

    public int getIntHeader(String arg0) {
      return 0;
    }

    public String getMethod() {
      return method;
    }

    public String getPathInfo() {
      return null;
    }

    public String getPathTranslated() {
      return null;
    }

    public String getQueryString() {
      return null;
    }

    public String getRemoteUser() {
      return null;
    }

    public String getRequestURI() {
      return null;
    }

    public StringBuffer getRequestURL() {
      return null;
    }

    public String getRequestedSessionId() {
      return null;
    }

    public String getServletPath() {
      return null;
    }

    public HttpSession getSession() {
      return null;
    }

    public HttpSession getSession(boolean arg0) {
      return null;
    }

    public Principal getUserPrincipal() {
      return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }

    public boolean isRequestedSessionIdFromURL() {
      return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }

    public boolean isRequestedSessionIdValid() {
      return false;
    }

    public boolean isUserInRole(String arg0) {
      return false;
    }

    public Object getAttribute(String arg0) {
      return null;
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public int getContentLength() {
      return length;
    }

    public String getContentType() {
      return headers.getFirst("content-type");
    }

    public ServletInputStream getInputStream() throws IOException {
      return new DummyServletInputStream(data);
    }

    public Locale getLocale() {
      return null;
    }

    public Enumeration getLocales() {
      return null;
    }

    public String getParameter(String arg0) {
      return null;
    }

    public Map getParameterMap() {
      return null;
    }

    public Enumeration getParameterNames() {
      return null;
    }

    public String[] getParameterValues(String arg0) {
      return null;
    }

    public String getProtocol() {
      return null;
    }

    public BufferedReader getReader() throws IOException {
      return null;
    }

    public String getRealPath(String arg0) {
      return null;
    }

    public String getRemoteAddr() {
      return null;
    }

    public String getRemoteHost() {
      return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
      return null;
    }

    public String getScheme() {
      return null;
    }

    public String getServerName() {
      return null;
    }

    public int getServerPort() {
      return 0;
    }

    public boolean isSecure() {
      return false;
    }

    public void removeAttribute(String arg0) {
    }

    public void setAttribute(String arg0, Object arg1) {
    }

    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
    }
    
  }
  
  private static class EnumerationImpl implements Enumeration {
    
    private final Iterator iter;
    
    public EnumerationImpl(Iterator iter) {
      this.iter = iter;
    }

    public boolean hasMoreElements() {
      return iter.hasNext();
    }

    public Object nextElement() {
      return iter.next();
    }
  }
  
  private static class DummyServletInputStream extends ServletInputStream {

    
    private final InputStream data;

    public DummyServletInputStream(InputStream data) {
      this.data = data;
    }
    
    @Override
    public int read() throws IOException {
      return data.read();
    }
    
  }


}
