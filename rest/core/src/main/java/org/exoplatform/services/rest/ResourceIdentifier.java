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

import java.util.Map;

/**
 * Created by The eXo Platform SAS .<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class ResourceIdentifier {

  private Map<String, String> parameters = null;

  private String host = "localhost";

  private String uri;

  private String baseURI;

  public ResourceIdentifier(String host, String baseURI, String relURI) {
    this(baseURI, relURI);
    this.host = host;
  }

  /**
   * @param baseURI the base URI
   * @param relURI the relative URI
   */
  public ResourceIdentifier(String baseURI, String relURI) {
    this.uri = (relURI.endsWith("/")) ? relURI : (relURI + '/');
    this.baseURI = baseURI;
  }

  /**
   * @param relURI the relative URI Relative URI used for identification
   *            ResourceContainer with can serve the request
   */
  public ResourceIdentifier(String relURI) {
    this("", relURI);
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @return the relative URI
   */
  public String getURI() {
    return uri;
  }

  /**
   * @return the base URI
   */
  public String getBaseURI() {
    return baseURI;
  }

  /**
   * Initialize the URI parameters.
   * @param pattern the URIPattern
   * @see org.exoplatform.services.rest.URIPattern
   */
  public void initParameters(URIPattern pattern) {
    this.parameters = pattern.parse(uri);
  }

  /**
   * @return the key-value pairs of URi parameters.
   * @throws IllegalStateException URI parameters not initialized yet.
   */
  public Map<String, String> getParameters() throws IllegalStateException {
    if (parameters == null) {
      throw new IllegalStateException(
          "Parameters are not initialized. Call initParameters(pattern) first");
    }
    return parameters;
  }

}
