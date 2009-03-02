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

package org.exoplatform.services.rest.impl.provider;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.ConstructorInjector;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.impl.ConstructorInjectorImpl;
import org.exoplatform.services.rest.impl.FieldInjectorImpl;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.provider.ProviderDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProviderDescriptorImpl implements ProviderDescriptor {

  /**
   * Provider class.
   */
  private final Class<?>                  providerClass;

  /**
   * Resource class constructors.
   * 
   * @see {@link ConstructorInjector}
   */
  private final List<ConstructorInjector> constructorDescriptors;

  /**
   * Resource class fields.
   */
  private final List<FieldInjector>       fields;

  /**
   * List of media types which this method can consume. See
   * {@link javax.ws.rs.Consumes} .
   */
  private final List<MediaType>           consumes;

  /**
   * List of media types which this method can produce. See
   * {@link javax.ws.rs.Produces} .
   */
  private final List<MediaType>           produces;

  /**
   * @param providerClass provider class
   */
  public ProviderDescriptorImpl(Class<?> providerClass) {
    this.providerClass = providerClass;
    this.consumes = MediaTypeHelper.createConsumesList(providerClass.getAnnotation(Consumes.class));
    this.produces = MediaTypeHelper.createProducesList(providerClass.getAnnotation(Produces.class));
    this.constructorDescriptors = new ArrayList<ConstructorInjector>();
    this.fields = new ArrayList<FieldInjector>();

    for (java.lang.reflect.Field jfield : providerClass.getDeclaredFields()) {
      getFieldInjectors().add(new FieldInjectorImpl(providerClass, jfield));
    }

    for (Constructor<?> constructor : providerClass.getConstructors()) {
      getConstructorInjectors().add(new ConstructorInjectorImpl(providerClass, constructor));
    }

  }

  /**
   * {@inheritDoc}
   */
  public List<MediaType> consumes() {
    return consumes;
  }

  /**
   * {@inheritDoc}
   */
  public List<ConstructorInjector> getConstructorInjectors() {
    return constructorDescriptors;
  }

  /**
   * {@inheritDoc}
   */
  public List<FieldInjector> getFieldInjectors() {
    return fields;
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getProviderClass() {
    return providerClass;
  }

  /**
   * {@inheritDoc}
   */
  public List<MediaType> produces() {
    return produces;
  }

}
