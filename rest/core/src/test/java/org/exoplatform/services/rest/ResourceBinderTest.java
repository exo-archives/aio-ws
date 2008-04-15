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

import java.util.List;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceContainerMimeTypes;
import org.exoplatform.services.rest.container.ResourceContainerQueryTemplate;
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

  public void setUp() throws Exception {
    StandaloneContainer
        .setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
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
      i--;
    }
    binder.unbind(resourceContainer);
    assertEquals(0, list.size());
  }

  public void testBindResourceContainerWithQueryTemplate() throws Exception {
    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainerQueryTemplate resourceContainer = new ResourceContainerQueryTemplate();
    ResourceContainerQueryTemplate resourceContainer2 = new ResourceContainerQueryTemplate();
    binder.bind(resourceContainer2);
    binder.bind(resourceContainer);
    assertEquals(3, list.size());
    ResourceContainerQueryTemplateFail containerQueryTemplateFail =
      new ResourceContainerQueryTemplateFail();
    try {
      binder.bind(containerQueryTemplateFail);
      fail("Binding for this component shoud be failed!");
    } catch (InvalidResourceDescriptorException e) {
      ;
    }
    binder.unbind(resourceContainer);
    binder.unbind(resourceContainer2);
    assertEquals(0, list.size());
  }

}
