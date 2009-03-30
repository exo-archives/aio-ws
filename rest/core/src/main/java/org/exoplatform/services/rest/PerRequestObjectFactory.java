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

/**
 * Provide object's instance of component that support per-request lifecycle.
 * 
 * @param <T> ObjectModel extensions
 * @see ObjectModel
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PerRequestObjectFactory<T extends ObjectModel> implements ObjectFactory<T> {

  /**
   * Object model that at least gives possibility to create object instance.
   * Should provide full set of available constructors and object fields.
   * 
   * @see ObjectModel
   */
  protected final T model;

  /**
   * @param model any extension of ObectModel
   */
  public PerRequestObjectFactory(T model) {
    this.model = model;
  }

  /**
   * {@inheritDoc}
   */
  public Object getInstance(ApplicationContext context) {
    ConstructorDescriptor inj = model.getConstructorDescriptors().get(0);
    Object object = inj.createInstance(context);

    for (FieldInjector field : model.getFieldInjectors()) {
      field.inject(object, context);
    }

    return object;
  }

  /**
   * {@inheritDoc}
   */
  public T getObjectModel() {
    return model;
  }

}
