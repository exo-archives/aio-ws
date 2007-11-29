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
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@URITemplate("/level1/level2/")
public class ResourceContainerAnnot implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/level3/{id1}/{id2}/")
  public Response method1(@URIParam("id1")
  String param) {
    System.out.println(">>> (annot. class) method1 called");
    System.out.println(">>> (annot. class) param = " + param);
    String entity = ">>> annotated container response!!!\n";
    StringOutputTransformer transformer = new StringOutputTransformer();
    Response resp = Response.Builder.ok(entity, "text/plain").transformer(
        transformer).build();
    return resp;
  }

}
