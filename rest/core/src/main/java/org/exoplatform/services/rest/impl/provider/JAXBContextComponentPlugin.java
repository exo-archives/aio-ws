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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.apache.commons.logging.Log;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;

/**
 * For injection JAXBContext from configuration at startup in {@link JAXBContextResolver}.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JAXBContextComponentPlugin extends BaseComponentPlugin {
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(JAXBContextComponentPlugin.class.getName());
  
  /**
   * See {@link JAXBContext}.
   */
  private final Map<Class<?>, JAXBContext> jcs = new HashMap<Class<?>, JAXBContext>();
  
  
  /**
   * @param params initialize parameters
   * @see InitParams
   */
  @SuppressWarnings("unchecked")
  public JAXBContextComponentPlugin(InitParams params) {
    if (params != null) {
      Iterator<ValueParam> i = params.getValueParamIterator();
      while (i.hasNext()) {
        ValueParam v = i.next();
        try {
          Class c = Class.forName(v.getValue());
          jcs.put(c, JAXBContext.newInstance(c));
        } catch (Exception e) {
          LOG.warn("Can't create JAXB context for class with name " + v.getValue(), e);
        }
      }
    }
  }
  
  /**
   * @return collection of {@link JAXBContext}
   */
  public Map<Class<?>, JAXBContext> getJAXBContexts() {
    return jcs;
  }

}
