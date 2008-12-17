package org.exoplatform.services.ws.soap.jsr181.custom;

import javax.xml.ws.WebFault;

/**
 */

@WebFault(name = "InvalidRegistration", targetNamespace = "http://exoplatform.org/soap/cxf")
public class InvalidRegistration extends Exception {
  public static final long         serialVersionUID = 20081216172650L;

  private InvalidRegistrationFault invalidRegistration;

  public InvalidRegistration() {
    super();
  }

  public InvalidRegistration(String message) {
    super(message);
  }

  public InvalidRegistration(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidRegistration(String message, InvalidRegistrationFault invalidRegistration) {
    super(message);
    this.invalidRegistration = invalidRegistration;
  }

  public InvalidRegistration(String message,
                             InvalidRegistrationFault invalidRegistration,
                             Throwable cause) {
    super(message, cause);
    this.invalidRegistration = invalidRegistration;
  }

  public InvalidRegistrationFault getFaultInfo() {
    return this.invalidRegistration;
  }
}
