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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.method.DefaultMethodInvoker;
import org.exoplatform.services.rest.impl.method.OptionsRequestMethodInvoker;
import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.impl.uri.UriTemplateParser;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class ResourceFactory {

  /**
   * Logger.
   */
  private static final Log                   LOG = ExoLogger.getLogger(ResourceFactory.class.getName());

  /**
   * See {@link AbstractResourceDescriptor}.
   */
  protected final AbstractResourceDescriptor resourceDescriptor;

  /**
   * See {@link ResourceMethodMap}.
   */
  protected final ResourceMethodMap          resourceMethodMap;

  /**
   * See {@link SubResourceMethodMap}.
   */
  protected final SubResourceMethodMap       subResourceMethodMap;

  /**
   * See {@link SubResourceLocatorMap}.
   */
  protected final SubResourceLocatorMap      subResourceLocatorMap;
  
  /**
   * Constructor.
   * 
   * @param resourceDescriptor See {@link AbstractResourceDescriptor}
   */
  public ResourceFactory(AbstractResourceDescriptor resourceDescriptor) {
    this.resourceDescriptor = resourceDescriptor;

    this.resourceMethodMap = new ResourceMethodMap();

    this.subResourceMethodMap = new SubResourceMethodMap();

    this.subResourceLocatorMap = new SubResourceLocatorMap();

    // Merge resource method to HTTP method name
    processResourceMethods(getResourceMethods());

    resolveHeadRequest(getResourceMethods());
    resolveOptionsRequest(getResourceMethods());
    getResourceMethods().sort();

    // Merge sub-resource method to URI pattern and HTTP method name
    processSubResourceMethods(getSubResourceMethods());

    resolveHeadRequest(getSubResourceMethods());
    for (ResourceMethodMap rmm : getSubResourceMethods().values())
      rmm.sort();

    // Merge resource locator to URI pattern
    processSubResourceLocators(getSubResourceLocators());
  }

  /**
   * @return get resource object
   */
  public abstract Object getResource(ApplicationContext context);
  
  //
  
  /**
   * @return resource class
   * @see AbstractResourceDescriptor#getResourceClass()
   */
  @SuppressWarnings("unchecked")
  public Class getResourceClass() {
    return resourceDescriptor.getResourceClass();
  }

  /**
   * @return value of {@link Path} annotation or null if resource is not root
   *         resource
   * @see AbstractResourceDescriptor#isRootResource()
   * @see AbstractResourceDescriptor#getPath()
   */
  public PathValue getPathValue() {
    return resourceDescriptor.getPath();
  }

  /**
   * @return {@link UriPattern}
   * @see UriPattern
   * @see UriTemplateParser
   */
  public UriPattern getUriPattern() {
    return resourceDescriptor.getUriPattern();
  }

  /**
   * @return See {@link ResourceMethodMap}
   * @see ResourceMethodDescriptor
   */
  public ResourceMethodMap getResourceMethods() {
    return resourceMethodMap;
  }

  /**
   * @return See {@link SubResourceMethodMap}
   * @see SubResourceMethodDescriptor
   */
  public SubResourceMethodMap getSubResourceMethods() {
    return subResourceMethodMap;
  }

  /**
   * @return See {@link SubResourceLocatorMap}
   * @see SubResourceLocatorDescriptor
   */
  public SubResourceLocatorMap getSubResourceLocators() {
    return subResourceLocatorMap;
  }

  //

  /**
   * According to JSR-311:
   * <p>
   * On receipt of a HEAD request an implementation MUST either: 1. Call method
   * annotated with request method designation for HEAD or, if none present, 2.
   * Call method annotated with a request method designation GET and discard any
   * returned entity.
   * </p>
   * 
   * @param resourceMethodMap ResourceMethodMap
   */
  protected void resolveHeadRequest(ResourceMethodMap resourceMethodMap) {

    List<ResourceMethodDescriptor> g = resourceMethodMap.get(HttpMethod.GET);
    if (g == null || g.size() == 0)
      return; // nothing to do, there is not 'GET' methods

    // If there is no methods for 'HEAD' anyway never return null.
    // Instead null empty List will be returned.
    List<ResourceMethodDescriptor> h = resourceMethodMap.getList(HttpMethod.HEAD);

    for (ResourceMethodDescriptor rmd : g) {
      if (!containsMediaType(h, rmd))
        h.add(new ResourceMethodDescriptorImpl(rmd.getMethod(),
                                               HttpMethod.HEAD,
                                               rmd.getMethodParameters(),
                                               rmd.getParentResource(),
                                               rmd.consumes(),
                                               rmd.produces(),
                                               rmd.getMethodInvoker()));
    }
  }

  /**
   * According to JSR-311:
   * <p>
   * On receipt of a OPTIONS request an implementation MUST either: 1. Call
   * method annotated with request method designation for OPTIONS or, if none
   * present, 2. Generate an automatic response using the metadata provided by
   * the JAX-RS annotations on the matching class and its methods.
   * </p>
   * 
   * @param resourceMethodMap ResourceMethodMap
   */
  protected void resolveOptionsRequest(ResourceMethodMap resourceMethodMap) {
    List<ResourceMethodDescriptor> o = resourceMethodMap.getList("OPTIONS");
    if (o.size() == 0) {
      List<MethodParameter> mps = Collections.emptyList();
      List<MediaType> consumes = MediaTypeHelper.DEFAULT_TYPE_LIST;
      List<MediaType> produces = new ArrayList<MediaType>(1);
      produces.add(MediaTypeHelper.WADL_TYPE);
      o.add(new OptionsRequestResourceMethodDescriptorImpl(null,
                                                           "OPTIONS",
                                                           mps,
                                                           resourceDescriptor,
                                                           consumes,
                                                           produces,
                                                           new OptionsRequestMethodInvoker()));
    }
  }

  /**
   * According to JSR-311:
   * <p>
   * On receipt of a HEAD request an implementation MUST either: 1. Call method
   * annotated with request method designation for HEAD or, if none present, 2.
   * Call method annotated with a request method designation GET and discard any
   * returned entity.
   * </p>
   * 
   * @param subResourceMethodMap SubResourceMethodMap
   */
  protected void resolveHeadRequest(SubResourceMethodMap subResourceMethodMap) {
    for (ResourceMethodMap rmm : subResourceMethodMap.values()) {

      List<ResourceMethodDescriptor> g = rmm.get(HttpMethod.GET);
      if (g == null || g.size() == 0)
        continue; // nothing to do, there is not 'GET' methods

      // If there is no methods for 'HEAD' anyway never return null.
      // Instead null empty List will be returned.
      List<ResourceMethodDescriptor> h = rmm.getList(HttpMethod.HEAD);

      Iterator<ResourceMethodDescriptor> i = g.iterator();
      while (i.hasNext()) {
        SubResourceMethodDescriptor srmd = (SubResourceMethodDescriptor) i.next();
        if (!containsMediaType(h, srmd)) {
          h.add(new SubResourceMethodDescriptorImpl(srmd.getPathValue(),
                                                    srmd.getMethod(),
                                                    HttpMethod.HEAD,
                                                    srmd.getMethodParameters(),
                                                    srmd.getParentResource(),
                                                    srmd.consumes(),
                                                    srmd.produces(),
                                                    new DefaultMethodInvoker()));
        }
      }
    }
  }

  //

  /**
   * Process resource methods.
   * 
   * @param rmm ResourceMethodMap
   * @see ResourceMethodDescriptor
   */
  protected void processResourceMethods(ResourceMethodMap rmm) {
    for (ResourceMethodDescriptor rmd : resourceDescriptor.getResourceMethodDescriptors()) {
      addResourceMethod(rmm, rmd);
    }
  }

  /**
   * Process sub-resource methods.
   * 
   * @param srmm SubResourceMethodMap
   * @see SubResourceMethodDescriptor
   */
  protected void processSubResourceMethods(SubResourceMethodMap srmm) {
    for (SubResourceMethodDescriptor srmd : resourceDescriptor.getSubResourceMethodDescriptors()) {
      ResourceMethodMap rmm = srmm.getMethodMap(srmd.getUriPattern());
      addResourceMethod(rmm, srmd);
    }
  }

  /**
   * Process sub-resource locators.
   * 
   * @param srlm SubResourceLocatorMap
   * @see SubResourceLocatorDescriptor
   */
  protected void processSubResourceLocators(SubResourceLocatorMap srlm) {
    for (SubResourceLocatorDescriptor srld : resourceDescriptor.getSubResourceLocatorDescriptors()) {
      if (!srlm.containsKey(srld.getUriPattern()))
        srlm.put(srld.getUriPattern(), srld);
      else
        LOG.warn("Sub-resource locator " + srld.getParentResource().getResourceClass() + "#"
            + srld.getMethod().getName() + " ignored because resource method with the"
            + " same path already exists");
    }
  }

  //

  /**
   * Check does {@link ResourceMethodMap} already contains the
   * {@link ResourceMethodDescriptor} with the same both (consumes and produces)
   * media types. If it does not then ResourceMethodDescriptor is added in map
   * otherwise warning message generated and ResourceMethodDescriptor ignored.
   * 
   * @param rmm See {@link ResourceMethodMap}
   * @param rmd See {@link ResourceMethodDescriptor}
   */
  protected void addResourceMethod(ResourceMethodMap rmm, ResourceMethodDescriptor rmd) {
    List<ResourceMethodDescriptor> r = rmm.getList(rmd.getHttpMethod());
    if (!containsMediaType(r, rmd))
      r.add(rmd);
    else
      LOG.warn("(Sub)resource method " + rmd.getParentResource().getResourceClass() + "#"
          + rmd.getMethod().getName() + " ignored because resource method with the"
          + " same consumes and produces type already exists");
  }

  /**
   * Check is collection of {@link ResourceMethodDescriptor} already contains
   * ResourceMethodDescriptor with the same media types.
   * 
   * @param rmds {@link Set} of {@link ResourceMethodDescriptor}
   * @param other ResourceMethodDescriptor which must be checked
   * @return true if Set already contains resource with the same media types
   *         false otherwise
   */
  protected boolean containsMediaType(List<ResourceMethodDescriptor> rmds,
                                      ResourceMethodDescriptor other) {
    for (ResourceMethodDescriptor rmd : rmds) {
      if (rmd.consumes().equals(other.consumes()) && rmd.produces().equals(other.produces()))
        return true;
    }
    return false;
  }
}
