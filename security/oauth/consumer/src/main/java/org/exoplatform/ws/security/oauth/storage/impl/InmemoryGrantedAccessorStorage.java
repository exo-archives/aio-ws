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

package org.exoplatform.ws.security.oauth.storage.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthProblemException;

import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorage;
import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorageProperties;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InmemoryGrantedAccessorStorage extends GrantedAccessorStorage {

  private final Map<String, OAuthAccessor> storage;

  public InmemoryGrantedAccessorStorage() {
    storage = new ConcurrentHashMap<String, OAuthAccessor>();
  }

  /**
   * {@inheritDoc}
   */
  public void addAccessor(OAuthAccessor accessor) {
    storage.put(accessor.accessToken, accessor);
  }

  /**
   * {@inheritDoc}
   */
  public OAuthAccessor getAccessor(String token, String secret) throws OAuthProblemException {
    OAuthAccessor accessor = storage.get(token);
    if (accessor != null) {
      checkSecretToken(accessor, secret);
      return accessor;
    }
    
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void init(GrantedAccessorStorageProperties properties) {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   */
  public void removeAccessor(String token) {
    storage.remove(token);
  }

}
