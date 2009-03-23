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

package org.exoplatform.services.rest.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ResponseFilter;

/**
 * For injection {@link ResponseFilter} in {@link RequestHandler} at startup.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResponseFilterComponentPlugin extends BaseComponentPlugin {

  /**
   * Logger.
   */
  private static final Log     LOG = ExoLogger.getLogger(RequestFilterComponentPlugin.class.getName());

  /**
   * See {@link ResponseFilter}.
   */
  private Set<Class<? extends ResponseFilter>> fs  = new HashSet<Class<? extends ResponseFilter>>();

  /**
   * @param params initialize parameters from configurations
   * @see InitParams
   */
  @SuppressWarnings("unchecked")
  public ResponseFilterComponentPlugin(InitParams params) {
    if (params != null) {
      Iterator<ValueParam> i = params.getValueParamIterator();
      while (i.hasNext()) {
        ValueParam v = i.next();
        try {
          fs.add((Class<? extends ResponseFilter>) Class.forName(v.getValue()));
        } catch (ClassNotFoundException e) {
          LOG.error("Failed load class " + v.getValue(), e);
        }
      }
    }
  }

  /**
   * @return Collection of classes ResponseFilter supplied in configuration.
   */
  public Set<Class<? extends ResponseFilter>> getFilters() {
    return fs;
  }

}
