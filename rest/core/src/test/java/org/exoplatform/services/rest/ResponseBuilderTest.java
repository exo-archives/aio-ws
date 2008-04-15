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

import junit.framework.TestCase;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResponseBuilderTest extends TestCase {

  StringOutputTransformer transformer = new StringOutputTransformer();

  public void testError() {
    Response response = Response.Builder.serverError().build();
    assertEquals(HTTPStatus.INTERNAL_ERROR, response.getStatus());

    response = Response.Builder.notFound().build();
    assertEquals(HTTPStatus.NOT_FOUND, response.getStatus());

    response = Response.Builder.forbidden().build();
    assertEquals(HTTPStatus.FORBIDDEN, response.getStatus());
  }

  public void testOk() throws Exception {
    Response response = Response.Builder.ok().build();
    assertEquals(HTTPStatus.OK, response.getStatus());

    String entity = "oktest";
    response = Response.Builder.ok(entity).transformer(transformer).build();
    assertEquals(HTTPStatus.OK, response.getStatus());
    assertEquals("oktest", response.getEntity());
    assertNotNull(response.getTransformer());

    response = Response.Builder.ok(entity, "text/plain").build();
    assertEquals("text/plain", response.getEntityMetadata().getMediaType());
  }

  public void testCreated() throws Exception {
    String location = "http://localhost/test";

    Response response = Response.Builder.created(location).build();
    assertEquals(location, response.getResponseHeaders().getFirst("Location"));

    response = Response.Builder.created(location, location).transformer(
        transformer).build();
    assertNotNull(response.getTransformer());
    assertEquals(location, response.getResponseHeaders().getFirst("Location"));
    assertEquals(location, response.getEntity());
  }

  public void testCustom() throws Exception {
    int st = 306; // this is for test builder with custom status
    Response response = Response.Builder.newInstance().status(st).build();
    assertEquals(st, response.getStatus());
    String entity = "customtest";
    response = Response.Builder.withStatus(st).entity(entity, "text/plain")
        .transformer(transformer).build();

    assertNotNull(response.getTransformer());
    assertEquals(st, response.getStatus());
    assertEquals("customtest", response.getEntity());
    assertEquals("text/plain", response.getEntityMetadata().getMediaType());
  }

}
