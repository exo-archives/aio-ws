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

package org.exoplatform.services.security.sso;

import java.security.Principal;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SSOAuthenticator {
  
  /**
   * @param token the token from client.
   * @throws Exception if any error occurs.
   */
  void doAuthenticate(byte[] token) throws Exception;
  
  /**
   * @return the name of user if authentication is success otherwise null.
   */
  String getUser();
  
  /**
   * @return the user Principal.
   */
  Principal getPrincipal();
  
  /**
   * @return Token for sending back to client, can be null if server has nothing to say. 
   */
  byte[] getSendBackToken();
  
  /**
   * Says is authentication complete.
   * @return true is authentication completed (with error or success), false otherwise.
   */
  boolean isComplete();
  
  /**
   * Says is authentication successful.
   * @return true if authentication (or step of it) successful false otherwise.
   */
  boolean isSuccess();

}

