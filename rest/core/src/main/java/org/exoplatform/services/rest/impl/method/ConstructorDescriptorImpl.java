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

package org.exoplatform.services.rest.impl.method;

import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;

import org.exoplatform.services.rest.method.ConstructorDescriptor;
import org.exoplatform.services.rest.method.ConstructorParameter;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConstructorDescriptorImpl implements ConstructorDescriptor {

  @SuppressWarnings("unchecked")
  private final Constructor constructor;
  
  private final List<ConstructorParameter> parameters;
  
  @SuppressWarnings("unchecked")
  public ConstructorDescriptorImpl(Constructor constructor, List<ConstructorParameter> parameters) {
    this.constructor = constructor;
    this.parameters = parameters;
  }
  
  @SuppressWarnings("unchecked")
  public ConstructorDescriptorImpl(Constructor constructor) {
    this(constructor, null);
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
//    throw new UnsupportedOperationException("Not implemneted yet");
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Constructor getConstructor() {
    return constructor;
  }
  
}
