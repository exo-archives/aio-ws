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

package org.exoplatform.services.rest;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class Cookie {
  /**
   * Cookie name. 
   */
  private String name;
  
  /**
   * Cookie value.
   */
  private String value;
  
  /**
   * Describes cookie and how it use. This optional.  
   */
  private String comment;                                                        
  
  /**
   * Domain that sees cookie. This optional.
   */
  private String domain;
  
  /**
   *  Cookie auto-expire after specified time. 
   */
  private int maxAge = -1;
  
  /**
   *  URLs that see the cookie. This is optional. 
   */
  private String path;
  
  /**
   * Secure (use SSL, https://). This is optional, by default false.
   */
  private boolean secure = false;
  
  /**
   * Set the default version to 0 (Netscape's Implementation).
   * Can be set to 1 (RFC 2109).
   */
  private int version = 0;
  
  /**
   * @param name the coolie name.
   * @param value the cookie value.
   * @param path the URLs that see the cookie.
   * @param domain the domain that sees cookie.
   * @param version the cookie version, can be 0 or 1.
   */
  public Cookie(String name, String value, String path, String domain,
      int version) {
    this.name = name;
    this.value = value;
    this.path = path;
    this.domain = domain;
    setVersion(version);
  }
  
  /**
   * Cookie with default version, default version is 0.
   * @param name the coolie name.
   * @param value the cookie value.
   * @param path the URLs that see the cookie.
   * @param domain the domain that sees cookie.
   */
  public Cookie(String name, String value, String path, String domain) {                                                   
    this.name = name;                                                                                                    
    this.value = value;                                                                                                  
    this.path = path;                                                                                                    
    this.domain = domain;                                                                                                
  }   
  
  /**
   * Cookie with default version, and without specified path and domain.
   * @param name the coolie name.
   * @param value the cookie value.
   */
  public Cookie(String name, String value) {
    this.name = name;
    this.value = value;
  }
  
  /**
   * @return the cookie name.
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return the cookie value.
   */
  public String getValue() {
    return value;
  }
  
  /**
   * A positive value indicates that the cookie will expire after that many
   * seconds have passed.<br/>
   * A negative value means that the cookie is not stored persistently and will
   * be deleted when the Web browser exits.
   * A zero value causes the cookie to be deleted.
   * @return the cookie max age.
   */
  public int getMaxAge() {
    return maxAge;
  }
  
  /**
   * @see #getMaxAge()
   * @param maxAge the new cookie max age.
   */
  public void setMaxAge(int maxAge) {
    this.maxAge = maxAge;
  }
  
  /**
   * Cookie version. By default cookie version set to 0,
   * this means it is Netscape version of cookie.
   * Version 1 - RFC 2109.
   * @return the cookie version.
   */
  public int getVersion() {
    return version;
  }
  
  /**
   * @see #getVersion()
   * @param version the cookie version.
   */
  public void setVersion(int version) {
    if (version != 1 && version != 0)
      throw new IllegalArgumentException("Unknow cookie version, must be 0 or 1.");
    this.version = version;
  }
  
  /**
   * @return the domain name for this cookie.
   */
  public String getDomain() {
    return domain;
  }
  
  /**
   * @return path for the cookie to which the client should return the cookie.
   */
  public String getPath() {
    return path;
  }
  
  /**
   * Check is this cookie marked as secure. 
   * If it is then it must be send only using secure protocol, SSL or HTTPS ...
   * @return the cookie secure state.
   */
  public boolean isSecure() {
    return secure;
  }
  
  /**
   * @see #isSecure()
   * @param secure the new cookie secure state.
   */
  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  /**
   * @return the cookie comment.
   */
  public String getComment() {
    return comment;
  }

  /**
   * @param comment the comment for this cookie.
   */
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  /** String representation of cookie. 
   * @see java.lang.Object#toString()
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("name = ").append(name)
      .append("; value = ").append(value)
      .append("; maxAge = ").append(maxAge)
      .append("; version = ").append(version)
      .append("; domain = ").append(domain)
      .append("; path = ").append(path)
      .append("; comment = ").append(comment);
    return sb.toString();
  }

}

