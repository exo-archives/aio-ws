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
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.exoplatform.container.component.ComponentPlugin;

/**
 * Provide cache for {@link JAXBContext}.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

  /**
   * JAXBContext cache.
   */
  private final Map<Class<?>, JAXBContext> jaxbContexts;

  /**
   * Constructs new instance of JAXBContextResolver.
   */
  public JAXBContextResolver() {
    this.jaxbContexts = new HashMap<Class<?>, JAXBContext>();
  }

  /**
   * Return JAXBContext according to supplied type. If no one context found then
   * try create new context and save it in cache. {@inheritDoc}
   */
  public JAXBContext getContext(Class<?> type) {
    try {
      JAXBContext c = jaxbContexts.get(type);
      if (c == null) {
        synchronized (jaxbContexts) {
          c = JAXBContext.newInstance(type);
          jaxbContexts.put(type, c);
        }
      }
      return c;
    } catch (JAXBException e) {
      return null;
    }
  }

  /**
   * @param plugin for injection prepared JAXBContext at startup
   */
  public void addPlugin(ComponentPlugin plugin) {
    if (plugin instanceof JAXBContextComponentPlugin) {
      jaxbContexts.putAll(((JAXBContextComponentPlugin) plugin).getJAXBContexts());
    }
  }

}
