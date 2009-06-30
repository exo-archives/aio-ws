/**
 * Copyright (C) 2003-2008 eXo Platform SAS.
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

package org.exoplatform.services.security.sso.ntlm;

import java.security.Principal;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbSession;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.jaas.UserPrincipal;
import org.exoplatform.services.security.sso.SSOAuthenticationException;
import org.exoplatform.services.security.sso.SSOAuthenticator;
import org.exoplatform.services.security.sso.config.Config;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class NTLMAuthenticator implements SSOAuthenticator {

  private static final Log LOG = ExoLogger.getLogger("ws.security.NTLMAuthenticator");

  /**
   * Response to the client. Can be null if server has nothing to say.
   */
  private byte[] sendBackToken;
  
  /**
   * Domain name.
   */
  private String domain;
  
  /**
   * Workstation name.
   */
  private String workstation;
  
  /**
   * User name.
   */
  private String user;
  
  /**
   * User principal. 
   */
  private Principal principal;
  
  /**
   * Indicate is authentication completed.
   */
  private boolean complete = false;

  /**
   * Indicate is authentication successful.
   */
  private boolean success = false;
  

  /**
   * {@inheritDoc}
   */
  public boolean isComplete() {
    return complete;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * {@inheritDoc}
   */
  public void doAuthenticate(byte[] token) throws Exception {

    if (!isNTLM(token))
      throw new SSOAuthenticationException("Not NTLM tokens.");
      
    Config config = Config.getInstance();
    /* Message Type 1
     */
    if (token[8] == 1) {
      int flags = readLong(token, 12);
      
      /* Negotiate Domain Supplied (0x00001000)
       * When set, the client will send with the message the name of the
       * domain in which the workstation has membership.
       */
      if ((flags & 0x00001000) == 0x1000) {
        int domainLength = readInt(token, 16);
        int domainOffset = readLong(token, 16 + 4);
        
//      NOTE that the supplied domain are in OEM format. (Message type 1)
        domain = new String(token, domainOffset, domainLength);
        if (LOG.isDebugEnabled()) {
          LOG.debug("NTLM message Type 1. Domain name specified: " + domain);
        }
        
        // Check is workstation member of domain.
        if (!domain.equalsIgnoreCase(config.getDomain())) {
          if (!config.getCrossDomain()) {
            complete = true;
            success = false;
            throw new SSOAuthenticationException("Request out of domain, must authenticate");
          }
        }
      }

      /* Negotiate Workstation Supplied (0x00002000)
       * Indicates that the client is sending its workstation name
       * with the message.
       */
      if ((flags & 0x00002000) == 0x2000) {
        int workstationLength = readInt(token, 24);
        int workstationOffset = readLong(token, 24 + 4);
//      NOTE that the supplied domain are in OEM format. (Message type 1)
        workstation = new String(token, workstationOffset, workstationLength);
        if (LOG.isDebugEnabled()) {
          LOG.debug("NTLM message Type 1. Workstation name specified: " + workstation);
        }
        
      }
      
      UniAddress domainController = UniAddress.getByName(config.getDomain(), true);
      byte[] challenge = SmbSession.getChallenge(domainController);

      // Create Message Type 2
      sendBackToken = new byte[32];
      System.arraycopy(Config.NTLMSSP_SIGNATURE, 0, sendBackToken, 0, 8);
      writeLong(sendBackToken, 8, 0x00000002);
      // write flags (indicate support Unicode and support NTLM)
      writeLong(sendBackToken, 20, (0x00000001 | 0x00000200));
      
      System.arraycopy(challenge, 0, sendBackToken, 24, 8);
      
      if (LOG.isDebugEnabled()) {
        LOG.debug("NTLM message type 1. Send back token : " + new String(Base64.encodeBase64(sendBackToken)));
      }
      
      // Indicate we get success on this step but process is not finished yet.
      complete = false;
      success = true;
      
      // end stage one
      return;
    }
    /* 
     * Message Type 3
     */
    if (token[8] == 3) {
      int flags = readLong(token, 60);
      String charset = (flags & 0x00000001) == 1 ? "UnicodeLittleUnmarked"
          : config.getCharset();

      int domainLength = readInt(token, 28);
      int domainOffset = readLong(token, 28 + 4);
      domain = new String(token, domainOffset, domainLength, charset);
      if (LOG.isDebugEnabled()) {
        LOG.debug("NTLM message Type 3. Domain name specified: " + domain);
      }
      
      // Check is workstation member of domain.
      if (!domain.equalsIgnoreCase(config.getDomain())) {
        if (!config.getCrossDomain()) {
          complete = true;
          success = false;
          throw new SSOAuthenticationException("Request out of domain, must authenticate");
        }
      }

      int workstationLength = readInt(token, 44);
      int workstationOffset = readLong(token, 44 + 4);
      workstation = new String(token, workstationOffset, workstationLength,
          charset);
      if (LOG.isDebugEnabled()) {
        LOG.debug("NTLM message Type 3. Workstation name specified: " + workstation);
      }
      
      int userLength = readInt(token, 36);
      int userOffset = readLong(token, 36 + 4);
      user = new String(token, userOffset, userLength, charset);
      if (LOG.isDebugEnabled()) {
        LOG.debug("NTLM message Type 3. User name specified: " + user);
      }

      int lmLength = readInt(token, 12);
      int lmOffset = readLong(token, 12 + 4);
      byte[] lmResponse = new byte[lmLength];
      System.arraycopy(token, lmOffset, lmResponse, 0, lmLength);

      int ntLength = readInt(token, 20);
      int ntOffset = readLong(token, 20 + 4);
      byte[] ntResponse = new byte[ntLength];
      System.arraycopy(token, ntOffset, ntResponse, 0, ntLength);

      UniAddress domainController = UniAddress.getByName(config.getDomain(), true);
      byte[] challenge = SmbSession.getChallenge(domainController);
      NtlmPasswordAuthentication ntlmPassAuth = new NtlmPasswordAuthentication(
          domain, user, challenge, lmResponse, ntResponse);
      SmbSession.logon(domainController, ntlmPassAuth);
      
      principal = new UserPrincipal(getUser());
      
      if (LOG.isDebugEnabled()) {
        LOG.debug("Login successful for principal: " + ntlmPassAuth);
      }
      
      // Authentication successful, level 3 passed.
      complete = true;
      success = true;
      // nothing to send anymore.
      sendBackToken = null;
      
      // end stage two (completed)
      return;
    }
    
    // unknown type of NTLM message, must never happen
    throw new SSOAuthenticationException("Unknown message type. "
        + "Client must send NTLM message type 1 or message type 3.");
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getSendBackToken() {
    return sendBackToken;
  }
  
  /**
   * {@inheritDoc}
   */
  public String getUser() {
    return user;
  }
  
  /**
   * {@inheritDoc}
   */
  public Principal getPrincipal() {
    return principal;
  }
  
  /**
   * Check is authentication token has correct signature.
   * @param token the token.
   * @return true if token signature is correct false otherwise.
   */
  public static boolean isNTLM(byte[] token) {
    if (token == null || token.length < 8)
      return false;
    for (int i = 0; i < 8; i++)
      if (Config.NTLMSSP_SIGNATURE[i] != token[i])
        return false;
    return true;
  }

  /**
   * Read integer value from given byte array. 
   * Not Java integer, two bytes. 
   * @param bb source bytes.
   * @param p the position in byte array.
   * @return integer.
   */
  private static int readInt(byte[] bb, int p) {
    return (bb[p] & 0xFF) | ((bb[p + 1] & 0xFF) << 8);
  }

  /**
   * Read long value from given byte array.
   * Not Java long, four bytes.
   * @param bb source bytes.
   * @param p the position in byte array.
   * @return long.
   */
  private static int readLong(byte[] bb, int p) {
    return (bb[p] & 0xFF) | ((bb[p + 1] & 0xFF) << 8)
         | ((bb[p + 2] & 0xFF) << 16) | ((bb[p + 2] & 0xFF) << 24);
  }

  /**
   * Write long value to given byte array.
   * This is not Java long, four bytes.
   * @param bb the bytes.
   * @param p the start position.
   * @param l the long value.
   */
  private static void writeLong(byte[] bb, int p, int l) {
    bb[p] = (byte) (l & 0xFF);
    bb[p + 1] = (byte) (l >> 8  & 0xFF);
    bb[p + 2] = (byte) (l >> 16 & 0xFF);
    bb[p + 3] = (byte) (l >> 24 & 0xFF);
  }  

}
