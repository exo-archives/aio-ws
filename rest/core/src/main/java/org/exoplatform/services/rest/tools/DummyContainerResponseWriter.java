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

package org.exoplatform.services.rest.tools;

import java.io.IOException;

import javax.ws.rs.ext.MessageBodyWriter;

import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.GenericContainerResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DummyContainerResponseWriter implements ContainerResponseWriter {

  public DummyContainerResponseWriter() {
  }

  @SuppressWarnings("unchecked")
  public void writeBody(GenericContainerResponse response, MessageBodyWriter entityWriter) throws IOException {
  }

  public void writeHeaders(GenericContainerResponse response) throws IOException {
  }

}
