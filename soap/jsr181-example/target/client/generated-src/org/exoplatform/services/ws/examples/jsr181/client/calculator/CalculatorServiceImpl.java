
package org.exoplatform.services.ws.examples.jsr181.client.calculator;

import javax.jws.WebService;

@WebService(serviceName = "CalculatorService", targetNamespace = "http://exoplatform.org/soap/xfire", endpointInterface = "org.exoplatform.services.ws.examples.jsr181.client.calculator.CalculatorService")
public class CalculatorServiceImpl
    implements CalculatorService
{


    public float calculate(float item1, float item2, String operation) {
        throw new UnsupportedOperationException();
    }

}
