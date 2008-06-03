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
  private String name_;                                                       
  private String value_;
  
  /* Optional. Describes cookie and how it use. 
   */
  private String comment_;                                                        
  
  /* Optional. Domain that sees cookie. 
   */
  private String domain_;
  
  /* Cookie auto-expire after specified time. 
   */
  private int maxAge_ = -1;
  
  /* Optional. URLs that see the cookie. 
   */
  private String path_;
  
  /* Optional. Secure (use SSL, https://)
   */
  private boolean secure_ = false;
  
  /* Set the default version to 0 (Netscape's Implementation).
   * Can be set to 1 (RFC 2109).
   */
  private int version_ = 0;
  
  public Cookie(String name, String value, String path, String domain,
      int version) {
    name_ = name;
    value_ = value;
    setVersion(version);
    domain_ = domain;
    path_ = path;
  }
  
  public Cookie(String name, String value, String path, String domain) {                                                   
    name_ = name;                                                                                                    
    value_ = value;                                                                                                  
    domain_ = domain;                                                                                                
    path_ = path;                                                                                                    
  }   
  
  public Cookie(String name, String value) {
    name_ = name;
    value_ = value;
  }
  
  /**
   * @return the cookie name.
   */
  public String getName() {
    return name_;
  }
  
  /**
   * @return the cookie value.
   */
  public String getValue() {
    return value_;
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
    return maxAge_;
  }
  
  /**
   * @see #getMaxAge()
   * @param maxAge the new cookie max age.
   */
  public void setMaxAge(int maxAge) {
    maxAge_ = maxAge;
  }
  
  /**
   * Cookie version. By default cookie version set to 0,
   * this means it is Netscape version of cookie.
   * Version 1 - RFC 2109.
   * @return the cookie version.
   */
  public int getVersion() {
    return version_;
  }
  
  /**
   * @see #getVersion()
   * @param version the cookie version.
   */
  public void setVersion(int version) {
    if (version != 1 && version != 0)
      throw new IllegalArgumentException("Unknow cookie version, must be 0 or 1.");
    version_ = version;
  }
  
  /**
   * @return the domain name for this cookie.
   */
  public String getDomain() {
    return domain_;
  }
  
  /**
   * @return path for the cookie to which the client should return the cookie.
   */
  public String getPath() {
    return path_;
  }
  
  /**
   * Check is this cookie marked as secure. 
   * If it is then it must be send only using secure protocol, SSL or HTTPS ...
   * @return the cookie secure state.
   */
  public boolean isSecure() {
    return secure_;
  }
  
  /**
   * @see #isSecure()
   * @param secure the new cookie secure state.
   */
  public void setSecure(boolean secure) {
    secure_ = secure;
  }

  /**
   * @return the cookie comment.
   */
  public String getComment() {
    return comment_;
  }

  /**
   * @param comment the comment for this cookie.
   */
  public void setComment(String comment) {
    comment_ = comment;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("name = ").append(name_)
      .append("; value = ").append(value_)
      .append("; maxAge = ").append(maxAge_)
      .append("; version = ").append(version_)
      .append("; domain = ").append(domain_)
      .append("; path = ").append(path_)
      .append("; comment = ").append(comment_);
    return sb.toString();
  }

}

