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

package org.exoplatform.services.ws.rest.samples;

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringInputTransformer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * This service does not do real work, it useful to check rest-ejb connector.
 */
@URITemplate("/simple-service/")
public class SimpleService implements ResourceContainer {

  /**
   * Keeps data.
   */
  private String data;

  /**
   * Get actual data.
   * @return response with actual data as body or response with status 404 if data is null. 
   */
  @HTTPMethod("GET")
  @OutputTransformer(StringOutputTransformer.class)
  public Response get() {
    if (data != null) {
      return Response.Builder.ok(data).build();
    }
    return Response.Builder.notFound().build();
  }

  /**
   * Set data.
   * @param data the data.
   * @return response with status 201.
   */
  @HTTPMethod("POST")
  @InputTransformer(StringInputTransformer.class)
  public Response post(String data) {
    this.data = data;
    return Response.Builder.created("/simple-service/").build();
  }

  /**
   * Set data.
   * @param data the data.
   * @return response with status 201.
   */
  @HTTPMethod("PUT")
  @InputTransformer(StringInputTransformer.class)
  public Response put(String data) {
    this.data = data;
    return Response.Builder.created("/simple-service/").build();
  }

  /**
   * Delete data.
   * @return response with status 204.
   */
  @HTTPMethod("DELETE")
  public Response delete() {
    this.data = null;
    return Response.Builder.noContent().build();
  }

}
