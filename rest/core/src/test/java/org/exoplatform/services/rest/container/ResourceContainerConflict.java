/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.container;

import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.OutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerConflict implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/test/test1/{id1}/{id2}/test/test2")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method1(@URIParam("id1")
  String id1) {
    return Response.Builder.ok(id1).build();
  }

  @HTTPMethod("GET")
  @URITemplate("/test1/{id1}/{id2}/test2/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method2(@URIParam("id1")
  String id1) {
    return Response.Builder.ok(id1).build();
  }

}
