package org.mortbay.cometd.continuation;

import dojox.cometd.*;

import org.mortbay.cometd.ClientImpl;
import org.mortbay.cometd.MessageImpl;
import org.mortbay.cometd.SuspendingBayeux;
import org.mortbay.cometd.Transport;
import org.mortbay.cometd.ChannelId;

import javax.servlet.ServletContext;
import java.util.*;
import java.io.IOException;
import java.security.SecureRandom;


public class EXoContinuationBayeux extends ContinuationBayeux{

    // need to clean the map if an element hasn't been accessed for x min
    private static Map userToken = new HashMap();
    transient Random _random;
    private long timeout;

    public EXoContinuationBayeux()
    {
        super();
        this.setSecurityPolicy(new EXoSecurityPolicy());
        
    }

    public ClientImpl newRemoteClient() {
       EXoContinuationClient client = new EXoContinuationClient(this);

        return client;
    }
    
    public void setTimeout(long timeout) {
      this.timeout = timeout;
    }
    
    public long getTimeout() {
      return timeout;
    } 
    
    /* ------------------------------------------------------------ */
    long getRandom(long variation)
    {
        long l=_random.nextLong()^variation;
        return l<0?-l:l;
    }

    public String getUserToken(String eXoId) {
        if (userToken.containsKey(eXoId)) {
            return (String) userToken.get(eXoId);
        }
        String token=Long.toString(this.getRandom(System.identityHashCode(this)^System.currentTimeMillis()),36);
        userToken.put(eXoId, token);
        return token;
    }
       
        /* ------------------------------------------------------------ */
    /* (non-Javadoc)
     * @see org.mortbay.cometd.AbstractBayeux#initialize(javax.servlet.ServletContext)
     */
    protected void initialize(ServletContext context)
    {
        super.initialize(context);
        
        try
        {
            _random= SecureRandom.getInstance("SHA1PRNG");
        }
        catch (Exception e)
        {
            context.log("Could not get secure random for ID generation",e);
            _random=new Random();
        }
        _random.setSeed(_random.nextLong()^hashCode()^(context.hashCode()<<32)^Runtime.getRuntime().freeMemory());
        
    }

    /**
     *
     * @param eXoID
     * @return
     */
//    public Set<EXoContinuationClient> getClientByEXoId(String eXoID){
//        Set<String> ids = getClientIDs();
//        Set<EXoContinuationClient> clients = new HashSet<EXoContinuationClient>();
//
//        for(String id:ids) {
//            Client client = getClient(id);
//            if(client instanceof EXoContinuationClient) {
//                EXoContinuationClient exoClient = (EXoContinuationClient) client;
//                if (exoClient.getEXoId() != null && exoClient.getEXoId().equals(eXoID))
//                    clients.add(exoClient);
//            }
//
//        }
//        return clients;
//    }
    
    public EXoContinuationClient getClientByEXoId(String eXoID){
      Set<String> ids = getClientIDs();
      for(String id:ids) {
          Client client = getClient(id);
          if(client instanceof EXoContinuationClient) {
              EXoContinuationClient exoClient = (EXoContinuationClient) client;
              if (exoClient.getEXoId() != null && exoClient.getEXoId().equals(eXoID))
                  return exoClient;
          }

      }
      return null;
  }




    public void sendMessage(String eXoId, String channel, Object data) {
      EXoContinuationClient toClient = getClientByEXoId(eXoId);
      send(toClient, channel, data, null);
    } 


        /* ------------------------------------------------------------ */
    /** Send data to a individual client.
     * The data passed is sent to the client as the "data" member of a message
     * with the given channel and id.  The message is not published on the channel and is
     * thus not broadcast to all channel subscribers.  However to the target client, the
     * message appears as if it was broadcast.
     * <p>
     * Typcially this method is only required if a service method sends response(s) to
     * channels other than the subscribed channel. If the response is to be sent to the subscribed
     * channel, then the data can simply be returned from the subscription method.
     *
     * @param toClient The target client
     * @param onChannel The channel the message is for
     * @param data The data of the message
     * @param id The id of the message (or null for a random id).
     */
    protected void send(Client toClient, String onChannel, Object data, String id)
    {
        ClientImpl fromClient = (ClientImpl) newClient("EXoContinuationBayeux",null);
        Message reply = newMessage();
        reply.put(Bayeux.DATA_FIELD,data);
        if (id!=null)
            reply.put(Bayeux.ID_FIELD,id);
        deliver(fromClient, toClient, onChannel, reply);
        ((MessageImpl)reply).decRef();
    }


    
    
    
        /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    public static class EXoSecurityPolicy implements SecurityPolicy
    {
      public EXoSecurityPolicy() {
        super();
      }
      
        public boolean canHandshake(Message message)
        {
            return checkUser(message);
        }

        public boolean canCreate(Client client, String channel, Message message)
        {
            return client!=null && !channel.startsWith("/meta/");
        }

        public boolean canSubscribe(Client client, String channel, Message message)
        {
            if (!checkUser(message)) {
                return false;
            }
            // We set the eXoID
            if (((EXoContinuationClient)client).getEXoId() == null) {
                ((EXoContinuationClient)client).setEXoId((String) message.get("exoId"));
            }
            
            return client!=null && !channel.startsWith("/meta/");
        }

        public boolean canPublish(Client client, String channel, Message message)
        {
          
            return client!=null && !channel.startsWith("/meta/");
        }

        private boolean checkUser(Message message) {
            String userId = (String) message.get("exoId");
            String eXoToken = (String) message.get("exoToken");
            return (userId != null && userToken.containsKey(userId) && userToken.get(userId).equals(eXoToken));
        }

    }

}
