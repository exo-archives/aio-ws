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
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.ws.framework.json.transformer.Bean2JsonOutputTransformer;
import org.exoplatform.ws.framework.json.transformer.Json2BeanInputTransformer;

/**
 * Created by The eXo Platform SARL Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */

public class JSON2BeanConvertor implements ResourceContainer {

  @URITemplate("/json/test/")
  @HTTPMethod("GET")
  @InputTransformer(Json2BeanInputTransformer.class)
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response method(Book book) {
    return Response.Builder.ok(book).build();
  }

}
