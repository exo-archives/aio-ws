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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.test.JSON2BeanConvertor;

/**
 * Created by The eXo Platform SARL Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */

public class JSONJavaBeanTransformerTest extends TestCase {

  private StandaloneContainer container;

  private ResourceBinder      binder;

  private ResourceDispatcher  dispatcher;

  @Override
  protected void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();

    StandaloneContainer
        .setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
    binder.clear();
    dispatcher = (ResourceDispatcher) container
        .getComponentInstanceOfType(ResourceDispatcher.class);

  }

  public void testJsonResponse() throws Exception {

    assertNotNull(dispatcher);
    assertNotNull(binder);

    ResourceContainer resourceContainer = new JSON2BeanConvertor();
    binder.bind(resourceContainer);

    InputStream entityStream = getClass().getResourceAsStream("/book.txt");
    assertNotNull(entityStream);

    String extURI = "/json/test/";
    Request request = new Request(entityStream, new ResourceIdentifier(extURI), "GET",
        new MultivaluedMetadata(), null);
    Response response = null;
    try {
      response = dispatcher.dispatch(request);
    } catch (Exception e) {
      e.printStackTrace();
      fail("!!! === >>> Cannot dispatch request !!!");
    }

    try {
      assertEquals(200, response.getStatus());
      System.out.println("JSON test !! - START");

      System.out.println(response.getEntity());

      System.out.println("\nJSON test !! - END");
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      fail("!!! === >> Comparision failure");
    }

    entityStream.close();

  }

  @Override
  protected void tearDown() throws Exception {
    // TODO Auto-generated method stub
    super.tearDown();
  }

}
