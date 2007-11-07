/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.container;

import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.QueryTemplate;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.QueryParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerQueryTemplate implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/qeuryfilter/")
  @QueryTemplate("method=method1&param1=param1")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1(@QueryParam("method")
  String method, @QueryParam("param1")
  String param1) {
    System.out.println(".. method=" + method);
    System.out.println(".. param1=" + param1);
    return Response.Builder.ok("method1", "text/plain").build();
  }

}
