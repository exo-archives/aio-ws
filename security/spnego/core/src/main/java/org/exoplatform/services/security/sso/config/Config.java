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

  /**
   * Authenticator key. With this key authenticator should be stored in
   * HTTPSession.
   */
  public static final String    SSOAUTHENTICATOR_KEY = "_sso.authenticator";

  /**
   * NTLMSSP signature : NTLMSSP.
   */
  public static final byte[]    NTLMSSP_SIGNATURE    = new byte[] { 0x4E, 0x54, 0x4C, 0x4D, 0x53,
      0x53, 0x50, 0x00                              };

  /**
   * Default character set for NTLM messages.
   */
  public static final String    DEFAULT_CHARSET      = "UnicodeLittleUnmarked";

  /**
   * Negotiate authentication.
   */
  public static final String    HTTP_NEGOTIATE       = "Negotiate";

  /**
   * NTLM authentication.
   */
  public static final String    HTTP_NTLM            = "NTLM";

  /**
   * userId attribute in the LDAP Supported authentication type.
   */
  private static final String[] SUPPORTED_MECHANISM  = new String[] { HTTP_NEGOTIATE, HTTP_NTLM };

  /**
   * Instance of Config.
   */
  private static Config         config;

  /**
   * Domain controller.
   */
  private String                domainCtrl;

  /**
   * Character encoding.
   */
  private String                charset;

  /**
   * LDAP server address. This address will be used for getting user's groups.
   * 
   * @see {@link JndiAction}
   */
  private String                ldapServer;

  /**
   * Name of JAAS context.
   */
  private String                jaasContext;

  /**
   * Address for alternative authentication, should be used if client does not
   * support 'Negotiate' or 'NTLMSSP'.
   */
  private String                redirectOnError;

  /**
   * Is cross domain authentication allowed. Usual actual only for NTLM,
   * Kerberos authentication is allowed only in one domain by default.
   */
  private boolean               crossDomain          = false;

  /**
   * Name of the userId attribute in the LDAP.
   */
  private String                userIdAttr           = "CN";

  /**
   * User URL in LDAP context.
   */
  private String                userURL              = "CN=Users";

  /**
   * Singleton instance. Constructor must not be used directly. Only one
   * instance of this class can be created. First time must be created and
   * configured by SSOConfigurator.
   */
  private Config() {
  }

  /**
   * @return existing instance of Config or create new one.
   */
  public static Config getInstance() {
    return config == null ? config = new Config() : config;
  }

  /**
   * @return the array of supported authentication mechanisms.
   */
  public static String[] getSupportedAuthenticationMechanisms() {
    return SUPPORTED_MECHANISM;
  }

  /**
   * @return the Domain Controller name.
   */
  public String getDomain() {
    return domainCtrl;
  }

  /**
   * Set Domain Controller name.
   * 
   * @param domainCtrl the Domain Controller name.
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
   *          authentication failed.
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
   *          default it is disable. NOTE: This is actual for NTLM only.
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
   * @param jaasContext the JAAS context name.
   */
  void setJaasContext(String jaasContext) {
    this.jaasContext = jaasContext;
  }

  /**
   * @param userIdAttr user Id attribute in the LDAP
   */
  void setUserIdAttr(String userIdAttr) {
    this.userIdAttr = userIdAttr;
  }

  /**
   * @return user Id attribute in the LDAP
   */
  public String getUserIdAttr() {
    return this.userIdAttr;
  }

  /**
   * @param userURL user URL in LDAP context
   */
  void setUserURL(String userURL) {
    this.userURL = userURL;
  }

  /**
   * @return user URL in LDAP context
   */
  public String getUserURL() {
    return this.userURL;
  }

}
