/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.container;

import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerConflict2 implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/{id1}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method3(@URIParam("id1") String id1) {
    return Response.Builder.ok(id1).build();
  }

}
