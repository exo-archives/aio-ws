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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;

/**
 * Provide cache for {@link JAXBContext}.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML })
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML, MediaTypeHelper.WADL })
public class JAXBContextResolver implements ContextResolver<JAXBContextResolver> {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(JAXBContextResolver.class.getName());

  /**
   * Classes map's key.
   */
  public static class ClassesKey {

    /**
     * Hash code for class array.
     */
    private int        hash = 0;

    /**
     * Classes.
     */
    private Class<?>[] classes;

    /**
     * @param classes classes 
     */
    public ClassesKey(Class<?>... classes) {
      for (Class<?> cl : classes)
        hash += cl.hashCode();
      this.classes = classes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ClassesKey other = (ClassesKey) obj;
      if (classes.length != other.classes.length)
        return false;
      if (hash != other.hash)
        return false;
      l: for (Class<?> cl1 : classes) {
        for (Class<?> cl2 : other.classes) {
          if (cl2 == cl1)
            continue l;
        }
        return false;
      }

      return true;

    }

  }

  /**
   * JAXBContext cache.
   */
  private final ConcurrentHashMap<ClassesKey, JAXBContext> jaxbContexts = new ConcurrentHashMap<ClassesKey, JAXBContext>();

  /**
   * {@inheritDoc}
   */
  public JAXBContextResolver getContext(Class<?> type) {
    return this;
  }

  /**
   * Return JAXBContext according to supplied type. If no one context found then
   * try create new context and save it in cache.
   * 
   * @param classes classes to be bound
   * @return JAXBContext
   * @throws JAXBException if JAXBContext creation failed
   */
  public JAXBContext getJAXBContext(Class<?>... classes) throws JAXBException {
    ClassesKey key = new ClassesKey(classes);
    JAXBContext jaxbctx = jaxbContexts.get(key);
    if (jaxbctx == null) {
      jaxbctx = JAXBContext.newInstance(classes);
      jaxbContexts.put(key, jaxbctx);
    }
    return jaxbctx;
  }

  /**
   * Create and add in cache JAXBContext for supplied set of classes.
   * 
   * @param classes set of java classes to be bound
   * @return JAXBContext
   * @throws JAXBException if JAXBContext for supplied classes can't be created
   *           in any reasons
   */
  public JAXBContext createJAXBContext(Class<?>... classes) throws JAXBException {
    JAXBContext jaxbctx = JAXBContext.newInstance(classes);
    addJAXBContext(jaxbctx, classes);
    return jaxbctx;
  }

  /**
   * Add prepared JAXBContext that will be mapped to set of class. In this case
   * this class works as cache for JAXBContexts.
   * 
   * @param jaxbctx JAXBContext
   * @param classes set of java classes to be bound
   */
  public void addJAXBContext(JAXBContext jaxbctx, Class<?>... classes) {
    jaxbContexts.put(new ClassesKey(classes), jaxbctx);
  }

  /**
   * @param plugin for injection prepared JAXBContext at startup
   */
  public void addPlugin(ComponentPlugin plugin) {
    if (plugin instanceof JAXBContextComponentPlugin) {
      for (Iterator<Class<?>> i = ((JAXBContextComponentPlugin) plugin).getJAXBContexts()
                                                                       .iterator(); i.hasNext();) {
        Class<?> c = i.next();
        try {
          createJAXBContext(c);
        } catch (JAXBException e) {
          LOG.error("Failed add JAXBContext for class " + c.getName(), e);
        }
      }
    }
  }

}
