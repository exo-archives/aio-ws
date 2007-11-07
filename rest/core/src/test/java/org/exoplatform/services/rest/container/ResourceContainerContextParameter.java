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
import org.exoplatform.services.rest.ContextParam;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerContextParameter implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/context/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1(@ContextParam("test")
  String test, @ContextParam(ResourceDispatcher.CONTEXT_PARAM_REL_URI)
  String relURI, @ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI)
  String baseURI, @ContextParam(ResourceDispatcher.CONTEXT_PARAM_HOST)
  String host) {
    System.out.println(">>> " + host);
    System.out.println(">>> " + baseURI);
    System.out.println(">>> " + relURI);
    System.out.println(">>> " + test);
    return Response.Builder.ok(baseURI, "text/plain").build();
  }

}
