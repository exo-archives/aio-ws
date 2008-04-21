package org.exoplatform.ws.frameworks.cometd;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.mortbay.cometd.AbstractBayeux;
import org.mortbay.cometd.continuation.EXoContinuationBayeux;
import org.mortbay.cometd.continuation.EXoContinuationClient;



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
    
    public EXoContinuationClient getClientByExoId(String exoId) {
      EXoContinuationBayeux bayeux = (EXoContinuationBayeux) getBayeux();
      return bayeux.getClientByEXoId(exoId);
    }
    
    public long getTimeout(){
      EXoContinuationBayeux bayeux = (EXoContinuationBayeux) getBayeux();
      return bayeux.getTimeout();
    }

    /*    public boolean isSubscribe(String eXoId, String channel) {
        throw new Exception();
    }

	public boolean isConnected(String eXoId) {
		throw new Exception();
	}
	
	public boolean sendMessageOnChannel(String channel, Object data) {
		throw new Exception();
	}
	
	public boolean sendMessageToGroup(String group, String channel, Object data) {
		throw new Exception();
		}*/
    
    
//    public boolean isClientAlive(String eXoId) throws NullPointerException{
//      EXoContinuationBayeux bayeux = (EXoContinuationBayeux) getBayeux();
//      Set<EXoContinuationClient> set = bayeux.getClientByEXoId(eXoId);
//      Date date = new Date();
//      for (EXoContinuationClient exoClient : set) {
//       try{ 
////        exoClient.getContinuation().isPending();
////        exoClient.getContinuation().isExpired();
//        long l = date.getTime() - exoClient.lastAccessed();
//        System.out.println("exoClient.lastAccessed() = " + l + " : " + new Date(exoClient.lastAccessed())) ;
//        System.out.println("timeout " + exoClient._timeout.isExpired());
//        System.out.println("timeout " + exoClient._timeout.isScheduled());
//        System.out.println("timeout " + exoClient._timeout.getTimestamp());
//       } catch (Exception e) {
//         e.printStackTrace();
//         throw(new NullPointerException());
//      }
//      }
//      return true;
//    }

    public String getUserToken(String eXoId) {
        EXoContinuationBayeux bayeux = (EXoContinuationBayeux) getBayeux();
        return bayeux.getUserToken(eXoId);
    }


}
