/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This transformer read data from given InputStream and write it to
 * OutputStream. This type of transformers can be usefull when ResourceContainer
 * produce InputStream representation of requested resource.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PassthroughOutputTransformer extends OutputEntityTransformer {

  private static final int BUFF_SIZE = 4096;
  
  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.OutputEntityTransformer#writeTo(java.lang.Object,
   *      java.io.OutputStream)
   */
  @Override
  public final void writeTo(final Object entity, final OutputStream entityDataStream)
      throws IOException {
    InputStream e = (InputStream) entity;
    byte[] buf = new byte[BUFF_SIZE];
    int rd = -1;
    while ((rd = e.read(buf)) != -1) {
      entityDataStream.write(buf, 0, rd);
    }
  }

}
