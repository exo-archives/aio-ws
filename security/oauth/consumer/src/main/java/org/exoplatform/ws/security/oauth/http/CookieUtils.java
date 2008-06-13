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

package org.exoplatform.ws.security.oauth.http;

import javax.servlet.http.Cookie;

/**
 * Common operations with cookie.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class CookieUtils {
  
  /**
   * For removing old cookie, set max age for it 0.
   * @param name the cookie name.
   * @return cookie.
   */
  static final Cookie deleteCookie(Cookie c) {
    c.setMaxAge(0);
    c.setValue("");
    return c;
  }
  
  /**
   * Searching cookie by name. 
   * @param name the cookie name.
   * @param cookies the cookie array.
   * @return cookie or null if no one found.
   */
  static final Cookie findCookie(String name, Cookie[] cookies) {
    if (cookies == null || cookies.length == 0)
      return null;
    for (Cookie c : cookies) {
      if (c != null && name.equalsIgnoreCase(c.getName()))
        return c;
    }
    return null;
  }

}

