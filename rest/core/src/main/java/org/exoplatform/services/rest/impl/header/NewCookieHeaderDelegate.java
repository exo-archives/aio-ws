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

import javax.ws.rs.core.NewCookie;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class NewCookieHeaderDelegate extends AbstractHeaderDelegate<NewCookie> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<NewCookie> support() {
    return NewCookie.class;
  }

  /**
   * {@inheritDoc}
   */
  public NewCookie fromString(String header) {
    throw new UnsupportedOperationException("NewCookie used only for response headers.");
  }

  /**
   * {@inheritDoc}
   */
  public String toString(NewCookie cookie) {
    StringBuffer sb = new StringBuffer();
    sb.append(cookie.getName()).append('=').append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getValue()));

    sb.append(';').append("Version=").append(cookie.getVersion());

    if (cookie.getComment() != null)
      sb.append(';').append("Comment=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getComment()));

    if (cookie.getDomain() != null)
      sb.append(';').append("Domain=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getDomain()));

    if (cookie.getPath() != null)
      sb.append(';').append("Path=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getPath()));

    if (cookie.getMaxAge() != -1)
      sb.append(';').append("Max-Age=").append(HeaderHelper.addQuotesIfHasWhitespace("" + cookie.getMaxAge()));

    if (cookie.isSecure())
      sb.append(';').append("Secure");

    return sb.toString();
  }

}
