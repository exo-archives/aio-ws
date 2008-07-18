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

package org.exoplatform.services.rest;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.<br/> Request represents REST request (not
 * HTTP request).<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class Request {

  /**
   * HTTP method name, method name corresponding to client request.
   */
  private String methodName;
  
  /**
   * ResourceIdentifier (host, URI, etc). 
   */
  private ResourceIdentifier resourceIdentifier;
  
  /**
   * HTTP headers. 
   */
  private MultivaluedMetadata headerParams;
  
  /**
   * Query parameters.
   */
  private MultivaluedMetadata queryParams;
  
  /**
   * Data stream.
   */
  private InputStream entityDataStream;
  
  /**
   * Cookies.
   */
  private Map<String, org.exoplatform.services.rest.Cookie> cookies;

  /**
   * Constructor with cookie support.
   * @param entityDataStream input data stream from HTTP request (HTTP methods
   *            POST, PUT).
   * @param resourceIdentifier @see org.exoplatform.services.rest.ResourceIdentifier.
   * @param methodName the HTTP method (GET, POST, DELETE, etc).
   * @param httpHeaderParams the HTTP headers.
   * @param httpQueryParams the query parameters.
   * @param cookies the cookies.
   */
  public Request(InputStream entityDataStream,
      ResourceIdentifier resourceIdentifier, String methodName,
      MultivaluedMetadata httpHeaderParams, MultivaluedMetadata httpQueryParams,
      Map<String, org.exoplatform.services.rest.Cookie> cookies) {
    
    this.methodName = methodName;
    this.resourceIdentifier = resourceIdentifier;
    this.entityDataStream = entityDataStream;
    this.queryParams = httpQueryParams;
    this.headerParams = httpHeaderParams;
    this.cookies = cookies;
  }

  /**
   * @param entityDataStream input data stream from HTTP request (HTTP methods
   *            POST, PUT).
   * @param resourceIdentifier @see org.exoplatform.services.rest.ResourceIdentifier.
   * @param methodName the HTTP method (GET, POST, DELETE, etc).
   * @param httpHeaderParams the HTTP headers.
   * @param httpQueryParams the query parameters.
   */
  public Request(InputStream entityDataStream,
      ResourceIdentifier resourceIdentifier, String methodName,
      MultivaluedMetadata httpHeaderParams, MultivaluedMetadata httpQueryParams) {
    
    this.methodName = methodName;
    this.resourceIdentifier = resourceIdentifier;
    this.entityDataStream = entityDataStream;
    this.queryParams = httpQueryParams;
    this.headerParams = httpHeaderParams;
  }

  /**
   * Return entity body represented by InputStream.
   * @return the entity data stream.
   */
  public InputStream getEntityStream() {
    return entityDataStream;
  }

  /**
   * get ResourceIdentifier.
   * @see org.exoplatform.services.rest.ResourceIdentifier.
   * @return the ResourceIdentifier.
   */
  public ResourceIdentifier getResourceIdentifier() {
    return resourceIdentifier;
  }

  /**
   * set ResourceIdentifier.
   * @see org.exoplatform.services.rest.ResourceIdentifier.
   * @param resourceIdentifier the ResourceIdentifier.
   */
  public void setResourceIdentifier(ResourceIdentifier resourceIdentifier) {
    this.resourceIdentifier = resourceIdentifier;
  }

  /**
   * HTTP method name.
   * @return the HTTP method name.
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * Return a map of key-values pair of header parameters.
   * @see org.exoplatform.services.rest.MultivaluedMetadata.
   * @return the all key-values pair of headers
   */
  public MultivaluedMetadata getHeaderParams() {
    return headerParams;
  }

  /**
   * Return a map of key-values pair of query parameters.
   * @see org.exoplatform.services.rest.MultivaluedMetadata.
   * @return the all key-values pair of query parameters.
   */
  public MultivaluedMetadata getQueryParams() {
    return queryParams;
  }
  
  /**
   * @param name the cookie name.
   * @return the map of cookies, key is cookie name. 
   */
  public org.exoplatform.services.rest.Cookie getCookie(String name) {
    if (cookies == null)
      return null;
    return cookies.get(name.toLowerCase());
  }

}
