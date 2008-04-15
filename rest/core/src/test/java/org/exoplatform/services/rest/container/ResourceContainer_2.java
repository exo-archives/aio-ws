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

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringInputTransformer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;

import java.io.IOException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@URITemplate("/level1/{id}/le vel3/")
public class ResourceContainer_2 implements ResourceContainer {

  @HTTPMethod("POST")
  @InputTransformer(StringInputTransformer.class)
  public Response postMethod(String str, @URIParam("id") String param) {
    Response resp = Response.Builder.created("http://localhost/level1/"
        + param + "/le vel3/" + str).build();
    return resp;
  }

  @HTTPMethod("PUT")
  @InputTransformer(StringInputTransformer.class)
  public Response putMethod(String str, @URIParam("id") String param) throws IOException {
    Response resp = Response.Builder.created("http://localhost/level1/"
        + param + "/le vel3/" + str).build();
    return resp;
  }

  @HTTPMethod("DELETE")
  @URITemplate("/delete resource/")
  public Response delMethod(@URIParam("myid") String param) {
    Response resp = Response.Builder.noContent().build();
    return resp;
  }

  @HTTPMethod("GET")
  @OutputTransformer(StringOutputTransformer.class)
  public Response getMethod(@URIParam("id") String param) throws IOException {
    Response resp = Response.Builder.ok("get resource", "text/plain").build();
    return resp;
  }
  
}
