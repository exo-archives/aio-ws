
package org.exoplatform.services.ws.soap.jsr181;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.exoplatform.services.ws.soap.jsr181 package. 
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

    private final static QName _GetTicket_QNAME = new QName("http://jsr181.soap.ws.services.exoplatform.org/", "getTicket");
    private final static QName _GetTicketResponse_QNAME = new QName("http://jsr181.soap.ws.services.exoplatform.org/", "getTicketResponse");
    private final static QName _ConfirmationResponse_QNAME = new QName("http://jsr181.soap.ws.services.exoplatform.org/", "confirmationResponse");
    private final static QName _Confirmation_QNAME = new QName("http://jsr181.soap.ws.services.exoplatform.org/", "confirmation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.exoplatform.services.ws.soap.jsr181
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTicket }
     * 
     */
    public GetTicket createGetTicket() {
        return new GetTicket();
    }

    /**
     * Create an instance of {@link Confirmation }
     * 
     */
    public Confirmation createConfirmation() {
        return new Confirmation();
    }

    /**
     * Create an instance of {@link GetTicketResponse }
     * 
     */
    public GetTicketResponse createGetTicketResponse() {
        return new GetTicketResponse();
    }

    /**
     * Create an instance of {@link ConfirmationResponse }
     * 
     */
    public ConfirmationResponse createConfirmationResponse() {
        return new ConfirmationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTicket }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jsr181.soap.ws.services.exoplatform.org/", name = "getTicket")
    public JAXBElement<GetTicket> createGetTicket(GetTicket value) {
        return new JAXBElement<GetTicket>(_GetTicket_QNAME, GetTicket.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTicketResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jsr181.soap.ws.services.exoplatform.org/", name = "getTicketResponse")
    public JAXBElement<GetTicketResponse> createGetTicketResponse(GetTicketResponse value) {
        return new JAXBElement<GetTicketResponse>(_GetTicketResponse_QNAME, GetTicketResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfirmationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jsr181.soap.ws.services.exoplatform.org/", name = "confirmationResponse")
    public JAXBElement<ConfirmationResponse> createConfirmationResponse(ConfirmationResponse value) {
        return new JAXBElement<ConfirmationResponse>(_ConfirmationResponse_QNAME, ConfirmationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Confirmation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jsr181.soap.ws.services.exoplatform.org/", name = "confirmation")
    public JAXBElement<Confirmation> createConfirmation(Confirmation value) {
        return new JAXBElement<Confirmation>(_Confirmation_QNAME, Confirmation.class, null, value);
    }

}
