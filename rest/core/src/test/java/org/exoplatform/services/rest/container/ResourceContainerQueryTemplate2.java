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
public class ResourceContainerQueryTemplate2 implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/qeuryfilter/")
  @QueryTemplate("method=method3&param3=param3&param2=param2&param1=param1")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method3(@QueryParam("method")
  String method, @QueryParam("param1")
  String param1, @QueryParam("param2")
  String param2, @QueryParam("param3")
  String param3, @QueryParam("param4")
  String param4) {
    System.out.println(".... method=" + method);
    System.out.println(".... param1=" + param1);
    System.out.println(".... param2=" + param2);
    System.out.println(".... param3=" + param3);
    System.out.println(".... param4=" + param4);
    return Response.Builder.ok("method3", "text/plain").build();
  }

  @HTTPMethod("GET")
  @URITemplate("/test/qeuryfilter/")
  @QueryTemplate("method=method2&param1=param1&param2=param2")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method2(@QueryParam("method")
  String method, @QueryParam("param1")
  String param1, @QueryParam("param2")
  String param2) {
    System.out.println("... method=" + method);
    System.out.println("... param1=" + param1);
    System.out.println("... param2=" + param2);
    return Response.Builder.ok("method2", "text/plain").build();
  }

}
