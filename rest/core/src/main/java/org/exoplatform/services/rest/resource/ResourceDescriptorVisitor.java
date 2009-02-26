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

package org.exoplatform.services.rest.resource;

/**
 * Can be used for validation next resource descriptors
 * {@link ConstructorDescriptor}, {@link AbstractResourceDescriptor},
 * {@link ResourceMethodDescriptor}, {@link SubResourceMethodDescriptor},
 * {@link SubResourceLocatorDescriptor} .
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ResourceDescriptorVisitor {

  /**
   * @param ard See {@link AbstractResourceDescriptor}
   * @throws IllegalArgumentException if AbstractResourceDescriptor has not
   *           valid structure
   */
  void visitAbstractResourceDescriptor(AbstractResourceDescriptor ard) throws IllegalArgumentException;

  /**
   * @param rmd See {@link ResourceMethodDescriptor}
   * @throws IllegalArgumentException if ResourceMethodDescriptor has not valid
   *           structure
   */
  void visitResourceMethodDescriptor(ResourceMethodDescriptor rmd) throws IllegalArgumentException;

  /**
   * @param srmd See {@link SubResourceMethodDescriptor}
   * @throws IllegalArgumentException if SubResourceMethodDescriptor has not
   *           valid structure
   */
  void visitSubResourceMethodDescriptor(SubResourceMethodDescriptor srmd) throws IllegalArgumentException;

  /**
   * @param srld See {@link SubResourceLocatorDescriptor}
   * @throws IllegalArgumentException if SubResourceLocatorDescriptor has not
   *           valid structure
   */
  void visitSubResourceLocatorDescriptor(SubResourceLocatorDescriptor srld) throws IllegalArgumentException;

}
