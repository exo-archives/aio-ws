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

/**
 * EntityTransformerFactory produces instances of GenericEntityTransformer.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EntityTransformerFactory {

  private final Class<? extends GenericEntityTransformer> transformerType_;

  /**
   * Create a new instance of GenericEntityTransformer.<br/>
   * @param transformerType the type of transformer with should be creted.
   */
  public EntityTransformerFactory(
      Class<? extends GenericEntityTransformer> transformerType) {
    this.transformerType_ = transformerType;
  }

  /**
   * Create a new GenericEntityTransformer.<br/>
   * @return new instance GenericEntityTransformer
   * @see org.exoplatform.services.rest.transformer.GenericEntityTransformer
   * @throws Exception create transformer instance exception
   */
  public final GenericEntityTransformer newTransformer() throws Exception {
    return transformerType_.newInstance();
  }

}
