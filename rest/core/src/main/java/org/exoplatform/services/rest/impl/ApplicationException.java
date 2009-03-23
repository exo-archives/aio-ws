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
 * {@link javax.ws.rs.WebApplicationException} instead. ApplicationException is
 * used as wrapper for exception that may occur during request processing.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationException extends RuntimeException {

  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = -712006975338590407L;

  /**
   * @param s message
   * @param throwable cause
   */
  public ApplicationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  /**
   * @param throwable cause
   */
  public ApplicationException(Throwable throwable) {
    super(throwable);
  }
}
