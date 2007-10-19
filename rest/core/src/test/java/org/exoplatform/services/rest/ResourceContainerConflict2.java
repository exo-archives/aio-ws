/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest;

import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceContainerConflict2 implements ResourceContainer {

  @HTTPMethod("GET")
  @URITemplate("/{}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response method3(@URIParam("id1") String id1) {
    return null;
  }

}

