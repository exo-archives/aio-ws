/**
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 */

package org.exoplatform.services.rest.container;

/**
 * Created by The eXo Platform SAS .
 * @author Gennady Azarenkov
 * @version $Id: $
 */

public class InvalidResourceDescriptorException extends Exception {

  public InvalidResourceDescriptorException() {
    super();
  }

  public InvalidResourceDescriptorException(final String message,
      final Throwable cause) {
    super(message, cause);
  }

  public InvalidResourceDescriptorException(final String message) {
    super(message);
  }

  public InvalidResourceDescriptorException(final Throwable cause) {
    super(cause);
  }

}
