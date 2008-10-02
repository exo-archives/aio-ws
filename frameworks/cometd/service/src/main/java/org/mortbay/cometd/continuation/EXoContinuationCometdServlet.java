package org.mortbay.cometd.continuation;

/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.mortbay.cometd.AbstractBayeux;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class EXoContinuationCometdServlet extends ContinuationCometdServlet {
  
  /**
   * Logger.
   */
  private static Log log = ExoLogger.getLogger("ws.EXoContinuationCometdServlet");

  /**
   * {@inheritDoc}
   */
  protected EXoContinuationBayeux newBayeux() {
    try {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      if (container == null) {
        container = ExoContainerContext.getContainerByName("portal");
      }
      if (container instanceof RootContainer) {
        container = RootContainer.getInstance().getPortalContainer("portal");
      }
      if (log.isInfoEnabled())
        log.info("EXoContinuationCometdServlet - Current Container-ExoContainer: " + container);
      EXoContinuationBayeux bayeux = (EXoContinuationBayeux) container.getComponentInstanceOfType(AbstractBayeux.class);
      bayeux.setTimeout(Long.parseLong(getInitParameter("timeout")));
      if (log.isDebugEnabled())
        log.debug("EXoContinuationCometdServlet - -->AbstractBayeux=" + bayeux);
      return bayeux;
    } catch (Exception e) {
      log.error("Error new Bayeux creation ", e);
      return null;
    }
  }

}
