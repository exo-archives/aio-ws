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

package org.exoplatform.services.rest.impl;

/**
 * Should not be used by custom services. They have to use
 * {@link javax.ws.rs.WebApplicationException} instead. UnhandledException is
 * used to propagate exception than can't be handled by this framework to top
 * container (e.g. Servlet Container)
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UnhandledException extends RuntimeException {

  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = -1454662279257930428L;

  /**
   * @param s message
   * @param throwable cause
   */
  public UnhandledException(String s, Throwable throwable) {
    super(s, throwable);
  }

  /**
   * @param throwable cause
   */
  public UnhandledException(Throwable throwable) {
    super(throwable);
  }
}
