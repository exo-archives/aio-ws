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

package org.exoplatform.services.rest.transformer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

/**
 * EntityTransformerFactory produces instances of GenericEntityTransformer.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EntityTransformerFactory {

  private ExoContainer container_;
  
  /**
   * Create a new instance of GenericEntityTransformer.<br/>
   * @param transformerType the type of transformer with should be created.
   */
  public EntityTransformerFactory(ExoContainerContext containerContext) {
    container_ = containerContext.getContainer();
  }
  
  /**
   * Create a new GenericEntityTransformer.<br/>
   * @param transformerType the type of transformer with should be created.
   * @return new instance GenericEntityTransformer
   * @see org.exoplatform.services.rest.transformer.GenericEntityTransformer.
   * @throws Exception create transformer instance exception
   */
  public final GenericEntityTransformer newTransformer(
      Class<? extends GenericEntityTransformer> transformerType) throws Exception {
    Constructor<? extends GenericEntityTransformer>[] constructors = (Constructor<? extends GenericEntityTransformer>[])transformerType.getConstructors();
    // Sort constructors by number of parameters.
    // With more parameters must be first.
    sortConstructorsByParamsLength(constructors, 0, constructors.length - 1);
      
    l:for (Constructor<? extends GenericEntityTransformer> c : constructors) {
      Class<?>[] parameterTypes = c.getParameterTypes();
      if (parameterTypes.length == 0) 
        return c.newInstance();
      
      List<Object> parameters = new ArrayList<Object>(parameterTypes.length);
      for (Class<?> clazz : c.getParameterTypes()) {
        Object p = container_.getComponentInstanceOfType(clazz);
        if (p == null)
          continue l;
        parameters.add(p);
      }
      return c.newInstance(parameters.toArray(new Object[parameters.size()]));
    }
    return null;
  }

  /**
   * @param i0 - index of start element.
   * @param k0 - index of end element.
   */
  private void sortConstructorsByParamsLength(
      Constructor<? extends GenericEntityTransformer>[] constructors, int i0, int k0) {
    int i = i0;
    int k = k0;
    if (k0 > i0) {
      int middleElementParameterArrayLength = constructors[(i0 + k0) / 2].getParameterTypes().length;
      while (i <= k) {
        while ((i < k0) &&
            (constructors[i].getParameterTypes().length > middleElementParameterArrayLength)) {
          i++;
        }
        while ((k > i0) &&
            (constructors[k].getParameterTypes().length < middleElementParameterArrayLength)) {
          k--;
        }
        if (i <= k) {
          swapResources(constructors, i, k);
          i++;
          k--;
        }
      }
      if (i0 < k) {
        sortConstructorsByParamsLength(constructors, i0, k);
      }
      if (i < k0) {
        sortConstructorsByParamsLength(constructors, i, k0);
      }
    }
  }

  private void swapResources(Constructor<? extends GenericEntityTransformer>[] constructors,
      int i, int k) {
    Constructor<? extends GenericEntityTransformer> temp = constructors[i];
    constructors[i] = constructors[k];
    constructors[k] = temp;
  }

}
