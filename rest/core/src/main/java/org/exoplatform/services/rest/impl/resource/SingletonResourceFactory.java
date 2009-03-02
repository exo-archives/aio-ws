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

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SingletonResourceFactory extends ResourceFactory {

  /**
   * Resource object.
   */
  protected final Object resource;

  /**
   * Constructor.
   * 
   * @param resourceDescriptor See {@link AbstractResourceDescriptor}
   * @param resource resource instance
   */
  public SingletonResourceFactory(AbstractResourceDescriptor resourceDescriptor, Object resource) {
    super(resourceDescriptor);
    this.resource = resource;
  }


  /**
   * {@inheritDoc}
   */
  public Object getResource(ApplicationContext context) {
    return resource;
  }

}
