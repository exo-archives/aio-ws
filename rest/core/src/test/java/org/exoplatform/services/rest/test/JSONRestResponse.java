/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.rest.test;

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.Bean2JsonOutputTransformer;

/**
 * Created by The eXo Platform SARL Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */

public class JSONRestResponse implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/json/{tail}/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response method(@URIParam("tail")String tail) {
    return Response.Builder.ok(new PojoTest()).build();
  }

  public class PojoTest {
    private long   id   = 007;

    private String name = "James Bond";

    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

}
