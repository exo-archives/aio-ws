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

import org.exoplatform.container.ExoContainerContext;

/**
 * Factory provides object that is created and is manageable by
 * inversion-of-control container, PicoContainer.
 * 
 * @param <T> any extension of {@link ObjectModel}
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */

public class ContainerObjectFactory<T extends ObjectModel> implements ObjectFactory<T> {

  /**
   * Object model.
   */
  protected final T model;

  /**
   * @param model object model
   * @see ObjectModel
   */
  public ContainerObjectFactory(T model) {
    this.model = model;
  }

  /**
   * {@inheritDoc}
   */
  public Object getInstance(ApplicationContext context) {
    Class<?> clazz = model.getObjectClass();
    return ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(clazz);
  }

  /**
   * {@inheritDoc}
   */
  public T getObjectModel() {
    return model;
  }

}
