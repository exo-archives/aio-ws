
package org.exoplatform.services.ws.examples.jsr181.client.calculator;

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

public class CalculatorServiceClient {

    private static XFireProxyFactory proxyFactory = new XFireProxyFactory();
    private HashMap endpoints = new HashMap();
    private Service service0;

    public CalculatorServiceClient() {
        create0();
        Endpoint CalculatorServiceLocalEndpointEP = service0 .addEndpoint(new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceLocalEndpoint"), new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceLocalBinding"), "xfire.local://CalculatorService");
        endpoints.put(new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceLocalEndpoint"), CalculatorServiceLocalEndpointEP);
        Endpoint CalculatorServiceHttpPortEP = service0 .addEndpoint(new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceHttpPort"), new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceHttpBinding"), "http://localhost/services/CalculatorService");
        endpoints.put(new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceHttpPort"), CalculatorServiceHttpPortEP);
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
        service0 = asf.create((org.exoplatform.services.ws.examples.jsr181.client.calculator.CalculatorService.class), props);
        {
            AbstractSoapBinding soapBinding = asf.createSoap11Binding(service0, new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceLocalBinding"), "urn:xfire:transport:local");
        }
        {
            AbstractSoapBinding soapBinding = asf.createSoap11Binding(service0, new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceHttpBinding"), "http://schemas.xmlsoap.org/soap/http");
        }
    }

    public CalculatorService getCalculatorServiceLocalEndpoint() {
        return ((CalculatorService)(this).getEndpoint(new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceLocalEndpoint")));
    }

    public CalculatorService getCalculatorServiceLocalEndpoint(String url) {
        CalculatorService var = getCalculatorServiceLocalEndpoint();
        org.codehaus.xfire.client.Client.getInstance(var).setUrl(url);
        return var;
    }

    public CalculatorService getCalculatorServiceHttpPort() {
        return ((CalculatorService)(this).getEndpoint(new QName("http://exoplatform.org/soap/xfire", "CalculatorServiceHttpPort")));
    }

    public CalculatorService getCalculatorServiceHttpPort(String url) {
        CalculatorService var = getCalculatorServiceHttpPort();
        org.codehaus.xfire.client.Client.getInstance(var).setUrl(url);
        return var;
    }

}
