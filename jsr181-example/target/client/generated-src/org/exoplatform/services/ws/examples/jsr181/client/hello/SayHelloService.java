
package org.exoplatform.services.ws.examples.jsr181.client.hello;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "SayHelloService", targetNamespace = "http://exoplatform.org/soap/xfire")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface SayHelloService {


    @WebMethod(operationName = "sayHello", action = "urn:SayHello")
    @WebResult(name = "sayHelloResult", targetNamespace = "http://exoplatform.org/soap/xfire")
    public String sayHello(
        @WebParam(name = "sayHelloParam", targetNamespace = "http://exoplatform.org/soap/xfire", header = true)
        String sayHelloParam);

}
