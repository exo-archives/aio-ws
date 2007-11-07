/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.data;

import org.exoplatform.services.rest.MultivaluedMetadata;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class QueryUtils {

  public static MultivaluedMetadata parseQueryString(String queryString) {
    String[] p = queryString.split("&");
    MultivaluedMetadata queryParams = new MultivaluedMetadata();
    for (String s : p) {
      String[] t = s.split("=");
      queryParams.putSingle(t[0], t[1]);
    }
    return queryParams;
  }

}
