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

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.exoplaform.services.rest.test.SimpleRestResponse;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.container.ResourceDescriptor;

/**
 * Created by The eXo Platform SARL Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua 19 Гру 2007
 */
public class StressRestResponseTest extends TestCase {
  
  private StandaloneContainer container;
  private ResourceBinder binder;
  private ResourceDispatcher dispatcher;
  
  private static int THREAD_MAX = 50;
  private int thread_count;
  
  public synchronized void dispose()  {
    thread_count = thread_count - 1;
  }
  public boolean isEmpty(){
    return (thread_count == 0)?true:false;
  }
  
  public void setUp() throws Exception {
    StandaloneContainer
        .setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
    binder.clear();
    dispatcher = (ResourceDispatcher) container
        .getComponentInstanceOfType(ResourceDispatcher.class);
    
    thread_count = THREAD_MAX;
  }

  public void testResponse() throws Exception {
    assertNotNull(dispatcher);
    assertNotNull(binder);

    System.out.println("!!! === >>> Start of stress test");
    ;

    List<ResourceDescriptor> list = binder.getAllDescriptors();
    ResourceContainer resourceContainer = new SimpleRestResponse();
    binder.bind(resourceContainer);
    assertEquals(1, list.size());

    
    for(int count = 0; count < THREAD_MAX; count++){ 
      new GetThread(count).start();
      System.out.println("+++++ Thread# "+count+" started !!");
    }
  
    while (!isEmpty()); // wait

    binder.unbind(resourceContainer);
    assertEquals(0, list.size());

    System.out.println("!!! === >>> End of stress test");

  }

  public void tearDown() throws Exception {

  }
  
  class GetThread extends Thread {

    public GetThread(int number) {
      super("Thread# "+number);
    }
    
    public void run() {

      String[] req_strs = { "hello", "mazda6", "hibernate" };

      for (String str : req_strs) {
        MultivaluedMetadata mv = new MultivaluedMetadata();
        String baseURI = "http://localhost:8080/rest/";

        String extURI = String.format("/getter/%s/", str);

        Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv,
            null);
        Response response = null;
        try {
          response = dispatcher.dispatch(request);
        } catch (Exception e) {
          e.printStackTrace();
          dispose();
          stop();
          fail("!!! === >>> Cannot dispatch request !!!");
        }
        
        String response_str = "";
        try {
          assertEquals(200, response.getStatus());
          response_str = response.getEntity().toString(); 
          assertEquals(str, response_str);
        } catch (ComparisonFailure e) {
          
          e.printStackTrace();
          dispose();
          stop();
          
          fail("!!! === >> Comparision failure : Request: " + str+ " Response : "+response_str);
          
        }
        
        try {
          sleep((long) Math.random() * 1000);
        } catch (InterruptedException e) {
          fail("Thread halted abnormaly");
        }

      }

      dispose();
      
      System.out.println("----- "+this.getName()+" halted!");

    }
  }
  

}
