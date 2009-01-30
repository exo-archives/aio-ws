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

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("/samples/security")
public class JSR250AnnotationExample implements ResourceContainer {

  private static final CacheControl CC = new CacheControl();
  
  static {
    CC.setNoCache(true);
  }

  @GET
  @Produces( { MediaType.TEXT_PLAIN })
  @PermitAll
  @Path("all")
  public Response get(@Context SecurityContext sc) {
    return Response.ok("This method is permitted for all authenticated users, caller principal "
        + sc.getUserPrincipal()).header(HttpHeaders.CACHE_CONTROL, CC).build();
  }

  @GET
  @Produces( { MediaType.TEXT_PLAIN })
  @RolesAllowed("exo")
  @Path("exo")
  public Response get2(@Context SecurityContext sc) {
    return Response.ok("This method is permitted only for users in \"exo\" role, caller principal "
        + sc.getUserPrincipal()).header(HttpHeaders.CACHE_CONTROL, CC).build();
  }

  @GET
  @Produces( { MediaType.TEXT_PLAIN })
  @RolesAllowed("admin")
  @Path("admin")
  public Response get3(@Context SecurityContext sc) {
    return Response.ok("This method is permitted only for users in \"admin\" role, caller principal "
        + sc.getUserPrincipal()).header(HttpHeaders.CACHE_CONTROL, CC).build();
  }

}
