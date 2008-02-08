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

package org.exoplatform.services.ws.rest.samples;

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.QueryParam;
import org.exoplatform.services.rest.QueryTemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Calculator implements ResourceContainer {

  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/calculator/{1}/{2}/")
  @QueryTemplate("operation=add")
  @OutputTransformer(StringOutputTransformer.class)                                                                                                                                                         
  public Response add(@URIParam("1") Integer firstItem,
      @URIParam("2") Integer secondItem,
      @QueryParam("operation") String op) {
    return Response.Builder.ok(firstItem + secondItem + "").mediaType("text/plain").build();  
  }
  
  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/calculator/{1}/{2}/")
  @QueryTemplate("operation=subtract")
  @OutputTransformer(StringOutputTransformer.class)                                                                                                                                                         
  public Response subtract(@URIParam("1") Integer firstItem,
      @URIParam("2") Integer secondItem,
      @QueryParam("operation") String op) {
    return Response.Builder.ok(firstItem - secondItem + "").mediaType("text/plain").build();  
  }

  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/calculator/{1}/{2}/")
  @QueryTemplate("operation=multiply")
  @OutputTransformer(StringOutputTransformer.class)                                                                                                                                                         
  public Response multiply(@URIParam("1") Integer firstItem,
      @URIParam("2") Integer secondItem,
      @QueryParam("operation") String op) {
    return Response.Builder.ok(firstItem * secondItem + "").mediaType("text/plain").build();  
  }

  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/calculator/{1}/{2}/")
  @QueryTemplate("operation=divide")
  @OutputTransformer(StringOutputTransformer.class)                                                                                                                                                         
  public Response divide(@URIParam("1") Integer firstItem,
      @URIParam("2") Integer secondItem,
      @QueryParam("operation") String op) {
    return Response.Builder.ok(firstItem / secondItem + "").mediaType("text/plain").build();  
  }
}
