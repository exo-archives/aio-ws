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

package org.exoplatform.services.ws.rest.simple;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringInputTransformer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

@URITemplate("/ejb/simple-service/")
public class SimpleService implements ResourceContainer {

  private static final Log LOGGER = ExoLogger.getLogger(SimpleService.class);

  private SimpleStorage storage_;

  public SimpleService(SimpleStorage storage) {
    storage_ = storage;
  }
  

  private SimpleStorage getStorage() throws Exception {
    return storage_;
  }

  @HTTPMethod("GET")
  @URITemplate("/{id}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response get(@URIParam("id")
  String id) {
    LOGGER.info(">>> [GET] " + id);
    try {
      String data = getStorage().get(Integer.valueOf(id));
      if (data != null) {
        return Response.Builder.ok(data).build();
      }
      return Response.Builder.notFound().build();
    } catch (Exception e) {
      LOGGER.error("Can't get object, object id " + id);
      e.printStackTrace();
      return Response.Builder.serverError().build();
    }
  }

  @HTTPMethod("POST")
  @URITemplate("/{id}/")
  @InputTransformer(StringInputTransformer.class)
  public Response post(String data, @URIParam("id")
  String id) throws Exception {
    LOGGER.info(">>> [POST] " + id);
    try {
      getStorage().set(Integer.valueOf(id).intValue(), data);
      return Response.Builder.noContent().build();
    } catch (Exception e) {
      LOGGER.error("Can't change object, object id " + id);
      e.printStackTrace();
      return Response.Builder.serverError().build();
    }
  }

  @HTTPMethod("PUT")
  @InputTransformer(StringInputTransformer.class)
  @OutputTransformer(StringOutputTransformer.class)
  public Response put(String data) throws Exception {
    LOGGER.info(">>> [PUT], data: " + data);
    try {
      SimpleStorage ms = getStorage();
      ms.add(data);
      return Response.Builder.ok("Add new object with id " + (ms.size() - 1)).build();
    } catch (Exception e) {
      LOGGER.error("Can't add new object.");
      e.printStackTrace();
      return Response.Builder.serverError().build();
    }
  }

  @HTTPMethod("DELETE")
  @URITemplate("/{id}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response delete(@URIParam("id")
  String id) {
    LOGGER.info(">>> [DELETE]" + id);
    try {
      int index = Integer.valueOf(id).intValue();
      return Response.Builder.ok(
          "Remove object: " + getStorage().remove(index)).build();
    } catch (Exception e) {
      LOGGER.error("Can't remove object, object id " + id);
      e.printStackTrace();
      return Response.Builder.serverError().build();
    }
  }

  @HTTPMethod("DELETE")
  @OutputTransformer(StringOutputTransformer.class)
  public Response clear() {
    LOGGER.info(">>> [CLEAN STORAGE]");
    try {
      getStorage().clear();
      return Response.Builder.ok("Clean storage.").build();
    } catch (Exception e) {
      LOGGER.error("Can't clean storage!");
      e.printStackTrace();
      return Response.Builder.serverError().build();
    }
  }
}
