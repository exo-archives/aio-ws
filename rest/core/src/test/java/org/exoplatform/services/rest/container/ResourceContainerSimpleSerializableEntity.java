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
import org.exoplatform.services.rest.transformer.DeserializableTransformer;
import org.exoplatform.services.rest.transformer.SerializableTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.InputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerSimpleSerializableEntity implements
    ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/serializable/")
  @InputTransformer(DeserializableTransformer.class)
  @OutputTransformer(SerializableTransformer.class)
  public Response method1(SimpleDeserializableEntity de) {
    StringBuffer sb = new StringBuffer();
    sb.append(de.data);
    SimpleSerializableEntity se = new SimpleSerializableEntity();
    se.data = sb.reverse().toString();
    return Response.Builder.ok(se).build();
  }
}
