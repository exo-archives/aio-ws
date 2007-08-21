
package org.exoplatform.services.ws.examples.jsr181.client.hello;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.jaxb2.JaxbTypeRegistry;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.transport.TransportManager;

public class SayHelloServiceClient {

    private static XFireProxyFactory proxyFactory = new XFireProxyFactory();
    private HashMap endpoints = new HashMap();
    private Service service0;

    public SayHelloServiceClient() {
        create0();
        Endpoint SayHelloServiceHttpPortEP = service0 .addEndpoint(new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceHttpPort"), new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceHttpBinding"), "http://localhost/services/SayHelloService");
        endpoints.put(new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceHttpPort"), SayHelloServiceHttpPortEP);
        Endpoint SayHelloServiceLocalEndpointEP = service0 .addEndpoint(new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceLocalEndpoint"), new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceLocalBinding"), "xfire.local://SayHelloService");
        endpoints.put(new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceLocalEndpoint"), SayHelloServiceLocalEndpointEP);
    }

    public Object getEndpoint(Endpoint endpoint) {
        try {
            return proxyFactory.create((endpoint).getBinding(), (endpoint).getUrl());
        } catch (MalformedURLException e) {
            throw new XFireRuntimeException("Invalid URL", e);
        }
    }

    public Object getEndpoint(QName name) {
        Endpoint endpoint = ((Endpoint) endpoints.get((name)));
        if ((endpoint) == null) {
            throw new IllegalStateException("No such endpoint!");
        }
        return getEndpoint((endpoint));
    }

    public Collection getEndpoints() {
        return endpoints.values();
    }

    private void create0() {
        TransportManager tm = (org.codehaus.xfire.XFireFactory.newInstance().getXFire().getTransportManager());
        HashMap props = new HashMap();
        props.put("annotations.allow.interface", true);
        AnnotationServiceFactory asf = new AnnotationServiceFactory(new Jsr181WebAnnotations(), tm, new AegisBindingProvider(new JaxbTypeRegistry()));
        asf.setBindingCreationEnabled(false);
        service0 = asf.create((org.exoplatform.services.ws.examples.jsr181.client.hello.SayHelloService.class), props);
        {
            AbstractSoapBinding soapBinding = asf.createSoap11Binding(service0, new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceLocalBinding"), "urn:xfire:transport:local");
        }
        {
            AbstractSoapBinding soapBinding = asf.createSoap11Binding(service0, new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceHttpBinding"), "http://schemas.xmlsoap.org/soap/http");
        }
    }

    public SayHelloService getSayHelloServiceHttpPort() {
        return ((SayHelloService)(this).getEndpoint(new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceHttpPort")));
    }

    public SayHelloService getSayHelloServiceHttpPort(String url) {
        SayHelloService var = getSayHelloServiceHttpPort();
        org.codehaus.xfire.client.Client.getInstance(var).setUrl(url);
        return var;
    }

    public SayHelloService getSayHelloServiceLocalEndpoint() {
        return ((SayHelloService)(this).getEndpoint(new QName("http://exoplatform.org/soap/xfire", "SayHelloServiceLocalEndpoint")));
    }

    public SayHelloService getSayHelloServiceLocalEndpoint(String url) {
        SayHelloService var = getSayHelloServiceLocalEndpoint();
        org.codehaus.xfire.client.Client.getInstance(var).setUrl(url);
        return var;
    }

}
