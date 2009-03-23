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
 * Implementation of this interface should be able provide object instance
 * dependent of component lifecycle.
 * 
 * @param <T> ObjectModel extensions
 * @see ObjectModel
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ObjectFactory<T extends ObjectModel> {

  /**
   * Create object instance. ApplicationContext can be used for getting required
   * parameters for object constructors or fields.
   * 
   * @param context ApplicationContext
   * @return object instance
   */
  Object getInstance(ApplicationContext context);

  /**
   * @return any extension of {@link ObjectModel}. That must allows create
   *         object instance and initialize object's fields for per-request
   *         resources
   */
  T getObjectModel();

}
