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

package org.exoplatform.services.rest.data;

import org.w3c.dom.Element;

/**
 * Created by The eXo Platform SAS.<br/> Can add xlink:href into given Element
 * of DOM structure.<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class XlinkHref {

  private static final String XLINK_HREF = "xlinks:href";
  private static final String XLINK_NAMESPACE_URL = "http://www.w3c.org/1999/xlink";

  private String uri;

  /**
   * @param u new uri
   */
  public XlinkHref(String u) {
    this.uri = u;
  }

  /**
   * Get uri.
   * @return uri current uri
   */
  public final String getURI() {
    return uri;
  }

  /**
   * Add xlink to given element of DOM structure.
   * @param parent element
   */
  public void putToElement(Element parent) {
    parent.setAttributeNS(XLINK_NAMESPACE_URL, XLINK_HREF, uri);
  }

  /**
   * Add external suffix to uri an d then insert xlink into element of DOM.
   * @param parent element
   * @param extURI external suffix for uri
   */
  public void putToElement(Element parent, String extURI) {
    parent.setAttributeNS(XLINK_NAMESPACE_URL, XLINK_HREF, uri + extURI);
  }

}
