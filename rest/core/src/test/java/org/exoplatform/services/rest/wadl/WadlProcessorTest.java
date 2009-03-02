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

package org.exoplatform.services.rest.wadl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.services.rest.impl.resource.PathValue;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.wadl.research.Application;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class WadlProcessorTest extends BaseTest {

  @Path("a/{b}")
  public static class Resource1 {

    @GET
    public String m1(@Context UriInfo uriInfo) {
      return uriInfo.getAbsolutePath().toString();
    }

    @POST
    @Consumes("text/plain")
    @Produces("text/plain")
    public String m2(@HeaderParam("content-type") String contentType, String data) {
      return data;
    }

    @DELETE
    public void m3(@DefaultValue("1") @MatrixParam("id") int j) {
    }

    @PUT
    @Consumes("text/xml")
    public void m4(DOMSource ds) {
    }

    @GET
    @Path("{c}/{d}")
    public String m5(@PathParam("b") String b, @PathParam("c") String a) {
      return b;
    }

    @POST
    @Path("{c}/{d}/{e}")
    public void m6(@PathParam("c") String b, @PathParam("e") String a) {
    }

    @Path("sub/{x}")
    public Resource2 m7() {
      return new Resource2();
    }

  }

  public static class Resource2 {
    @GET
    @Produces("text/plain")
    public String m0(@PathParam("x") String x) {
      return x;
    }
  }

  public void testBaseWadlGenerator() throws Exception {
    
    PathValue path = new PathValue(Resource1.class.getAnnotation(Path.class).value());
    AbstractResourceDescriptor ard = new AbstractResourceDescriptorImpl(path, Resource1.class);
    WadlProcessor wadlProcessor = new WadlProcessor();
    Application app = wadlProcessor.process(ard, new URI("http://localhost:8080/ws/rs"));
    
    JAXBContext jctx = JAXBContext.newInstance(Application.class);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    jctx.createMarshaller().marshal(app, bout);
//    System.out.println(new String(bout.toByteArray()));
    
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setNamespaceAware(true);
    Document doc = f.newDocumentBuilder().parse(new ByteArrayInputStream(bout.toByteArray()));

    XPath xp = XPathFactory.newInstance().newXPath();
    xp.setNamespaceContext(new DummyNamespaceContext());
    String res = (String) xp.evaluate("count(//wadl:resource)", doc, XPathConstants.STRING);
    assertEquals("4", res);
    res = (String) xp.evaluate("count(//wadl:resource[@path='a/{b}'])", doc, XPathConstants.STRING);
    assertEquals("1", res);
    res = (String) xp.evaluate("count(//wadl:resource[@path='{c}/{d}'])",
                               doc,
                               XPathConstants.STRING);
    assertEquals("1", res);
    res = (String) xp.evaluate("count(//wadl:resource[@path='{c}/{d}/{e}'])",
                               doc,
                               XPathConstants.STRING);
    assertEquals("1", res);
    res = (String) xp.evaluate("count(//wadl:resource[@path='sub/{x}'])",
                               doc,
                               XPathConstants.STRING);
    assertEquals("1", res);

    // discover resource methods
    res = (String) xp.evaluate("count(//wadl:resource[@path='a/{b}']/wadl:method)",
                               doc,
                               XPathConstants.STRING);
    assertEquals("4", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:method[@id='m1']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("GET", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:method[@id='m2']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("POST", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:method[@id='m2']/wadl:request/wadl:param[@style='header']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("content-type", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:method[@id='m3']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("DELETE", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:method[@id='m4']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("PUT", res);

    // discover sub-resource methods
    res = (String) xp.evaluate("count(//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}']/wadl:method)",
                               doc,
                               XPathConstants.STRING);
    assertEquals("1", res);
    res = (String) xp.evaluate("count(//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}/{e}']/wadl:method)",
                               doc,
                               XPathConstants.STRING);
    assertEquals("1", res);

    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}']/wadl:param[@name='c']/@style",
                               doc,
                               XPathConstants.STRING);
    assertEquals("template", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}']/wadl:param[@name='b']/@style",
                               doc,
                               XPathConstants.STRING);
    assertEquals("template", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}']/wadl:method[@id='m5']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("GET", res);

    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}/{e}']/wadl:param[@name='c']/@style",
                               doc,
                               XPathConstants.STRING);
    assertEquals("template", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}/{e}']/wadl:param[@name='e']/@style",
                               doc,
                               XPathConstants.STRING);
    assertEquals("template", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='{c}/{d}/{e}']/wadl:method[@id='m6']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("POST", res);

    // discover sub-resource locators
    res = (String) xp.evaluate("count(//wadl:resource[@path='a/{b}']/wadl:resource[@path='sub/{x}']/wadl:method)",
                               doc,
                               XPathConstants.STRING);
    assertEquals("1", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='sub/{x}']/wadl:param[@name='x']/@style",
                               doc,
                               XPathConstants.STRING);
    assertEquals("template", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='sub/{x}']/wadl:method[@id='m0']/@name",
                               doc,
                               XPathConstants.STRING);
    assertEquals("GET", res);
    res = (String) xp.evaluate("//wadl:resource[@path='a/{b}']/wadl:resource[@path='sub/{x}']/wadl:method[@id='m0']/wadl:response/wadl:representation/@mediaType",
                               doc,
                               XPathConstants.STRING);
    assertEquals("text/plain", res);
  }

  private static class DummyNamespaceContext implements NamespaceContext {

    private final String   nsPrefix;

    private final String   nsUri;

    private final Iterator nsIter;

    public DummyNamespaceContext() {
      nsPrefix = "wadl";
      nsUri = "http://research.sun.com/wadl/2006/10";
      List l = new ArrayList(1);
      l.add(nsPrefix);
      nsIter = l.iterator();

    }

    public String getNamespaceURI(String prefix) {
      if (prefix.equals(nsPrefix))
        return nsUri;
      return "";
    }

    public String getPrefix(String namespaceURI) {
      if (namespaceURI.equals(nsUri))
        return nsPrefix;
      return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
      if (namespaceURI.equals(nsUri))
        return nsIter;
      return Collections.emptyList().iterator();
    }

  }

}
