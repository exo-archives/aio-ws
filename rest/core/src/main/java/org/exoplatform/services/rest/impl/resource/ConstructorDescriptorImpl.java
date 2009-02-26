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

import java.util.List;
import java.lang.reflect.Constructor;

import org.exoplatform.services.rest.resource.ConstructorDescriptor;
import org.exoplatform.services.rest.resource.ConstructorParameter;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConstructorDescriptorImpl implements ConstructorDescriptor {

  private final Constructor<?>             constructor;

  private final List<ConstructorParameter> parameters;

  public ConstructorDescriptorImpl(Constructor<?> constructor, List<ConstructorParameter> parameters) {
    this.constructor = constructor;
    this.parameters = parameters;
  }

  /**
   * {@inheritDoc}
   */
  public List<ConstructorParameter> getConstructorParameters() {
    return parameters;
  }

  /**
   * {@inheritDoc}
   */
  public void accept(ResourceDescriptorVisitor visitor) {
    // Not used currently, override it if need some validation here.
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * {@inheritDoc}
   */
  public Constructor<?> getConstructor() {
    return constructor;
  }

}
