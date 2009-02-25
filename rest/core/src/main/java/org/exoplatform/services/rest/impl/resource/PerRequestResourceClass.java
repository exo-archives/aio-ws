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

package org.exoplatform.services.rest.impl.resource;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PerRequestResourceClass extends ResourceClass {

  public PerRequestResourceClass(AbstractResourceDescriptor resourceDescriptor) {
    super(resourceDescriptor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getResource(GenericContainerRequest request, GenericContainerResponse response) {
    try {
      return createObject(getResourceClass());
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  private Object createObject(Class<?> clazz) throws Exception {

    ExoContainer container = ExoContainerContext.getCurrentContainer();

    Constructor<?>[] constructors = clazz.getConstructors();

    Arrays.sort(constructors, COMPARATOR);

    for (Constructor<?> c : constructors) {
      Class<?>[] parameterTypes = c.getParameterTypes();
      if (parameterTypes.length == 0)
        return c.newInstance();

      List<Object> parameters = new ArrayList<Object>(parameterTypes.length);

      for (Class<?> parameterType : parameterTypes) {
        Object param = container.getComponentInstanceOfType(parameterType);
        if (param == null)
          break;
        parameters.add(param);
      }
      
      if (parameters.size() == parameterTypes.length)
        return c.newInstance(parameters.toArray(new Object[parameters.size()]));
    }
    
    return null;
  }

  private static final Comparator<Constructor<?>> COMPARATOR = new Comparator<Constructor<?>>() {
    public int compare(Constructor<?> o1, Constructor<?> o2) {
      return o2.getParameterTypes().length - o1.getParameterTypes().length;
    }
  };

}
