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
import java.util.List;

import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.method.ConstructorDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AbstractResourceDescriptorImpl implements AbstractResourceDescriptor {

  /**
   * @see {@link PathValue}.
   */
  private final PathValue                          path;

  /**
   * See {@link UriPattern}.
   */
  private final UriPattern                         uriPattern;

  /**
   * Resource class.
   */
  private final Class<?>                           resourceClazz;

  /**
   * Sub-resource methods. Sub-resource method has path annotation.
   * 
   * @see {@link SubResourceMethodDescriptor}
   */
  private final List<SubResourceMethodDescriptor>  subResourceMethods;

  /**
   * Sub-resource locators. Sub-resource locator has path annotation.
   * 
   * @see {@link SubResourceLocatorDescriptor}
   */
  private final List<SubResourceLocatorDescriptor> subResourceLocators;

  /**
   * Resource methods. Resource method has not own path annotation.
   * 
   * @see {@link ResourceMethodDescriptor}
   */
  private final List<ResourceMethodDescriptor>     resourceMethods;

  /**
   * Resource class constructors.
   * 
   * @see {@link ConstructorDescriptor}
   */
  private final List<ConstructorDescriptor>        constructorDescriptors;

  /**
   * Constructs new instance of AbstractResourceDescriptor with path (root
   * resource).
   * 
   * @param path the path value.
   * @param resourceClazz resource class
   */
  public AbstractResourceDescriptorImpl(PathValue path, Class<?> resourceClazz) {
    this.path = path;
    
    if (path != null) 
      uriPattern = new UriPattern(path.getPath());
    else
      uriPattern = null;
    
    this.resourceClazz = resourceClazz;

    this.constructorDescriptors = new ArrayList<ConstructorDescriptor>();

    this.subResourceMethods = new ArrayList<SubResourceMethodDescriptor>();

    this.subResourceLocators = new ArrayList<SubResourceLocatorDescriptor>();

    this.resourceMethods = new ArrayList<ResourceMethodDescriptor>();
    
  }

  /**
   * Constructs new instance of AbstractResourceDescriptor without path
   * (sub-resource).
   * 
   * @param resourceClazz resource class
   */
  public AbstractResourceDescriptorImpl(Class<?> resourceClazz) {
    this(null, resourceClazz);
  }

  /**
   * {@inheritDoc}
   */
  public void accept(ResourceDescriptorVisitor visitor) {
    visitor.visitAbstractResourceDescriptor(this);
  }

  /**
   * {@inheritDoc}
   */
  public PathValue getPath() {
    return path;
  }
  
  /**
   * {@inheritDoc}
   */
  public UriPattern getUriPattern() {
    return uriPattern;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isRootResource() {
    return path != null;
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getResourceClass() {
    return resourceClazz;
  }

  /**
   * {@inheritDoc}
   */
  public List<ConstructorDescriptor> getConstructorDescriptor() {
    return constructorDescriptors;
  }

  /**
   * {@inheritDoc}
   */
  public List<ResourceMethodDescriptor> getResourceMethodDescriptors() {
    return resourceMethods;
  }

  /**
   * {@inheritDoc}
   */
  public List<SubResourceLocatorDescriptor> getSubResourceLocatorDescriptors() {
    return subResourceLocators;
  }

  /**
   * {@inheritDoc}
   */
  public List<SubResourceMethodDescriptor> getSubResourceMethodDescriptors() {
    return subResourceMethods;
  }

}
