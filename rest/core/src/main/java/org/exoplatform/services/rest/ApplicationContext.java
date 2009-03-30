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

package org.exoplatform.services.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.exoplatform.services.rest.impl.ProviderBinder;

/**
 * Provides access to ContainerRequest, ContainerResponse and request URI
 * information.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ApplicationContext extends UriInfo {

  /**
   * Should be used to pass template values in context by using returned list in
   * matching to @see
   * {@link org.exoplatform.services.rest.uri.UriPattern#match(String, List)}
   * . List will be cleared during matching.
   * 
   * @return the list for template values
   */
  List<String> getParameterValues();

  /**
   * Pass in context list of path template parameters @see {@link UriPattern}.
   * 
   * @param parameterNames list of templates parameters
   */
  void setParameterNames(List<String> parameterNames);

  /**
   * Add ancestor resource, according to JSR-311:
   * <p>
   * Entries are ordered according in reverse request URI matching order, with
   * the root resource last.
   * </p>
   * So add each new resource at the begin of list.
   * 
   * @param resource the resource e. g. resource class, sub-resource method or
   *          sub-resource locator.
   */
  void addMatchedResource(Object resource);

  /**
   * Add ancestor resource, according to JSR-311:
   * <p>
   * Entries are ordered in reverse request URI matching order, with the root
   * resource URI last.
   * </p>
   * So add each new URI at the begin of list.
   * 
   * @param uri the partial part of that matched to resource class, sub-resource
   *          method or sub-resource locator.
   */
  void addMatchedURI(String uri);

  /**
   * @return get mutable runtime attributes
   */
  Map<String, Object> getAttributes();

  /**
   * @return See {@link Request}
   */
  Request getRequest();

  /**
   * @return See {@link HttpHeaders}
   */
  HttpHeaders getHttpHeaders();

  /**
   * @return See {@link SecurityContext}
   */
  SecurityContext getSecurityContext();

  /**
   * @return See {@link GenericContainerRequest}
   */
  GenericContainerRequest getContainerRequest();

  /**
   * @return See {@link UriInfo}
   */
  UriInfo getUriInfo();

  /**
   * @return See {@link GenericContainerResponse}
   */
  GenericContainerResponse getContainerResponse();
  
  /**
   * @return {@link ProviderBinder}
   * @see Providers
   */
  ProviderBinder getProviders();

}
