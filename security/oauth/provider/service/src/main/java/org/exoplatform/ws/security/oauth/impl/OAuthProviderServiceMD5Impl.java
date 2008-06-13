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

package org.exoplatform.ws.security.oauth.impl;

import java.util.UUID;


import org.apache.commons.codec.digest.DigestUtils;
import org.exoplatform.container.xml.InitParams;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OAuthProviderServiceMD5Impl extends OAuthProviderServiceImpl {
  
  public OAuthProviderServiceMD5Impl(InitParams params) {
    super(params);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ws.security.oauth.impl.OAuthProviderServiceImpl#generateSecret()
   */
  @Override
  protected String generateSecret() {
    return DigestUtils.md5Hex(UUID.randomUUID().toString());
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ws.security.oauth.impl.OAuthProviderServiceImpl#generateToken()
   */
  @Override
  protected String generateToken() {
    return DigestUtils.md5Hex(UUID.randomUUID().toString());
  }
  
}

