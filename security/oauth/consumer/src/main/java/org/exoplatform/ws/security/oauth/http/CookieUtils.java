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

import org.exoplatform.ws.security.oauth.OAuthConsumerService;

import net.oauth.OAuthConsumer;

/**
 * Common operations with cookie.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class CookieUtils {

  /**
   * 1 years in seconds.
   */
  private static final int COOKIE_MAX_AGE = 31556926;

  /**
   * For removing old cookie, set max age for it 0.
   * 
   * @param c cookie which must be deleted
   * @return cookie cookie with 'maxAge = 0'
   */
  static Cookie deleteCookie(Cookie c) {
    c.setMaxAge(0);
    c.setValue("");
    return c;
  }

  /**
   * Searching cookie by name.
   * 
   * @param name the cookie name
   * @param cookies the cookie array
   * @return cookie or null if no one found
   */
  static Cookie findCookie(String name, Cookie[] cookies) {
    if (cookies == null || cookies.length == 0)
      return null;
    for (Cookie c : cookies) {
      if (c != null && name.equalsIgnoreCase(c.getName()))
        return c;
    }
    return null;
  }

  /**
   * Create a new cookie.
   * 
   * @param name cookie name
   * @param value cookie value
   * @return newly created cookie
   */
  static Cookie createCookie(String name, String value, OAuthConsumer consumer) {
    Cookie c = new Cookie(name, value);
    int maxAge = consumer.getProperty(OAuthConsumerService.REMMEMBER_MAX_AGE_PROPERTY) == null ? COOKIE_MAX_AGE
                                                                                              : Integer.parseInt((String) consumer.getProperty(OAuthConsumerService.REMMEMBER_MAX_AGE_PROPERTY));
    c.setMaxAge(maxAge);
    return c;
  }

}
