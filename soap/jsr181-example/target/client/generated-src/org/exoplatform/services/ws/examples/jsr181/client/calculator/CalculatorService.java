
package org.exoplatform.services.ws.examples.jsr181.client.calculator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "CalculatorService", targetNamespace = "http://exoplatform.org/soap/xfire")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CalculatorService {


    @WebMethod(operationName = "calculate", action = "urn:Calculate")
    @WebResult(name = "calculateResult", targetNamespace = "http://exoplatform.org/soap/xfire")
    public float calculate(
        @WebParam(name = "item1", targetNamespace = "http://exoplatform.org/soap/xfire", header = true)
        float item1,
        @WebParam(name = "item2", targetNamespace = "http://exoplatform.org/soap/xfire", header = true)
        float item2,
        @WebParam(name = "operation", targetNamespace = "http://exoplatform.org/soap/xfire", header = true)
        String operation);

}
