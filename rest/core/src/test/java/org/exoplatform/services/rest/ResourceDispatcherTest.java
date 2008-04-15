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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.List;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceContainerConflict;
import org.exoplatform.services.rest.container.ResourceContainerConflict2;
import org.exoplatform.services.rest.container.ResourceContainerContextParameter;
import org.exoplatform.services.rest.container.ResourceContainerJAXB;
import org.exoplatform.services.rest.container.ResourceContainerMimeTypes;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate;
import org.exoplatform.services.rest.container.ResourceContainerSimpleSerializableEntity;
import org.exoplatform.services.rest.container.ResourceContainer_2;
import org.exoplatform.services.rest.container.ResourceContainer_3;
import org.exoplatform.services.rest.container.ResourceDescriptor;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceDispatcherTest extends TestCase {

  private StandaloneContainer container;
  private ResourceBinder binder;
  private ResourceDispatcher dispatcher;

  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    StandaloneContainer
        .setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder) container
        .getComponentInstanceOfType(ResourceBinder.class);
    binder.clear();
    dispatcher = (ResourceDispatcher) container
        .getComponentInstanceOfType(ResourceDispatcher.class);
  }

  public void testDispatcheWithMimeTypes() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerMimeTypes resourceContainerMimeTypes = new ResourceContainerMimeTypes();
    binder.bind(resourceContainerMimeTypes);
    assertEquals(2, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    h.putSingle("accept", "text/html;q=0.8,text/xml,text/plain;q=0.5");
    h.putSingle("test", "test_header");
    Request request = new Request(new ByteArrayInputStream("test_string"
        .getBytes()), new ResourceIdentifier("/level1/myID/level3/"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("uriparam=myID, entity=test_string, header=test_header",
        resp.getEntity());

    request = new Request(new ByteArrayInputStream("test_string".getBytes()),
        new ResourceIdentifier("/level1/myID/level3/"), "POST", h, q);
    resp = dispatcher.dispatch(request);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    resp.writeEntity(out);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>"
         + "<data>uriparam=myID, entity=test_string, header=test_header</data></test>",
         new String(out.toByteArray()));
    binder.unbind(resourceContainerMimeTypes);
    assertEquals(0, list.size());
  }

  public void testHttpMethods() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainer_2 resourceContainer = new ResourceContainer_2();
    binder.bind(resourceContainer);
    assertEquals(4, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(new ByteArrayInputStream("new resource"
        .getBytes()), new ResourceIdentifier("/level1/myID/le vel3/"),
        "PoST", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("http://localhost/level1/myID/le vel3/new resource",
        resp.getResponseHeaders().getFirst("Location"));

    request = new Request(new ByteArrayInputStream("recreate resource"
        .getBytes()), new ResourceIdentifier("/level1/myID/le vel3/"),
        "PUT", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("http://localhost/level1/myID/le vel3/recreate resource",
        resp.getResponseHeaders().getFirst("Location"));
    
    request = new Request(null, new ResourceIdentifier("/level1/myID/le vel3/delete resource"),
        "DELETE", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals(204, resp.getStatus());
    
    request = new Request(null, new ResourceIdentifier("/level1/myID/le vel3/get resource"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("text/plain", resp.getEntityMetadata().getMediaType());
    assertEquals("get resource", resp.getEntity());

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testResourceSelectionByURI() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainer_3 resourceContainer = new ResourceContainer_3();
    binder.bind(resourceContainer);
    assertEquals(5, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    
    Request request = new Request(null,
        new ResourceIdentifier("/level1/myID1/"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("myID1", resp.getEntity());
    
    request = new Request(null, new ResourceIdentifier("/level1/myID1/myID2/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("myID1, myID2", resp.getEntity());
    
    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("myID1, myID2, myID3", resp.getEntity());
    
    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/myID4/"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("myID1, myID2, myID3, myID4", resp.getEntity());

    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/myID4/myID5/"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("myID1, myID2, myID3, myID4, myID5", resp.getEntity());

    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/myID4/m/y/I/D/5"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("myID1, myID2, myID3, myID4, m/y/I/D/5", resp.getEntity());

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testJAXBTransformetion() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerJAXB resourceContainer = new ResourceContainerJAXB();
    binder.bind(resourceContainer);
    assertEquals(1, list.size());

    FileInputStream f = new FileInputStream("src/test/resources/book-in.xml");

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(f, new ResourceIdentifier("/test/jaxb"),
        "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("text/xml", resp.getEntityMetadata().getMediaType());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    resp.writeEntity(out);
    String p = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
    		"<book send-by-post=\"false\"><title>Red Hat Enterprise Linux 5 Administration Unleashed</title>" +
    		"<author>Tammy Fox</author><price currency=\"EUR\">21.75</price>" +
    		"<member-price currency=\"EUR\">17.25</member-price></book>";
    assertEquals(p, new String(out.toByteArray()));
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testSerializable() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerSimpleSerializableEntity resourceContainer =
      new ResourceContainerSimpleSerializableEntity();
    binder.bind(resourceContainer);
    assertEquals(1, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(new ByteArrayInputStream("1234567890".getBytes()),
        new ResourceIdentifier("/test/serializable"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    resp.writeEntity(out);
    assertEquals("0987654321", new String(out.toByteArray()));
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testConflict() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerConflict resourceContainer = new ResourceContainerConflict();
    ResourceContainerConflict2 resourceContainer2 = new ResourceContainerConflict2();
    binder.bind(resourceContainer2);
    try {
      binder.bind(resourceContainer);
      fail("InvalidResourceDescriptorException should be here!");
    } catch (InvalidResourceDescriptorException e) {}
    assertEquals(1, list.size());
    binder.unbind(resourceContainer2);
    binder.bind(resourceContainer);
    assertEquals(7, list.size());
    try {
      binder.bind(resourceContainer2);
      fail("InvalidResourceDescriptorException should be here!");
    } catch (InvalidResourceDescriptorException e) {}
    
    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(null, new ResourceIdentifier("/test/test1/id1/id2/test/test2/"),
        "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("method1", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test/test1/id1/test2/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("method2", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test1/id1/id2/id3/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("method3", resp.getEntity());
    
    request = new Request(null, new ResourceIdentifier("/id/id1/id2/id3/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("method4", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test1/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("method5", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test/id1/id2/id3/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("method6", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test/id2/id3/"),
        "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("method7", resp.getEntity());
    
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
    binder.bind(resourceContainer2);
    assertEquals(1, list.size());
    request = new Request(null, new ResourceIdentifier("/id/id1/id2/id3/"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("id/id1/id2/id3", resp.getEntity());
    binder.unbind(resourceContainer2);
    assertEquals(0, list.size());
  }

  public void testQueryTemplate() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerQueryTemplate resourceContainer = new ResourceContainerQueryTemplate();
    binder.bind(resourceContainer);
    assertEquals(2, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    q.putSingle("method", "method2");
    q.putSingle("param1", "param1");
    q.putSingle("param2", "param2");
    q.putSingle("param3", "param3");
    q.putSingle("param4", "param4");
    Request request = new Request(null, new ResourceIdentifier(
        "/test/qeuryfilter/"), "GET", h, q);
    
    String p = "method=method2, param1=param1, param2=param2, param3=param3, param4=param4";
    
    assertEquals(p, dispatcher.dispatch(request).getEntity());

    q = new MultivaluedMetadata();
    q.putSingle("param1", "test1");
    q.putSingle("param2", "test2");
    q.putSingle("param3", "test3");
    q.putSingle("param4", "test4");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    try {
      // There is no method parameter.
      // It is not much to query template must be fail.
      dispatcher.dispatch(request).getEntity();
      fail("NoSuchMethodException should be here!");
    } catch (NoSuchMethodException e) {
    }

    q = new MultivaluedMetadata();
    q.putSingle("method", "method2");
    q.putSingle("param1", "param1");
    q.putSingle("param2", "param2");
    q.putSingle("param3", "test3");
    q.putSingle("param4", "param4");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    try {
      dispatcher.dispatch(request).getEntity();
      fail("NoSuchMethodException should be here!");
    } catch (NoSuchMethodException e) {
    }
    q.putSingle("param3", "param3");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    // now it must work.
    p = "method=method2, param1=param1, param2=param2, param3=test3, param3, param4=param4";
    assertEquals(p, dispatcher.dispatch(request).getEntity());
    // ---------------------
    q = new MultivaluedMetadata();
    q.putSingle("method", "method1");
    q.putSingle("param1", "param1");
    q.putSingle("param2", "param2");
    q.putSingle("param3", "test3");
    q.putSingle("param4", "param4");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    p = "method=method1, param1=param1, param2=param2";
    assertEquals(p, dispatcher.dispatch(request).getEntity());
    q = new MultivaluedMetadata();
    q.putSingle("method", "method1");
    q.putSingle("param1", "param1");
    q.putSingle("param2", "test2");
    q.putSingle("param3", "test3");
    q.putSingle("param4", "param4");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    try {
      dispatcher.dispatch(request).getEntity();
      fail("NoSuchMethodException should be here!");
    } catch (NoSuchMethodException e) {
    }
    // now must work
    q.putSingle("param2", "param2");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    p = "method=method1, param1=param1, param2=test2, param2";
    assertEquals(p, dispatcher.dispatch(request).getEntity());
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testContextParam() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerContextParameter resourceContainer = new ResourceContainerContextParameter();
    binder.bind(resourceContainer);
    assertEquals(1, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(null, new ResourceIdentifier("host",
        "/path/", "/test/context/"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("host=host, baseURI=/path/, relURI=/test/context/, test=test11",
        resp.getEntity());
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }
  
}
