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
 * Provide object instance of components that support singleton lifecycle.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @param <T>
 */
public class SingletonObjectFactory<T extends ObjectModel> implements ObjectFactory<T> {

  /**
   * @see ObjectModel.
   */
  protected final T      model;

  /**
   * Component instance.
   */
  protected final Object object;

  /**
   * @param model ObjectMode
   * @param object component instance
   */
  public SingletonObjectFactory(T model, Object object) {
    this.model = model;
    this.object = object;
  }

  /**
   * {@inheritDoc}
   */
  public Object getInstance(ApplicationContext context) {
    // prepared object instance
    return object;
  }

  /**
   * {@inheritDoc}
   */
  public T getObjectModel() {
    return model;
  }

}
