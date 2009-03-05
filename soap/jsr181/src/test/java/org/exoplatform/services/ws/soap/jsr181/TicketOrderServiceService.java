/*
 * 
 */

package org.exoplatform.services.ws.soap.jsr181;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.exoplatform.services.ws.soap.jsr181.custom.TicketOrderService;

/**
 * This class was generated by Apache CXF 2.1.3 Wed Dec 03 10:51:19 GMT+02:00
 * 2008 Generated source version: 2.1.3
 */

@WebServiceClient(name = "TicketOrderServiceCustom", wsdlLocation = "http://localhost:8080/TicketOrderServiceSingleton?wsdl", targetNamespace = "http://exoplatform.org/soap/cxf")
public class TicketOrderServiceService extends Service {

  public final static URL   WSDL_LOCATION;

  public final static QName SERVICE                = new QName("http://exoplatform.org/soap/cxf",
                                                               "TicketOrderServiceCustom");

  public final static QName TicketOrderServicePort = new QName("http://exoplatform.org/soap/cxf",
                                                               "TicketOrderServiceCustomPort");
  static {
    URL url = null;
    try {
      url = new URL("http://localhost:8080/TicketOrderServiceSingleton?wsdl");
    } catch (MalformedURLException e) {
      System.err.println("Can not initialize the default wsdl from http://localhost:8080/TicketOrderServiceSingleton?wsdl");
      // e.printStackTrace();
    }
    WSDL_LOCATION = url;
  }

  public TicketOrderServiceService(URL wsdlLocation) {
    super(wsdlLocation, SERVICE);
  }

  public TicketOrderServiceService(URL wsdlLocation, QName serviceName) {
    super(wsdlLocation, serviceName);
  }

  public TicketOrderServiceService() {
    super(WSDL_LOCATION, SERVICE);
  }

  /**
   * @return returns TicketOrderService
   */
  @WebEndpoint(name = "TicketOrderServicePort")
  public TicketOrderService getTicketOrderServicePort() {
    return super.getPort(TicketOrderServicePort, TicketOrderService.class);
  }

  /**
   * @param features A list of {@link javax.xml.ws.WebServiceFeature} to
   *          configure on the proxy. Supported features not in the
   *          <code>features</code> parameter will have their default values.
   * @return returns TicketOrderService
   */
  @WebEndpoint(name = "TicketOrderServicePort")
  public TicketOrderService getTicketOrderServicePort(WebServiceFeature... features) {
    return super.getPort(TicketOrderServicePort, TicketOrderService.class, features);
  }

}
