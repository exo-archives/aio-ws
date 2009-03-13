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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ConstructorInjector;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.services.rest.impl.resource.PathValue;
import org.exoplatform.services.rest.impl.resource.PerRequestResourceFactory;
import org.exoplatform.services.rest.impl.resource.ResourceFactory;
import org.exoplatform.services.rest.impl.resource.SingletonResourceFactory;
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
 * @see SingletonResourceFactory
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ResourceBinder {

  /**
   * Logger.
   */
  private static final Log                         LOG           = ExoLogger.getLogger("ws.rest.core.ResourceBinder");

  /**
   * Collection of root resource descriptors.
   */
  private List<ResourceFactory>                    rootResources = new ArrayList<ResourceFactory>();

  /**
   * Compare two {@link SingletonResourceFactory}.
   */
  private static final Comparator<ResourceFactory> COMPARATOR    = new Comparator<ResourceFactory>() {
    /**
     * Compare two ResourceClass for order.
     * 
     * @param o1 first ResourceClass to be compared
     * @param o2 second ResourceClass to be compared
     * @return positive , zero or negative dependent of {@link UriPattern}
     *         comparison
     * @see Comparator#compare(Object, Object)
     * @see UriPattern
     * @see UriPattern#URIPATTERN_COMPARATOR
     */
    public int compare(ResourceFactory o1, ResourceFactory o2) {
      return UriPattern.URIPATTERN_COMPARATOR.compare(o1.getUriPattern(), o2.getUriPattern());
    }
  };
  
  /**
   * Validator.
   */
  private  ResourceDescriptorValidator rdv = new ResourceDescriptorValidator();

  /**
   * @param containerContext eXo container context
   * @throws Exception if can't set instance of {@link RuntimeDelegate}
   */
  public ResourceBinder(ExoContainerContext containerContext) throws Exception {
    // Initialize RuntimeDelegate instance
    // This is first component in life cycle what needs.
    // TODO better solution to initialize RuntimeDelegate
    RuntimeDelegateImpl rd = new RuntimeDelegateImpl();
    RuntimeDelegate.setInstance(rd);

    init(containerContext.getContainer(), rd);
  }

  /**
   * Register supplied Object as root resource if it has valid JAX-RS
   * annotations and no one resource with the same UriPattern already
   * registered.
   * 
   * @param resource candidate to be root resource
   * @return true if resource was bound and false if resource was not bound
   *         cause it is not root resource
   */
  public synchronized boolean bind(final Object resource) {
    Class<?> resourceClass = resource.getClass();
    final Path path = resourceClass.getAnnotation(Path.class);

    AbstractResourceDescriptor descriptor = null;
    if (path != null) {
      descriptor = new AbstractResourceDescriptorImpl(new PathValue(path.value()), resourceClass);
    } else {
      LOG.warn("Resource class " + resourceClass + " it is not root resource. "
               + "Path annotation javax.ws.rs.Path is not specified for this class.");
      return false;
    }

    for (ResourceFactory r : getRootResources()) {
      if (r.getUriPattern().equals(descriptor.getUriPattern())) {
        String msg = "Resource class " + descriptor.getResourceClass().getClass().getName()
            + " can't be registered. Resource class " + r.getClass().getName()
            + " with the same pattern " + r.getUriPattern() + " already registered.";
        throw new RuntimeException(msg);
      }
    }

    // validate AbstractResourceDescriptor
    descriptor.accept(rdv);
    // validate each ResourceMethodDescriptor
    for (ResourceMethodDescriptor rmd : descriptor.getResourceMethodDescriptors()) {
      rmd.accept(rdv);
    }
    // validate each SubResourceMethodDescriptor
    for (SubResourceMethodDescriptor srmd : descriptor.getSubResourceMethodDescriptors()) {
      srmd.accept(rdv);
    }
    // validate each SubResourceLocatorDescriptor
    for (SubResourceLocatorDescriptor srld : descriptor.getSubResourceLocatorDescriptors()) {
      srld.accept(rdv);
    }

    ResourceFactory rc = new SingletonResourceFactory(descriptor, resource);
    getRootResources().add(rc);
    Collections.sort(getRootResources(), COMPARATOR);
    LOG.info("Bind new resource " + rc.getUriPattern().getRegex() + " : " + resourceClass);
    return true;
  }

  /**
   * @param resourceClass class of candidate to be root resource
   * @return true if resource was bound and false if resource was not bound
   *         cause it is not root resource
   */
  public synchronized boolean bind(final Class<?> resourceClass) {
    final Path path = resourceClass.getAnnotation(Path.class);

    AbstractResourceDescriptor descriptor = null;
    if (path != null) {
      descriptor = new AbstractResourceDescriptorImpl(new PathValue(path.value()), resourceClass);
    } else {
      LOG.warn("Resource class " + resourceClass + " it is not root resource. "
          + "Path annotation javax.ws.rs.Path is not specified for this class.");
      return false;
    }

    for (ResourceFactory r : getRootResources()) {
      if (r.getUriPattern().equals(descriptor.getUriPattern())) {
        String msg = "Resource class " + descriptor.getResourceClass().getClass().getName()
            + " can't be registered. Resource class " + r.getClass().getName()
            + " with the same pattern " + r.getUriPattern() + " already registered.";
        throw new RuntimeException(msg);
      }
    }

    // per-request resource then process constructors and fields

    // process constructors
    for (Constructor<?> constructor : resourceClass.getConstructors()) {
      descriptor.getConstructorInjectors().add(new ConstructorInjectorImpl(resourceClass,
                                                                           constructor));
    }
    if (descriptor.getConstructorInjectors().size() == 0) {
      String msg = "Not found accepted constructors in resource class " + resourceClass.getName();
      throw new RuntimeException(msg);
    }
    // Sort constructors in number parameters order
    if (descriptor.getConstructorInjectors().size() > 1) {
      java.util.Collections.sort(descriptor.getConstructorInjectors(),
                                 ConstructorInjectorImpl.CONSTRUCTOR_COMPARATOR);
    }

    // process fields
    for (java.lang.reflect.Field jfield : resourceClass.getDeclaredFields()) {
      descriptor.getFieldInjectors().add(new FieldInjectorImpl(resourceClass, jfield));
    }

    // validate AbstractResourceDescriptor
    descriptor.accept(rdv);
    // validate each ResourceMethodDescriptor
    for (ResourceMethodDescriptor rmd : descriptor.getResourceMethodDescriptors()) {
      rmd.accept(rdv);
    }
    // validate each SubResourceMethodDescriptor
    for (SubResourceMethodDescriptor srmd : descriptor.getSubResourceMethodDescriptors()) {
      srmd.accept(rdv);
    }
    // validate each SubResourceLocatorDescriptor
    for (SubResourceLocatorDescriptor srld : descriptor.getSubResourceLocatorDescriptors()) {
      srld.accept(rdv);
    }
    // validate constructors
    for (ConstructorInjector ci : descriptor.getConstructorInjectors()) {
      ci.accept(rdv);
    }
    // validate fields descriptor
    for (FieldInjector fi : descriptor.getFieldInjectors()) {
      fi.accept(rdv);
    }
    
    ResourceFactory rc = new PerRequestResourceFactory(descriptor);
    getRootResources().add(rc);
    Collections.sort(getRootResources(), COMPARATOR);
    LOG.info("Bind new resource " + rc.getUriPattern().getRegex() + " : " + resourceClass);
    return true;

  }

  /**
   * Remove root resource of supplied class from root resource collection.
   * 
   * @param clazz root resource class
   */
  @SuppressWarnings("unchecked")
  public synchronized boolean unbind(Class clazz) {
    Iterator<ResourceFactory> i = getRootResources().iterator();

    while (i.hasNext()) {
      ResourceFactory rc = i.next();
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
  public List<ResourceFactory> getRootResources() {
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
  private void init(ExoContainer container, RuntimeDelegateImpl rd) {
    List<Application> al = container.getComponentInstancesOfType(Application.class);
    for (Application a : al) {
      for (Object obj : a.getSingletons()) {
        if (obj.getClass().getAnnotation(Provider.class) != null)
          rd.addProviderInstance(obj); // singleton provider
        else
          bind(obj); // singleton resource
      }
      for (Class<?> clazz : a.getClasses()) {
        if (clazz.getAnnotation(Provider.class) != null)
          rd.addProvider(clazz); // per-request provider
        else
          bind(clazz); // per-request resource
      }
    }

    List<ResourceContainer> rcl = container.getComponentInstancesOfType(ResourceContainer.class);
    for (ResourceContainer rc : rcl)
      bind(rc);
  }

}
