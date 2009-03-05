/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.rest.transformer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of GenericOutputEntityTransformer.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class OutputEntityTransformer implements
    GenericOutputEntityTransformer {
  
  /**
   * External parameters for transformation.
   * In this Map parameters can be added by ResourceContainer. 
   * @see org.exoplatform.services.rest.Response#setTransformerParameters().
   * @see org.exoplatform.services.rest.Response#getTransformerParameters().
   */
  protected Map<String, String> transformerParameters = new HashMap<String, String>();
  
  /**
   * With this constructor from ExoContainer can be got any Objects. 
   * @param components the Objects from ExoContainer.
   * @see EntityTransformerFactory#newTransformer(Class).
   */
  public OutputEntityTransformer(Object... components) {
  }
  
  /**
   * @param trfParams the new parameters for transformer.
   *        Can be null if ResourceContainer does not set any parameters. 
   */
  public void addTransformerParameters(Map<String, String> trfParams) {
    if (trfParams != null) 
      transformerParameters.putAll(trfParams);
  }

  /**
   * Write entity to OutputStream.
   * @param entity the Object which should be writed
   * @param entityDataStream the OutputStream
   * @throws IOException Input/Output Exception
   */
  public abstract void writeTo(Object entity, OutputStream entityDataStream)
      throws IOException;
}

