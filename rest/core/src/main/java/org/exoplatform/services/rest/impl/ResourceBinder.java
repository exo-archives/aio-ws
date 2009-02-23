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

package org.exoplatform.services.rest.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.impl.resource.ResourceClass;
import org.exoplatform.services.rest.impl.resource.ResourceDescriptorFactory;
import org.exoplatform.services.rest.impl.resource.ResourceDescriptorValidator;
import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * Lookup for root resource eXo container components at startup and
 * register/unregister resources via specified methods.
 *
 * @see AbstractResourceDescriptor
 * @see ResourceClass
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ResourceBinder {

  /**
   * Logger.
   */
  private static final Log                       LOG           = ExoLogger.getLogger("ws.rest.core.ResourceBinder");

  /**
   * Collection of root resource descriptors.
   */
  private List<ResourceClass>                    rootResources = new ArrayList<ResourceClass>();

  /**
   * Compare two {@link ResourceClass}.
   */
  private static final Comparator<ResourceClass> COMPARATOR = new Comparator<ResourceClass>() {
    /**
     * Compare two ResourceClass for order.
     * 
     * @param o1 first ResourceClass to be compared
     * @param o2 second ResourceClass to be compared
     * @return positive, zero or negative dependent of {@link UriPattern}
     *         comparison
     * @see Comparator#compare(Object, Object)
     * @see UriPattern
     * @see UriPattern#URIPATTERN_COMPARATOR
     */
    public int compare(ResourceClass o1, ResourceClass o2) {
      return UriPattern.URIPATTERN_COMPARATOR.compare(o1.getUriPattern(), o2.getUriPattern());
    }
  };
  
  /**
   * @param containerContext eXo container context
   * @throws Exception if can't set instance of {@link RuntimeDelegate}
   */
  public ResourceBinder(ExoContainerContext containerContext) throws Exception {
    // initialize RuntimeDelegate instance
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());

    init(containerContext.getContainer());
  }
  
  /**
   * Register supplied Object as root resource if it has valid JAX-RS
   * annotations and no one resource with the same UriPattern already
   * registered.
   * 
   * @param resource candidate to be root resource
   */
  public synchronized boolean bind(final Object resource) {
    AbstractResourceDescriptor ard = ResourceDescriptorFactory.createAbstractResourceDescriptor(resource.getClass());

    validate(ard);

    if (ard.isRootResource()) {

      /*
       * Some validation done here, see
       * ResourceClass#processSubResourceLocators(SubResourceLocatorMap) and
       * ResourceClass#addResourceMethod(ResourceMethodMap, ResourceMethodDescriptor)
       */
      ResourceClass rc = new ResourceClass(ard, resource);
      
      for (ResourceClass r : getRootResources()) {
        if (r.getUriPattern().equals(rc.getUriPattern())) {
          LOG.warn("Ignore resource container " + rc.getClass().getName() + " the same pattern "
                   + rc.getUriPattern() + " already registered.");
          
          return false;
        }
      }
      getRootResources().add(rc);
      Collections.sort(getRootResources(), COMPARATOR);
      LOG.info("Bind new resource " + rc.getUriPattern().getRegex() + " : " + rc.getResourceClass());
      return true;
    } 
    LOG.warn("Ignore resource container "
        + ard.getResourceClass().getClass().getName()
        + " it is not root resource. Path annotation javax.ws.rs.Path is not specified for this class.");
    return false;
  }

  /**
   * Validate {@link AbstractResourceDescriptor}.
   * 
   * @param ard AbstractResourceDescriptor to be validated
   * @see ResourceDescriptorValidator
   */
  private static void validate(AbstractResourceDescriptor ard) {
    ResourceDescriptorValidator rdv = new ResourceDescriptorValidator();

    // validate AbstractResourceDescriptor
    ard.accept(rdv);

    // validate each ResourceMethodDescriptor
    for (ResourceMethodDescriptor rmd : ard.getResourceMethodDescriptors())
      rmd.accept(rdv);

    // validate each SubResourceMethodDescriptor
    for (SubResourceMethodDescriptor srmd : ard.getSubResourceMethodDescriptors())
      srmd.accept(rdv);

    // validate each SubResourceLocatorDescriptor
    for (SubResourceLocatorDescriptor srld : ard.getSubResourceLocatorDescriptors())
      srld.accept(rdv);
  }

  /**
   * Remove root resource of supplied class from root resource collection.
   * 
   * @param clazz root resource class
   */
  @SuppressWarnings("unchecked")
  public synchronized boolean unbind(Class clazz) {
    Iterator<ResourceClass> i = getRootResources().iterator();

    while (i.hasNext()) {
      ResourceClass rc = i.next();
      Class c = rc.getResourceClass();
      if (clazz.equals(c)) {
        i.remove();
        LOG.info("Remove ResourceContainer " + rc.getUriPattern().getRegex() + " : " + c);
        return true;
      }
    }
    return false;
  }

  /**
   * Clear the list of ResourceContainer description.
   */
  public void clear() {
    getRootResources().clear();
  }

  /**
   * @return all registered root resources
   */
  public List<ResourceClass> getRootResources() {
    return rootResources;
  }

  //
  
  /**
   * Lookup all object which implements {@link ResourceContainer} interface and
   * process them to be add as root resources.
   * 
   * @param container eXo container
   * @see ResourceContainer
   */
  @SuppressWarnings("unchecked")
  private void init(ExoContainer container) {
    List<Application> al = container.getComponentInstancesOfType(Application.class);
    for (Application a : al) {
      for (Object obj : a.getSingletons()) {
        if (obj.getClass().getAnnotation(Provider.class) != null)
          ; // provider
        else
          bind(obj);
      }
      for (Class<?> clazz : a.getClasses()) {
        
      }
    }
    
    List<ResourceContainer> rcl = container.getComponentInstancesOfType(ResourceContainer.class);
    for (ResourceContainer rc : rcl)
      bind(rc);
  }

}
