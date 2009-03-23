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

import java.util.List;

/**
 * Abstract description of object.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ObjectModel {

  /**
   * @return collections constructor, MAY return empty collection or null if
   *         object is singleton. There is no setter for this to add new
   *         ConstructorInjector use
   *         <code>ObjectModel.getConstructorInjectors().add(ConstructorInjector)</code>
   */
  List<ConstructorInjector> getConstructorInjectors();

  /**
   * @return collections of object fields, MAY return empty collection or null
   *         if object is singleton. There is no setter for this to add new
   *         ConstructorInjector use
   *         <code>ObjectModel.getFieldInjectors().add(FieldInjector)</code>
   */
  List<FieldInjector> getFieldInjectors();

  /**
   * @return {@link Class} of object
   */
  Class<?> getObjectClass();

}
