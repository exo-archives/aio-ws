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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;

import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringInputTransformer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.services.rest.transformer.XMLOutputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.ProducedMimeTypes;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.HeaderParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerMimeTypes implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/level1/{id}/level3/")
  @ProducedMimeTypes("text/*")
  @InputTransformer(StringInputTransformer.class)
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1(String str, @URIParam("id")
  String param, @HeaderParam("tESt")
  String test) {
    System.out.println(">>> method1 called: id = " + param);
    System.out.println(">>> request entity - type: " +
        str.getClass().toString() + "; value: " + str);
    System.out.println(">>> header test: " + test);
    String e = ">>> this is response entity\n";
    Response resp = Response.Builder.ok(e, "text/plain").build();
    return resp;
  }

  @HTTPMethod("POST")
  @URITemplate("/level1/{id}/level3/")
  @ProducedMimeTypes("text/*")
  @InputTransformer(StringInputTransformer.class)
  @OutputTransformer(XMLOutputTransformer.class)
  public Response method2(String str, @URIParam("id")
  String param, @HeaderParam("tESt")
  String test) throws Exception {

    System.out.println("<<< method1 called: id = " + param);
    System.out.println("<<< request entity - type: " +
        str.getClass().toString() + "; value: " + str);
    System.out.println("<<< header test: " + test);
    Document document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().newDocument();

    Element element = document.createElement("test");
    document.appendChild(element);
    Response resp = Response.Builder.ok(document, "text/xml").build();
    return resp;
  }
}
