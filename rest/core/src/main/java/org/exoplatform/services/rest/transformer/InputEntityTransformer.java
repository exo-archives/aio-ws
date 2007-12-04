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

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic implementations of GenericInputEntityTransformer.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class InputEntityTransformer implements
    GenericInputEntityTransformer {

  /**
   * type of Objects which InputEntityTransformer can serve.
   */
  protected Class<?> entityType_;

  /**
   * With this constructor from ExoContainer can be got any Objects. 
   * @param components the Objects from ExoContainer.
   * @see EntityTransformerFactory#newTransformer(Class).
   */
  public InputEntityTransformer(Object... components) {
  }

  /**
   * Set the type of Objects which should be serve by InputEntityTransformer.
   * @param entityType the type of entity
   */
  public final void setType(Class<?> entityType) {
    this.entityType_ = entityType;
  }

  /**
   * Get the type of served Objects.
   * @return type of served object
   */
  public final Class<?> getType() {
    return entityType_;
  }

  /**
   * Build Objects from given InputStream.
   * @param entityDataStream from this InputStream Object should be readed
   * @return Object builded Object
   * @throws IOException Input/Output Exception
   */
  abstract public Object readFrom(InputStream entityDataStream)
      throws IOException;

}
