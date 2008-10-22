/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.rest;

import java.util.List;

import junit.framework.TestCase;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.container.ResourceDescriptor;
import org.exoplatform.services.rest.container.SimpleRestResponse;

/**
 * Created by The eXo Platform SARL Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */
public class StressRestResponseTest extends TestCase {

  private StandaloneContainer container;
  private ResourceBinder binder;
  private ResourceDispatcher dispatcher;

  private static int THREAD_MAX = 50;
  private int thread_count;

  private synchronized void dispose() {
    thread_count--;
  }

  private boolean isEmpty() {
    return thread_count == 0;
  }

  /*
   * (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath(
        "src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
    binder.clear();
    dispatcher = (ResourceDispatcher) container.getComponentInstanceOfType(
        ResourceDispatcher.class);

    thread_count = THREAD_MAX;
  }

  public void testResponse() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainer resourceContainer = new SimpleRestResponse();
    binder.bind(resourceContainer);
    assertEquals(1, list.size());

    for (int count = 0; count < THREAD_MAX; count++)
      new GetThread().start();

    while (!isEmpty())
      ; // wait

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());

  }


  class GetThread extends Thread {
    
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

      MultivaluedMetadata mv = new MultivaluedMetadata();

      Request request = new Request(null, new ResourceIdentifier("/getter/" +
          this.getId()), "GET", mv, null);
      Response response = null;
      try {
        response = dispatcher.dispatch(request);
      } catch (Exception e) {
        e.printStackTrace();
        dispose();
      }

      assertEquals(HTTPStatus.OK, response.getStatus());
      assertEquals(this.getId() + "", response.getEntity());
      dispose();

    }

  }

}
