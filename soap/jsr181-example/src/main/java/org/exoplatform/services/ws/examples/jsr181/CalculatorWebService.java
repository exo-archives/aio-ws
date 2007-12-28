/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

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
