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
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@URITemplate("/level1/{id1}/")
public class ResourceContainer_3 implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/{id2}/")
  public Response method2(@URIParam("id1")
  String param1, @URIParam("id2")
  String param2) {
    System.out.println("=== method2 called: id1 = " + param1);
    System.out.println("=== method2 called: id2 = " + param2);
    Response resp = Response.Builder.noContent().build();
    return resp;
  }

  @HTTPMethod("GET")
  @URITemplate("/{id2}/{id3}/{id4}/{id5}/")
  public Response method5(@URIParam("id1")
  String param1, @URIParam("id2")
  String param2, @URIParam("id3")
  String param3, @URIParam("id4")
  String param4, @URIParam("id5")
  String param5) {
    System.out.println("=== method5 called: id1 = " + param1);
    System.out.println("=== method5 called: id2 = " + param2);
    System.out.println("=== method5 called: id3 = " + param3);
    System.out.println("=== method5 called: id4 = " + param4);
    System.out.println("=== method5 called: id5 = " + param5);
    Response resp = Response.Builder.noContent().build();
    return resp;
  }

  @HTTPMethod("GET")
  @URITemplate("/{id2}/{id3}/")
  public Response method3(@URIParam("id1")
  String param1, @URIParam("id2")
  String param2, @URIParam("id3")
  String param3) {
    System.out.println("=== method3 called: id1 = " + param1);
    System.out.println("=== method3 called: id2 = " + param2);
    System.out.println("=== method3 called: id3 = " + param3);
    Response resp = Response.Builder.noContent().build();
    return resp;
  }

  @HTTPMethod("GET")
  public Response method1(@URIParam("id1")
  String param1) {
    System.out.println("=== method1 called: id1 = " + param1);
    Response resp = Response.Builder.noContent().build();
    return resp;
  }

  @HTTPMethod("GET")
  @URITemplate("/{id2}/{id3}/{id4}/")
  public Response method4(@URIParam("id1")
  String param1, @URIParam("id2")
  String param2, @URIParam("id3")
  String param3, @URIParam("id4")
  String param4) {
    System.out.println("=== method4 called: id1 = " + param1);
    System.out.println("=== method4 called: id2 = " + param2);
    System.out.println("=== method4 called: id3 = " + param3);
    System.out.println("=== method4 called: id4 = " + param4);
    Response resp = Response.Builder.noContent().build();
    return resp;
  }
}
