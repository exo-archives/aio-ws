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
import java.util.List;

import org.exoplatform.services.rest.resource.ResourceDescriptor;

/**
 * Abstraction of constructor descriptor. Used for create object instance when
 * type is used in per-request lifecycle.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ConstructorDescriptor extends ResourceDescriptor {

  /**
   * @param context ApplicationContext
   * @return newly created instance of the constructor's
   * @see ApplicationContext
   */
  Object createInstance(ApplicationContext context);

  /**
   * Get source constructor.
   * 
   * @return constructor
   * @see Constructor
   */
  Constructor<?> getConstructor();

  /**
   * @return constructor's parameters
   */
  List<ConstructorParameter> getParameters();

}
