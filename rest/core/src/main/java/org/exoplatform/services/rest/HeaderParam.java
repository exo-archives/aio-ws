/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

package org.exoplatform.services.rest;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by The eXo Platform SAS.<br/> HeaderParam define the names of header
 * from HTTP request. In this way ResourceContainer gets only header parameters
 * wich it needs. For example:<br/>
 * 
 * <pre>
 * ...
 * public getMethod(@HeaderParam(&quot;accept&quot;) String accept_param) {
 * ...
 * }
 * </pre>
 * 
 * <br/> Method getMethod gets header parameter "accept" as String accept_param.<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
@Target(value = { PARAMETER })
@Retention(RUNTIME)
public @interface HeaderParam {
  String value();
}
