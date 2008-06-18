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

package org.exoplatform.services.security.cas.client.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.cas.client.AbstractHTTPUsernamePasswordValidator;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class BaseHTTPUsernamePasswordValidatorImpl
    extends AbstractHTTPUsernamePasswordValidator {

  private static final long serialVersionUID = 1218562187483098721L;

  /* (non-Javadoc)
   * @see org.exoplatform.services.security.cas3.AbstractHTTPAuthenticator#sendSuccessMessage(javax.servlet.http.HttpServletResponse, java.lang.String)
   */
  @Override
  protected void sendSuccessMessage(HttpServletResponse httpResponse,
      String principal) throws IOException {
    httpResponse.setStatus(HttpServletResponse.SC_OK);
    httpResponse.setContentType("text/plain");
    PrintWriter out = httpResponse.getWriter();
    out.print("yes\n" + principal + "\n");
    out.flush();
    out.close();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.security.cas3.AbstractHTTPAuthenticator#sendFailMessage(javax.servlet.http.HttpServletResponse, java.lang.String)
   */
  @Override
  protected void sendFailMessage(HttpServletResponse httpResponse,
      String principal) throws IOException {
    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    PrintWriter out = httpResponse.getWriter();
    out.print("no\n" + principal + "\n");
    out.flush();
    out.close();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.security.cas.client.AbstractHTTPUsernamePasswordValidator#getContainer()
   */
  @Override
  protected ExoContainer getContainer() {
    return ExoContainerContext.getCurrentContainer();
  }
  
}
