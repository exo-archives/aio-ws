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

package org.exoplatform.services.rest.impl;

import javax.ws.rs.GET;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConstructorInjectorTest extends BaseTest {

  public static class Resource1 {

    public Resource1() {
      fail(); // this constructor must not be used. There is constructors with
              // more parameter
    }

    public Resource1(TestContainerComponent tc) {
      assertNotNull(tc);
    }

    @GET
    public void m0() {
    }
  }

  public void setUp() throws Exception {
    super.setUp();
    container.registerComponentInstance(TestContainerComponent.class.getName(),
                                        new TestContainerComponent());
  }

  public static class TestContainerComponent {
    public String name = this.getClass().getName();
  }

  public void testConstructorInjectorFail() {
    AbstractResourceDescriptor resource = new AbstractResourceDescriptorImpl(Resource1.class);
  }

}
