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

package org.exoplatform.services.rest.container;

import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.QueryTemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.QueryParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerQueryTemplate implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/qeuryfilter/")
  @QueryTemplate("method=method1&param1=param1")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1(@QueryParam("method")
  String method, @QueryParam("param1")
  String param1) {
    System.out.println(".. method=" + method);
    System.out.println(".. param1=" + param1);
    return Response.Builder.ok("method1", "text/plain").build();
  }

}
