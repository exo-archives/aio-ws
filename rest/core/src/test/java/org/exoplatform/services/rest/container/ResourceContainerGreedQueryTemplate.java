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

package org.exoplatform.services.rest.container;

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.QueryParam;
import org.exoplatform.services.rest.QueryTemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerGreedQueryTemplate implements ResourceContainer {
  
  @HTTPMethod("GET")
  @URITemplate("/test/greed_query/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method0() {
    return Response.Builder.ok("method0", "text/plain").build();
  }

  @HTTPMethod("GET")
  @QueryTemplate("param1=param1")
  @URITemplate("/test/greed_query/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1() {
    return Response.Builder.ok("method1", "text/plain").build();
  }

  @HTTPMethod("GET")
  @QueryTemplate("param1=param1&param2=param2")
  @URITemplate("/test/greed_query/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method2() {
    return Response.Builder.ok("method2", "text/plain").build();
  }

  @HTTPMethod("GET")
  @QueryTemplate("param1=param1&param2=param2&param3=param3")
  @URITemplate("/test/greed_query/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method3() {
    return Response.Builder.ok("method3", "text/plain").build();
  }

  @HTTPMethod("GET")
  @QueryTemplate("param1=param1&param2=param2&param3=param3&param4=param4")
  @URITemplate("/test/greed_query/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method4() {
    return Response.Builder.ok("method4", "text/plain").build();
  }

  @HTTPMethod("GET")
  @QueryTemplate("param1=param1&param2=param2&param3=param3&param4=param4&param5=param5")
  @URITemplate("/test/greed_query/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method5() {
    return Response.Builder.ok("method5", "text/plain").build();
  }

}

