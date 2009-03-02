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

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.provider.ProviderDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SingletonProviderFactory extends ProviderFactory {

  /**
   * Provider object.
   */
  protected final Object provider;
  
  /**
   * @param providerDescriptor See {@link ProviderDescriptor}
   * @param provider provider instance
   */
  public SingletonProviderFactory(ProviderDescriptor providerDescriptor, Object provider) {
    super(providerDescriptor);
    this.provider=provider;
  }

  /**
   * {@inheritDoc}
   */
  public Object getProvider(ApplicationContext context) {
    return provider;
  }

}
