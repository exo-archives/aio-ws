/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.rest.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by The eXo Platform SARL Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua 21 Січ 2008
 */
public class RestEncodingFilter implements Filter {

  private String requestEncodings;

  public void destroy() {

  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
      throws IOException, ServletException {
    req.setCharacterEncoding(requestEncodings);

    filterChain.doFilter(req, resp);
    
  }

  public void init(FilterConfig config) throws ServletException {
    requestEncodings = config.getInitParameter("REQUEST_ENCODING");
  }

}
