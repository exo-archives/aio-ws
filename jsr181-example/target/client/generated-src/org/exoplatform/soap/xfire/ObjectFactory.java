
package org.exoplatform.soap.xfire;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.exoplatform.soap.xfire package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SayHelloParam_QNAME = new QName("http://exoplatform.org/soap/xfire", "sayHelloParam");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.exoplatform.soap.xfire
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SayHelloResponse }
     * 
     */
    public SayHelloResponse createSayHelloResponse() {
        return new SayHelloResponse();
    }

    /**
     * Create an instance of {@link SayHello }
     * 
     */
    public SayHello createSayHello() {
        return new SayHello();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://exoplatform.org/soap/xfire", name = "sayHelloParam")
    public JAXBElement<String> createSayHelloParam(String value) {
        return new JAXBElement<String>(_SayHelloParam_QNAME, String.class, null, value);
    }

}
