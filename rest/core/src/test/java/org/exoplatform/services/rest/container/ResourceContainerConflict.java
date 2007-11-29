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
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.OutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerConflict implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/test1/{id1}/{id2}/test/test2/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1() {
    System.out.println(">>> method1");
    return Response.Builder.ok("method1").build();
  }

  @HTTPMethod("GET")
  @URITemplate("/test/test1/{id1}/test2/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method2() {
    System.out.println(">>> method2");
    return Response.Builder.ok("method2").build();
  }

  @HTTPMethod("GET")
  @URITemplate("/test1/{id1}/{id2}/{id3}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method3() {
    System.out.println(">>> method3");
    return Response.Builder.ok("method3").build();
  }

  @HTTPMethod("GET")
  @URITemplate("/{id}/{id1}/{id2}/{id3}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method4() {
    System.out.println(">>> method4");
    return Response.Builder.ok("method4").build();
  }
  
  @HTTPMethod("GET")
  @URITemplate("/test1/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method5() {
    System.out.println(">>> method5");
    return Response.Builder.ok("method5").build();
  }

  @HTTPMethod("GET")
  @URITemplate("/test/{id1}/{id2}/{id3}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method6() {
    System.out.println(">>> method6");
    return Response.Builder.ok("method6").build();
  }
  
  @HTTPMethod("GET")
  @URITemplate("/test/{id1}/{id2}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method7() {
    System.out.println(">>> method7");
    return Response.Builder.ok("method7").build();
  }

}
