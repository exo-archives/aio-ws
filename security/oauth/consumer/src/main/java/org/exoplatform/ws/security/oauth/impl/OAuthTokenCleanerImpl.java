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

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.OAuthTokenCleaner;

import net.oauth.OAuthAccessor;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OAuthTokenCleanerImpl extends Thread implements OAuthTokenCleaner {
  
  private final long timeout;
  
  private Set<OAuthAccessor> tokens;
  
  private final static Log log = ExoLogger.getLogger("ws.security.OAuthTokenCleanerImpl");
  
  /**
   * Default cleaner timeout. By default 5 minutes.
   */
  public static final long DEFAULT_TIMEOUT = 5 * 60 * 1000;

  public OAuthTokenCleanerImpl(InitParams params) {
    ValueParam t = params.getValueParam("tokenCleanerTimeout");
    timeout = t != null ? Long.parseLong(t.getValue()) * 60 * 1000 : DEFAULT_TIMEOUT;
    log.info("Token Cleaner timeout is " + timeout + " ms.");
    this.start();
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    while (true) {
      try {
        clean();
        sleep(timeout);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.ws.security.oauth.TokenCleaner#clean()
   */
  public void clean() {
    long currentTime = System.currentTimeMillis();
    
    // If Collections which must be under control is not passed yet.
    if (tokens == null)
      return;
    
    Iterator<OAuthAccessor> iter = tokens.iterator();
    while (iter.hasNext()) {
      OAuthAccessor a = iter.next();
      Object o = null;
      if ((o = a.getProperty("expired")) != null) {
        if ((Long) o < currentTime) {
          if (log.isDebugEnabled()) {
            log.debug("Remove expired accessor: " + a);
          }
          iter.remove();
        }
      }
    }
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ws.security.oauth.OAuthTokenCleaner#setTokens(java.util.Set)
   */
  public void setTokens(Set<OAuthAccessor> tokens) {
    this.tokens = tokens;
  }

}

