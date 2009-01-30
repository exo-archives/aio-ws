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

package org.exoplatform.services.rest.impl.method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.method.MethodInvokerFilter;

/**
 * For injection {@link MethodInvokerFilter} in {@link RequestHandler} at
 * startup.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MethodInvokerFilterComponentPlugin extends BaseComponentPlugin {

  /**
   * Logger.
   */
  private static final Log          LOG  = ExoLogger.getLogger(MethodInvokerFilterComponentPlugin.class.getName());

  /**
   * List of {@link MethodInvokerFilter}.
   */
  private List<MethodInvokerFilter> mifs = new ArrayList<MethodInvokerFilter>();

  /**
   * @param params initialize parameters from configuration
   * @see InitParams
   */
  @SuppressWarnings("unchecked")
  public MethodInvokerFilterComponentPlugin(InitParams params) {
    if (params != null) {
      Iterator<ValueParam> i = params.getValueParamIterator();
      while (i.hasNext()) {
        ValueParam v = i.next();
        try {
          Class c = Class.forName(v.getValue());
          MethodInvokerFilter mif = (MethodInvokerFilter) c.newInstance();
          mifs.add(mif);
        } catch (Exception e) {
          LOG.error("Can't instantiate method invoker filter " + v.getValue(), e);
        }
      }
    }
  }

  /**
   * @return get list of {@link MethodInvokerFilter} supplied from configuration
   */
  public List<MethodInvokerFilter> getFilters() {
    return mifs;
  }

}
