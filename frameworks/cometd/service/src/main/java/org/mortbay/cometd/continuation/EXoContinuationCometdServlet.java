package org.mortbay.cometd.continuation;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.mortbay.cometd.AbstractBayeux;

import java.util.List;


public class EXoContinuationCometdServlet extends ContinuationCometdServlet {
    private static final Log LOGGER = ExoLogger.getLogger("EXoContinuationCometdServlet");

    protected AbstractBayeux newBayeux() {
        ExoContainer container = RootContainer.getInstance();
        container = ((RootContainer)container).getPortalContainer("portal");

        LOGGER.debug("EXoContinuationCometdServlet - Current Container-ExoContainer: " + container);
        AbstractBayeux bayeux = (AbstractBayeux) container.getComponentInstanceOfType(AbstractBayeux.class);
        LOGGER.debug("EXoContinuationCometdServlet - -->AbstractBayeux=" + bayeux);
        return bayeux;

    }


}
