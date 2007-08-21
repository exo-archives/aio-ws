/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.ws.examples.jsr181;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.ws.AbstractWebService;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebResult;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */

@WebService(name="SayHelloService",
    serviceName="SayHelloService",
    targetNamespace="http://exoplatform.org/soap/xfire")
public class SayHelloWebService implements AbstractWebService {
  
  public SayHelloWebService(ExoContainerContext context) {
    System.out.println(">>> ExoContainerContext: " + context);
    System.out.println(">>> SayHelloService: " + this);
  }

  @WebMethod(operationName="sayHello", action="urn:SayHello")
  @WebResult(name="sayHelloResult")
  public String hello(@WebParam(name="sayHelloParam", header=true) String name) {
    return "Hello " + name;
  }
}
