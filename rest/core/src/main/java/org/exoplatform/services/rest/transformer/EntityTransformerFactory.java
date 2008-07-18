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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

/**
 * EntityTransformerFactory produces instances of GenericEntityTransformer.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EntityTransformerFactory {

  /**
   * ExoContainer instance.
   */
  private ExoContainer container;
  
  /**
   * Constructor comparators.
   */
  private static final ConstructorsComparator COMPARATOR = new ConstructorsComparator(); 
  
  /**
   * Compare constructors by parameter number.
   */
  private static class ConstructorsComparator
      implements Comparator<Constructor<? extends GenericEntityTransformer>> {

    /** 
     * {@inheritDoc}
     */
    public int compare(Constructor<? extends GenericEntityTransformer> constructor1,
        Constructor<? extends GenericEntityTransformer> constructor2) {
      int c1 = constructor1.getParameterTypes().length;
      int c2 = constructor2.getParameterTypes().length;
      if (c1 < c2)
        return 1;
      if (c1 > c2)
        return -1;
      return 0;
    }
    
  }
  
  /**
   * Create a new instance of GenericEntityTransformer.
   * @param containerContext the ExoContainerContext.
   */
  public EntityTransformerFactory(ExoContainerContext containerContext) {
    container = containerContext.getContainer();
  }
  
  /**
   * Create a new GenericEntityTransformer.
   * @param transformerType the type of transformer with should be created.
   * @return new instance GenericEntityTransformer
   * @see org.exoplatform.services.rest.transformer.GenericEntityTransformer.
   * @throws Exception create transformer instance exception
   */
  public final GenericEntityTransformer newTransformer(
      Class<? extends GenericEntityTransformer> transformerType) throws Exception {

    Constructor<? extends GenericEntityTransformer>[] constructors =
      (Constructor<? extends GenericEntityTransformer>[]) transformerType.getConstructors();

    /* Sort constructors by number of parameters.
     * With more parameters must be first.
     */
    Arrays.sort(constructors, COMPARATOR);
      
    l: for (Constructor<? extends GenericEntityTransformer> c : constructors) {
      Class<?>[] parameterTypes = c.getParameterTypes();
      if (parameterTypes.length == 0) 
        return c.newInstance();
      
      List<Object> parameters = new ArrayList<Object>(parameterTypes.length);
      for (Class<?> clazz : c.getParameterTypes()) {
        Object p = container.getComponentInstanceOfType(clazz);
        if (p == null)
          continue l;
        parameters.add(p);
      }
      return c.newInstance(parameters.toArray(new Object[parameters.size()]));
    }
    return null;
  }

}
