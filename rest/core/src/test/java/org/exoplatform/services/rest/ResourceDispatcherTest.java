/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.util.List;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceContainerAnnot;
import org.exoplatform.services.rest.container.ResourceContainerConflict;
import org.exoplatform.services.rest.container.ResourceContainerConflict2;
import org.exoplatform.services.rest.container.ResourceContainerContextParameter;
import org.exoplatform.services.rest.container.ResourceContainerJAXB;
import org.exoplatform.services.rest.container.ResourceContainerMimeTypes;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate2;
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

  public void testIdentifier() throws Exception {
    URI uri = new URI("http://localhost/level1/level2/id");
    System.out.println("getScheme " + uri.getScheme());
    System.out.println("getSchemeSpecificPart " + uri.getSchemeSpecificPart());
    System.out.println("getPath " + uri.getPath());
    System.out.println("getHost " + uri.getHost());
    System.out.println("getPort " + uri.getPort());
    System.out.println("getAuthority " + uri.getAuthority());
    System.out.println("getFragment " + uri.getFragment());
    System.out.println("getQuery " + uri.getQuery());
    System.out.println("getUserInfo " + uri.getUserInfo());
    System.out.println("relativize " +
        new URI("http://localhost/level1").relativize(uri).toASCIIString());
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
    Request request = new Request(new ByteArrayInputStream("test string"
        .getBytes()), new ResourceIdentifier("/level1/myID/level3/"), "GET", h,
        q);
    Response resp = dispatcher.dispatch(request);
    resp.writeEntity(System.out);
    request = new Request(new ByteArrayInputStream("test string".getBytes()),
        new ResourceIdentifier("/level1/myID/level3/"), "POST", h, q);
    resp = dispatcher.dispatch(request);
    System.out.println(">>> Content-Length: " +
        resp.getEntityMetadata().getLength());
    resp.writeEntity(System.out);
    System.out.println();
    binder.unbind(resourceContainerMimeTypes);
    assertEquals(0, list.size());
  }

  public void testServe2() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainer_2 resourceContainer = new ResourceContainer_2();
    binder.bind(resourceContainer);
    assertEquals(4, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(new ByteArrayInputStream("create something"
        .getBytes()), new ResourceIdentifier("/level1/myID/le vel3/"), "PoST",
        h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("http://localhost/test/_post", resp.getResponseHeaders()
        .getFirst("Location"));

    request = new Request(new ByteArrayInputStream("recreate something"
        .getBytes()), new ResourceIdentifier("/level1/myID/le vel3/"), "PUT",
        h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("http://localhost/test/_put", resp.getResponseHeaders()
        .getFirst("Location"));
    assertEquals("text/plain", resp.getEntityMetadata().getMediaType());
    resp.writeEntity(System.out);

    request = new Request(new ByteArrayInputStream("delete something"
        .getBytes()), new ResourceIdentifier("/level1/myID/le vel3/test"),
        "DELETE", h, q);
    resp = dispatcher.dispatch(request);
    resp.writeEntity(System.out);

    request = new Request(new ByteArrayInputStream("get something".getBytes()),
        new ResourceIdentifier("/level1/myID/le vel3/test"), "get", h, q);
    resp = dispatcher.dispatch(request);
    resp.writeEntity(System.out);

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testServe3() throws Exception {
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
    dispatcher.dispatch(request);
    request = new Request(null, new ResourceIdentifier("/level1/myID1/myID2/"),
        "GET", h, q);
    dispatcher.dispatch(request);
    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/"), "GET", h, q);
    dispatcher.dispatch(request);
    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/myID4/"), "GET", h, q);
    dispatcher.dispatch(request);
    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/myID4/myID5/"), "GET", h, q);
    dispatcher.dispatch(request);
    request = new Request(null, new ResourceIdentifier(
        "/level1/myID1/myID2/myID3/myID4/m/y/I/D/5"), "GET", h, q);
    dispatcher.dispatch(request);

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testServeAnnotatedClass() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerAnnot dw = new ResourceContainerAnnot();
    binder.bind(dw);
    assertEquals(1, list.size());

    ByteArrayInputStream ds = new ByteArrayInputStream("hello".getBytes());
    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    h.putSingle("accept", "text/plain");
    Request request = new Request(ds, new ResourceIdentifier(
        "/level1/level2/level3/myID1/myID2"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    System.out.println(">>> Content-Length: " +
        resp.getEntityMetadata().getLength());
    assertEquals("text/plain", resp.getEntityMetadata().getMediaType());
    resp.writeEntity(System.out);
    binder.unbind(dw);
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
    System.out.println(">>> Content-Length: " +
        resp.getEntityMetadata().getLength());
    System.out.println(">>> Cache-Control: " +
        resp.getEntityMetadata().getCacheControl());
    assertEquals("text/xml", resp.getEntityMetadata().getMediaType());
    resp.writeEntity(System.out);
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testSerializable() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerSimpleSerializableEntity resourceContainer = new ResourceContainerSimpleSerializableEntity();
    binder.bind(resourceContainer);
    assertEquals(1, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(new ByteArrayInputStream(
        "this is request data".getBytes()), new ResourceIdentifier(
        "/test/serializable"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    System.out.println(">>> Content-Length: " +
        resp.getEntityMetadata().getLength());
    resp.writeEntity(System.out);
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
    } catch (InvalidResourceDescriptorException e) {
    }
    assertEquals(1, list.size());
    binder.unbind(resourceContainer2);
    binder.bind(resourceContainer);
//    assertEquals(2, list.size());
    try {
      binder.bind(resourceContainer2);
      fail("InvalidResourceDescriptorException should be here!");
    } catch (InvalidResourceDescriptorException e) {
    }

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    Request request = new Request(null, new ResourceIdentifier(
        "/test1/id1/id2/test2/"), "GET", h, q);
    Response resp = dispatcher.dispatch(request);
    assertEquals("id1", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test/test1/id1/id2/test/test2"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("id1", resp.getEntity());

    request = new Request(null, new ResourceIdentifier("/test1/id1/id2/id3/"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("id3", resp.getEntity());
    
    request = new Request(null, new ResourceIdentifier("/id/id1/id2/id3/"), "GET", h, q);
    resp = dispatcher.dispatch(request);
    assertEquals("id", resp.getEntity());

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
    binder.bind(resourceContainer2);
    assertEquals(1, list.size());
    binder.unbind(resourceContainer2);
    assertEquals(0, list.size());
  }

  public void testQueryTemplate() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerQueryTemplate resourceContainer = new ResourceContainerQueryTemplate();
    ResourceContainerQueryTemplate2 resourceContainer2 = new ResourceContainerQueryTemplate2();
    binder.bind(resourceContainer2);
    binder.bind(resourceContainer);
    assertEquals(3, list.size());

    MultivaluedMetadata h = new MultivaluedMetadata();
    MultivaluedMetadata q = new MultivaluedMetadata();
    q.putSingle("method", "method3");
    q.putSingle("param1", "param1");
    q.putSingle("param2", "param2");
    q.putSingle("param3", "param3");
    q.putSingle("param4", "param4");
    Request request = new Request(null, new ResourceIdentifier(
        "/test/qeuryfilter/"), "GET", h, q);
    assertEquals("method3", dispatcher.dispatch(request).getEntity());

    q.putSingle("param1", "test1");
    q.putSingle("param2", "test2");
    q.putSingle("param3", "test3");
    q.putSingle("param4", "test4");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    assertEquals("method3", dispatcher.dispatch(request).getEntity());

    q = new MultivaluedMetadata();
    q.putSingle("method", "method3");
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
    assertEquals("method3", dispatcher.dispatch(request).getEntity());

    q = new MultivaluedMetadata();
    q.putSingle("method", "method2");
    q.putSingle("param1", "param1");
    q.putSingle("param2", "param2");
    q.putSingle("param3", "test3");
    q.putSingle("param4", "param4");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    assertEquals("method2", dispatcher.dispatch(request).getEntity());
    q = new MultivaluedMetadata();
    q.putSingle("method", "method2");
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
    q.putSingle("param2", "param2");
    request = new Request(null, new ResourceIdentifier("/test/qeuryfilter/"),
        "GET", h, q);
    assertEquals("method2", dispatcher.dispatch(request).getEntity());
    binder.unbind(resourceContainer);
    binder.unbind(resourceContainer2);
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
    assertEquals("/path/", resp.getEntity());
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

}
