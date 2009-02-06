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

package org.exoplatform.services.rest.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.uri.UriComponent;

/**
 * Provides access to ContainerRequest, ContainerResponse and request URI
 * information.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationContext implements UriInfo {

  /**
   * {@link ThreadLocal} ApplicationContext.
   */
  private static ThreadLocal<ApplicationContext> current = new ThreadLocal<ApplicationContext>();

  /**
   * Set ApplicationContext for current thread.
   * 
   * @param context the ApplicationContext.
   */
  public static void setCurrent(ApplicationContext context) {
    current.set(context);
  }

  /**
   * @return current ApplicationContext.
   */
  public static ApplicationContext getCurrent() {
    return current.get();
  }

  /**
   * Values of template parameters.
   */
  private List<String>        parameterValues    = new ArrayList<String>();

  /**
   * List of matched resources.
   */
  private List<Object>        matchedResources   = new ArrayList<Object>();

  /**
   * List of not decoded matched URIs.
   */
  private List<String>        encodedMatchedURIs = new ArrayList<String>();

  /**
   * List of decoded matched URIs.
   */
  private List<String>        matchedURIs        = new ArrayList<String>();

  /**
   * See {@link GenericContainerRequest}.
   */
  protected GenericContainerRequest  request;

  /**
   * See {@link ContainerResponse}.
   */
  protected GenericContainerResponse response;

  /**
   * See {@link RequestHandler}.
   */
  protected RequestHandler    requestHandler;

  /**
   * Mutable runtime attributes.
   */
  private Map<String, Object> attributes;

  /**
   * Constructs new instance of ApplicationContext.
   * 
   * @param requestHandler See {@link RequestHandler}
   * @param request See {@link GenricContainerRequest}
   * @param response See {@link GenericContainerResponse}
   */
  public ApplicationContext(RequestHandler requestHandler,
                            GenericContainerRequest request,
                            GenericContainerResponse response) {
    this.requestHandler = requestHandler;
    this.request = request;
    this.response = response;
  }

  /**
   * Should be used to pass template values in context by using returned list in
   * matching to @see
   * {@link org.exoplatform.services.rest.impl.uri.UriPattern#match(String, List)}
   * . List will be cleared during matching.
   * 
   * @return the list for template values
   */
  public List<String> getParameterValues() {
    return parameterValues;
  }

  /**
   * Pass in context list of path template parameters @see {@link UriPattern}.
   * 
   * @param parameterNames list of templates parameters
   */
  public void setParameterNames(List<String> parameterNames) {
    if (encodedPathParameters == null)
      encodedPathParameters = new MultivaluedMapImpl();

    for (int i = 0; i < parameterNames.size(); i++)
      encodedPathParameters.add(parameterNames.get(i), parameterValues.get(i));

  }

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
  public void addMatchedResource(Object resource) {
    matchedResources.add(0, resource);
  }

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
  public void addMatchedURI(String uri) {
    encodedMatchedURIs.add(0, uri);
    matchedURIs.add(0, UriComponent.decode(uri, UriComponent.PATH_SEGMENT));
  }

  /**
   * @return get mutable runtime attributes
   */
  public Map<String, Object> getAttributes() {
    return attributes == null ? attributes = new HashMap<String, Object>() : attributes;
  }

  /**
   * @return See {@link Request}
   */
  public Request getRequest() {
    return request;
  }

  /**
   * @return See {@link HttpHeaders}
   */
  public HttpHeaders getHttpHeaders() {
    return request;
  }

  /**
   * @return See {@link SecurityContext}
   */
  public SecurityContext getSecurityContext() {
    return request;
  }

  /**
   * @return See {@link GenericContainerRequest}
   */
  public GenericContainerRequest getContainerRequest() {
    return request;
  }

  /**
   * @return See {@link RequestHandler}
   */
  public RequestHandler getRequestHandler() {
    return requestHandler;
  }

  /**
   * @return See {@link UriInfo}
   */
  public UriInfo getUriInfo() {
    return this;
  }

  /**
   * @return See {@link GenericContainerResponse}
   */
  public GenericContainerResponse getContainerResponse() {
    return response;
  }

  // UriInfo

  /**
   * Absolute path, full requested URI without query string and fragment.
   */
  private URI                            absolutePath;

  /**
   * Decoded relative path.
   */
  private String                         path;

  /**
   * Not decoded relative path.
   */
  private String                         encodedPath;

  /**
   * Not decoded path template parameters.
   */
  private MultivaluedMap<String, String> encodedPathParameters;

  /**
   * Decoded path template parameters.
   */
  private MultivaluedMap<String, String> pathParameters;

  /**
   * List of not decoded path segments.
   */
  private List<PathSegment>              encodedPathSegments;

  /**
   * Decoded path segments.
   */
  private List<PathSegment>              pathSegments;

  /**
   * Not decoded query parameters.
   */
  private MultivaluedMap<String, String> encodedQueryParameters;

  /**
   * Decoded query parameters.
   */
  private MultivaluedMap<String, String> queryParameters;

  /**
   * {@inheritDoc}
   */
  public URI getAbsolutePath() {
    if (absolutePath != null)
      return absolutePath;

    return absolutePath = getRequestUriBuilder().replaceQuery(null).fragment(null).build();
  }

  /**
   * {@inheritDoc}
   */
  public UriBuilder getAbsolutePathBuilder() {
    return UriBuilder.fromUri(getAbsolutePath());
  }

  /**
   * {@inheritDoc}
   */
  public List<Object> getMatchedResources() {
    return matchedResources;
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getMatchedURIs() {
    return getMatchedURIs(true);
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getMatchedURIs(boolean decode) {
    return decode ? matchedURIs : encodedMatchedURIs;
  }

  /**
   * {@inheritDoc}
   */
  public URI getBaseUri() {
    return request.getBaseUri();
  }

  /**
   * {@inheritDoc}
   */
  public UriBuilder getBaseUriBuilder() {
    return UriBuilder.fromUri(getBaseUri());
  }

  /**
   * {@inheritDoc}
   */
  public String getConnegExtension() {
    // This method and preprocessing process at all, except normalization URI is
    // not presents in newer releases of JSR-311.
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  public String getPath() {
    return getPath(true);
  }

  /**
   * {@inheritDoc}
   */
  public String getPath(boolean decode) {
    if (encodedPath == null)
      encodedPath = getAbsolutePath().getRawPath().substring(getBaseUri().getRawPath().length());

    if (decode) {
      if (path != null)
        return path;

      return path = UriComponent.decode(encodedPath, UriComponent.PATH);

    }

    return encodedPath;
  }

  /**
   * {@inheritDoc}
   */
  public MultivaluedMap<String, String> getPathParameters() {
    return getPathParameters(true);
  }

  /**
   * {@inheritDoc}
   */
  public MultivaluedMap<String, String> getPathParameters(boolean decode) {
    if (encodedPathParameters == null)
      throw new IllegalStateException("Path template variables not initialized yet.");

    if (decode) {
      if (pathParameters != null)
        return pathParameters;

      pathParameters = new MultivaluedMapImpl();
      for (String key : encodedPathParameters.keySet()) {
        pathParameters.putSingle(UriComponent.decode(key, UriComponent.PATH_SEGMENT),
                                 UriComponent.decode(encodedPathParameters.getFirst(key),
                                                     UriComponent.PATH));
      }
      
      return pathParameters;

    }

    return encodedPathParameters;
  }

  /**
   * {@inheritDoc}
   */
  public List<PathSegment> getPathSegments() {
    return getPathSegments(true);
  }

  /**
   * {@inheritDoc}
   */
  public List<PathSegment> getPathSegments(boolean decode) {
    if (decode) {
      return pathSegments != null ? pathSegments
                                 : (pathSegments = UriComponent.parsePathSegments(getPath(), true));
    }
    return encodedPathSegments != null ? encodedPathSegments
                                      : (encodedPathSegments = UriComponent.parsePathSegments(getPath(),
                                                                                              false));
  }

  /**
   * {@inheritDoc}
   */
  public MultivaluedMap<String, String> getQueryParameters() {
    return getQueryParameters(true);
  }

  /**
   * {@inheritDoc}
   */
  public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
    if (decode)
      return queryParameters != null ? queryParameters
                                    : (queryParameters = UriComponent.parseQueryString(getRequestUri().getRawQuery(),
                                                                                       true));
    return encodedQueryParameters != null ? encodedQueryParameters
                                         : (encodedQueryParameters = UriComponent.parseQueryString(getRequestUri().getRawQuery(),
                                                                                                   false));
  }

  /**
   * {@inheritDoc}
   */
  public URI getRequestUri() {
    return request.getRequestUri();
  }

  /**
   * {@inheritDoc}
   */
  public UriBuilder getRequestUriBuilder() {
    return UriBuilder.fromUri(getRequestUri());
  }

}
