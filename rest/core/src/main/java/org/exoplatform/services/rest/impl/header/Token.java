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

/**
 * Token is any header part which contains only valid characters see
 * {@link HeaderHelper#isToken(String)} . Token is separated by ','
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Token {
  
  /**
   * Token.
   */
  private String token;

  /**
   * @param token a token
   */
  public Token(String token) {
    this.token = token.toLowerCase();
  }

  /**
   * @return the token in lower case
   */
  public String getToken() {
    return token;
  }

  /**
   * Check is to token is compatible.
   * 
   * @param other the token must be checked
   * @return true if token is compatible false otherwise
   */
  public boolean isCompatible(Token other) {
    if ("*".equals(token))
      return true;

    return token.equalsIgnoreCase(other.getToken());
  }

}
