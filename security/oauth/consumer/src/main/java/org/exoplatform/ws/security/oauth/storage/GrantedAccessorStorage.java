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

package org.exoplatform.ws.security.oauth.storage;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthProblemException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class GrantedAccessorStorage {

  /**
   * Add supplied accessor in storage. NOTE does not check accessor is unique,
   * if the other one with same access key already presents in storage it will
   * be overridden.
   * 
   * @param accessor accessor
   * @throws OAuthProblemException if accessor can't be added in any reason
   */
  public abstract void addAccessor(OAuthAccessor accessor) throws OAuthProblemException;

  /**
   * Try obtain accessor from storage. Accessor searched by <tt>token</tt>
   * parameter, if accessor found then <tt>secret</tt> parameter will be checked
   * to much with <tt>accessor.tokenSecret</tt>
   * 
   * @param token access token
   * @param secret secret token
   * @return accessor if it is found null otherwise
   * @throws OAuthProblemException if any error occurs or if <tt>secret</tt>
   *           does not match to <tt>accessor.tokenSecret</tt>
   */
  public abstract OAuthAccessor getAccessor(String token, String secret) throws OAuthProblemException;

  /**
   * Initialize storage, should be called only one time.
   * 
   * @param properties See {@link GrantedAccessorStorageProperties}
   * @throws OAuthProblemException if any error occurs or some required
   *           properties is not presents or invalid
   */
  public abstract void init(GrantedAccessorStorageProperties properties) throws OAuthProblemException;

  /**
   * Remove accessor with supplied access key from storage.
   * 
   * @param token access token
   * @throws OAuthProblemException if any errors occurs
   */
  public abstract void removeAccessor(String token) throws OAuthProblemException;

  /**
   * Check is accessor has valid secret token.
   * 
   * @param accessor accessor
   * @param secret secret token
   * @throws OAuthProblemException if secret token is not match to
   *           accessor.tokenSecret
   */
  protected void checkSecretToken(OAuthAccessor accessor, String secret) throws OAuthProblemException {
    // If secret token is not specified in accessor nothing to do
    if (accessor.tokenSecret == null)
      return;

    if (secret == null)
      throw new OAuthProblemException("Secret token is null");
    if (!secret.equals(accessor.tokenSecret))
      throw new OAuthProblemException("Secret tokens are not matched!");
  }

}
