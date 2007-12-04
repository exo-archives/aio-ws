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

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.transformer.XSLT4DOMOutputTransformer;
import org.exoplatform.services.rest.transformer.XSLT4SourceOutputTransformer;
import org.exoplatform.services.rest.transformer.XSLTConstants;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class XSLTResourceContainer implements ResourceContainer {
  
  @HTTPMethod("GET")
  @URITemplate("/test/xslt/{schema-name}/")
  @OutputTransformer(XSLT4DOMOutputTransformer.class)
  public Response method1(@URIParam("schema-name") String schemaName) throws Exception {
    Map<String, String> p = new HashMap<String, String>();
    p.put(XSLTConstants.XSLT_TEMPLATE, schemaName);
    Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("book-in.xml"));
    return Response.Builder.ok(d, "text/html").setTransformerParameters(p).build();
  }

  @HTTPMethod("GET")
  @URITemplate("/test/xslt2/{schema-name}/")
  @OutputTransformer(XSLT4SourceOutputTransformer.class)
  public Response method2(@URIParam("schema-name") String schemaName) throws Exception {
    Map<String, String> p = new HashMap<String, String>();
    p.put(XSLTConstants.XSLT_TEMPLATE, schemaName);
    StreamSource s = new StreamSource(Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("book-in.xml"));
    return Response.Builder.ok(s, "text/html").setTransformerParameters(p).build();
  }

}
