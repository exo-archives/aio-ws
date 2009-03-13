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

package org.exoplatform.services.rest;

import java.lang.reflect.Constructor;
import java.util.Collections;

import javax.ws.rs.Path;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.impl.ConstructorInjectorImpl;
import org.exoplatform.services.rest.impl.FieldInjectorImpl;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.services.rest.impl.resource.PathValue;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseTest extends TestCase {

  protected StandaloneContainer container;
  
  protected RuntimeDelegateImpl rd;

  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    rd = RuntimeDelegateImpl.getInstance();
  }

  protected AbstractResourceDescriptor createResourceDescriptor(Class<?> clazz) {
    PathValue path = new PathValue(clazz.getAnnotation(Path.class).value());
    AbstractResourceDescriptor descriptor = new AbstractResourceDescriptorImpl(path, clazz);
    for (Constructor<?> constr : clazz.getConstructors())
      descriptor.getConstructorInjectors().add(new ConstructorInjectorImpl(clazz, constr));
    Collections.sort(descriptor.getConstructorInjectors(),
                     ConstructorInjectorImpl.CONSTRUCTOR_COMPARATOR);
    for (java.lang.reflect.Field jfield : clazz.getDeclaredFields()) {
      descriptor.getFieldInjectors().add(new FieldInjectorImpl(clazz, jfield));
    }
    return descriptor;
  }
  
}
