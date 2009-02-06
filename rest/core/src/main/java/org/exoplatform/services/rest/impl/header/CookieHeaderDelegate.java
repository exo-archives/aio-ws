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

package org.exoplatform.services.rest.impl.header;

import java.util.List;

import javax.ws.rs.core.Cookie;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CookieHeaderDelegate extends AbstractHeaderDelegate<Cookie> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<Cookie> support() {
    return Cookie.class;
  }

  /**
   * {@inheritDoc}
   */
  public Cookie fromString(String header) {
    if (header == null)
      throw new IllegalArgumentException();
    
    List<Cookie> l = HeaderHelper.parseCookies(header);
    if (l.size() > 0) // waiting for one cookie
      return l.get(0);

    return null;
  }

  /**
   * {@inheritDoc}
   */
  public String toString(Cookie cookie) {
    StringBuilder sb = new StringBuilder();

    sb.append("$Version=").append(cookie.getVersion()).append(';');

    sb.append(cookie.getName()).append('=').append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getValue()));

    if (cookie.getDomain() != null)
      sb.append(';').append("$Domain=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getDomain()));

    if (cookie.getPath() != null)
      sb.append(';').append("$Path=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getPath()));

    return sb.toString();
  }

}
