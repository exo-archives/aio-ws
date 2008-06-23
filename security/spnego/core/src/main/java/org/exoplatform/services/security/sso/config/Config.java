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

package org.exoplatform.services.security.sso.config;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Config {

  public static final String SSOAUTHENTICATOR_KEY = "_sso.authenticator";

  // NTLMSSP signature : NTLMSSP
  public static final byte[] NTLMSSP_SIGNATURE = new byte[]{ 0x4E, 0x54, 0x4C, 0x4D, 0x53, 0x53, 0x50, 0x00 };

  // Default character set for NTLM messages
  public static final String DEFAULT_CHARSET = "UnicodeLittleUnmarked";

  // Possible names of authentication mechanism
  public static final String HTTP_NEGOTIATE = "Negotiate";
  public static final String HTTP_NTLM = "NTLM";

  private static String[] supportedMechanisms = new String[]{ HTTP_NEGOTIATE, HTTP_NTLM };

  private static Config config;

  private String domainCtrl;
  private String charset;
  private String ldapServer;
  private String jaasContext;
  private String redirectOnError;
  private boolean crossDomain = false;

  /*
   * Only one instance of this class can be created. First time must be created
   * and configured by SSOConfigurator.
   */
  private Config() {
  }

  public static Config getInstance() {
    return config == null ? config = new Config() : config;
  }

  /**
   * @return the array of supported authentication mechanisms.
   */
  public static String[] getSupportedAuthenticationMechanisms() {
    return supportedMechanisms;
  }

  /**
   * @return the Domain Controller name.
   */
  public String getDomain() {
    return domainCtrl;
  }

  /**
   * Set Domain Controller name.
   * @param the domainCtrl the Domain Controller name.
   */
  void setDomain(String domainCtrl) {
    this.domainCtrl = domainCtrl;
  }

  /**
   * @return the name of character set.
   */
  public String getCharset() {
    return charset == null ? DEFAULT_CHARSET : charset;
  }

  /**
   * @param charset the name of character set.
   */
  void setCharset(String charset) {
    this.charset = charset;
  }

  /**
   * @return the ldap server address.
   */
  public String getLdapServer() {
    return ldapServer;
  }

  /**
   * @param ldapServer the LDAP server address.
   */
  void setLdapServer(String ldapServer) {
    this.ldapServer = ldapServer;
  }

  /**
   * @return the URL for other type of authentication.
   */
  public String getRedirectOnError() {
    return redirectOnError;
  }

  /**
   * @param url the URL for other type of authentication. Can be used if SSO
   *            authentication failed.
   */
  void setRedirectOnError(String url) {
    this.redirectOnError = url;
  }

  /**
   * @return true is cross domain authentication is enable, false otherwise.
   */
  public boolean getCrossDomain() {
    return crossDomain;
  }

  /**
   * @param crossDomain set cross domain authentication to enable or disable. By
   *            default it is disable. NOTE: This is actual for NTLM only.
   */
  void setCrossDomain(boolean crossDomain) {
    this.crossDomain = crossDomain;
  }

  /**
   * @return the JAAS context name.
   */
  public String getJaasContext() {
    return jaasContext;
  }

  /**
   * @param jassContext the JAAS context name.
   */
  void setJaasContext(String jaasContext) {
    this.jaasContext = jaasContext;
  }

}
