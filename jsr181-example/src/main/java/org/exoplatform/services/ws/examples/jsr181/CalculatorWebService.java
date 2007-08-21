/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.ws.examples.jsr181;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.ws.AbstractWebService;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */

@WebService(name = "CalculatorService",
    serviceName = "CalculatorService",
    targetNamespace = "http://exoplatform.org/soap/xfire")
public class CalculatorWebService implements AbstractWebService {

  public CalculatorWebService(ExoContainerContext context) {
    System.out.println(">>> ExoContainerContext: " + context);
    System.out.println(">>> CalculatorService: " + this);
  }
  
  @WebMethod(operationName = "calculate", action = "urn:Calculate")
  @WebResult(name = "calculateResult")
  public float calculate(@WebParam(name = "item1", header = true)
  float a, @WebParam(name = "item2", header = true)
  float b, @WebParam(name = "operation", header = true)
  char o)  throws Exception {

    switch (o) {
      case '+':
        return (a + b);
      case '-':
        return a - b;
      case '*':
        return a * b;
      case '/':
        return a / b;
    }
//    throw new XFireFault("Unknown operation: " + o, XFireFault.SENDER);
    throw new Exception("Unknown operation: " + o);
  }

}
