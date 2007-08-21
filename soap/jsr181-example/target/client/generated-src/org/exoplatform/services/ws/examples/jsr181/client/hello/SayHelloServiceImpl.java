
package org.exoplatform.services.ws.examples.jsr181.client.hello;

import javax.jws.WebService;

@WebService(serviceName = "SayHelloService", targetNamespace = "http://exoplatform.org/soap/xfire", endpointInterface = "org.exoplatform.services.ws.examples.jsr181.client.hello.SayHelloService")
public class SayHelloServiceImpl
    implements SayHelloService
{


    public String sayHello(String sayHelloParam) {
        throw new UnsupportedOperationException();
    }

}
