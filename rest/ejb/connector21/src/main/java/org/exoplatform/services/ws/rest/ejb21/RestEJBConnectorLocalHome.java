/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.ws.rest.ejb21;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RestEJBConnectorLocalHome extends EJBLocalHome {
  
  public RestEJBConnectorLocal create() throws CreateException;

}
