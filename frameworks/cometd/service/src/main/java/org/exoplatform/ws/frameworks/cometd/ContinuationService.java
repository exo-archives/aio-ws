package org.exoplatform.ws.frameworks.cometd;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.mortbay.cometd.AbstractBayeux;
import org.mortbay.cometd.continuation.EXoContinuationBayeux;
import java.util.Map;
import java.util.HashMap;


/**
 * TODO create an interface for this and be enable to change the implementation.
 * We will need another implementation that call a remote Bayeux server using WS
 *
 * TODO: add some other message to broadcast message to all the users
 *
 */
public class ContinuationService {
    private static final Log LOGGER = ExoLogger.getLogger("ContinuationService");

    protected AbstractBayeux getBayeux(){

        ExoContainer container = RootContainer.getInstance();
        container = ((RootContainer)container).getPortalContainer("portal");
        EXoContinuationBayeux bayeux = (EXoContinuationBayeux) container.getComponentInstanceOfType(AbstractBayeux.class);
        return bayeux;
    }

    /**
     *
     * @param eXoId  the user ID
     * @param channel  the channel you want to send the message. The client must listen to this channel to
     * receive it
     * @param data the data you want to send to the client
     */
    public void sendMessage(String eXoId, String channel, Object data) {
        EXoContinuationBayeux bayeux = (EXoContinuationBayeux) getBayeux();
        bayeux.sendMessage(eXoId, channel, data);
    }

    public String getUserToken(String eXoId) {
        EXoContinuationBayeux bayeux = (EXoContinuationBayeux) getBayeux();
        return bayeux.getUserToken(eXoId);
    }


}
