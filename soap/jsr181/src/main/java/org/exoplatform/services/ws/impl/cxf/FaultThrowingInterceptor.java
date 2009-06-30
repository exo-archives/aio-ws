/*
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
package org.exoplatform.services.ws.impl.cxf;

import org.exoplatform.services.log.Log;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $ Dec 17, 2008
 */
public class FaultThrowingInterceptor extends AbstractSoapInterceptor {

  private static final Log LOG = ExoLogger.getLogger(FaultThrowingInterceptor.class);

  public FaultThrowingInterceptor() {
    super(Phase.PRE_STREAM);
    if (LOG.isDebugEnabled()) {
      LOG.debug(">>> FaultThrowingInterceptor.FaultThrowingInterceptor() entered");
    }
  }

  public void handleMessage(SoapMessage message) throws Fault {
    if (LOG.isDebugEnabled()) {
      LOG.debug(">>> FaultThrowingInterceptor.handleMessage() entered");
    }
  }

}
