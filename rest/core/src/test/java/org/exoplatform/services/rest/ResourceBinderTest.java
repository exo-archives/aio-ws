/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest;

import java.util.List;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceContainerMimeTypes;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate2;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplateFail;
import org.exoplatform.services.rest.container.ResourceContainer_2;
import org.exoplatform.services.rest.container.ResourceContainer_3;
import org.exoplatform.services.rest.container.ResourceDescriptor;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceBinderTest extends TestCase {

  private StandaloneContainer container;
  private ResourceBinder binder;

// private ResourceDispatcher dispatcher;

  public void setUp() throws Exception {
    StandaloneContainer
        .setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder) container
        .getComponentInstanceOfType(ResourceBinder.class);
    binder.clear();
  }

  public void testBind() throws Exception {
    assertNotNull(binder);
    binder.clear();
    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerMimeTypes resourceContainer2 = new ResourceContainerMimeTypes();
    binder.bind(resourceContainer2);
    assertEquals(2, list.size());
    binder.clear();
    assertEquals(0, list.size());
  }

  public void testBindFailed() throws Exception {
    assertNotNull(binder);
    List<ResourceDescriptor> list = binder.getAllDescriptors();
    assertEquals(0, list.size());
    ResourceContainer_2 resourceContainer3 = new ResourceContainer_2();
    binder.bind(resourceContainer3);
    try {
      binder.bind(resourceContainer3);
      fail("Binding for this component shoud be failed!");
    } catch (InvalidResourceDescriptorException e) {
      ;
    }
    assertEquals(4, list.size());
    binder.unbind(resourceContainer3);
    assertEquals(0, list.size());
  }

  public void testSortOfResourceContainer() throws Exception {
    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainer_3 resourceContainer = new ResourceContainer_3();
    binder.bind(resourceContainer);
    assertEquals(5, list.size());
    int i = 5;
    for (ResourceDescriptor rd : list) {
      assertEquals(i, rd.getURIPattern().getParamNames().size());
      System.out.println(rd.getURIPattern().getString());
      i--;
    }
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testBindResourceContainerWithQueryTemplate() throws Exception {
    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerQueryTemplate resourceContainer = new ResourceContainerQueryTemplate();
    ResourceContainerQueryTemplate2 resourceContainer2 = new ResourceContainerQueryTemplate2();
    binder.bind(resourceContainer2);
    binder.bind(resourceContainer);
    assertEquals(3, list.size());
    ResourceContainerQueryTemplateFail containerQueryTemplateFail = new ResourceContainerQueryTemplateFail();
    try {
      binder.bind(containerQueryTemplateFail);
      fail("Binding for this component shoud be failed!");
    } catch (InvalidResourceDescriptorException e) {
      ;
    }

  }

}
