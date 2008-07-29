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
import org.exoplatform.services.rest.ContextParam;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerContextParameter implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/context/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1(@ContextParam("test")
  String test, @ContextParam(ResourceDispatcher.CONTEXT_PARAM_REL_URI)
  String relURI, @ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI)
  String baseURI, @ContextParam(ResourceDispatcher.CONTEXT_PARAM_HOST)
  String host) {
    StringBuffer sb = new StringBuffer();
    sb.append("host=").append(host)
      .append(", baseURI=").append(baseURI)
      .append(", relURI=").append(relURI)
      .append(", test=").append(test);
    return Response.Builder.ok(sb.toString(), "text/plain").build();
  }

}
