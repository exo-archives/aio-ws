/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * An abstraction for the value of a HTTP Cache-Control response header.
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">
 *      HTTP/1.1 section 14.9</a>
 */
public class CacheControl {

  private boolean publicCacheable_;
  private boolean privateCacheable_;
  private boolean noCache_;
  private boolean noStore_;
  private boolean noTransform_;
  private boolean mustRevalidate_;
  private boolean proxyRevalidate_;
  private int maxAge_ = -1;
  private int smaxAge_ = -1;
  private List<String> privateFields_;
  private List<String> noCacheFields_;
  private Map<String, String> cacheExtension_;

  private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");

  /**
   * Create a new instance of CacheControl.<br/> The new instance will have the
   * following default settings:
   * <ul>
   * <li>public = true</li>
   * <li>private = false</li>
   * <li>noCache = false</li>
   * <li>noStore = false</li>
   * <li>noTransform = true</li>
   * <li>mustRevalidate = false</li>
   * <li>proxyRevalidate = false</li>
   * <li>An empty list of private fields</li>
   * <li>An empty list of no-cache fields</li>
   * <li>An empty list of cache extensions</li>
   * </ul>
   */
  public CacheControl() {
    publicCacheable_ = true;
    privateCacheable_ = false;
    noCache_ = false;
    noStore_ = false;
    noTransform_ = true;
    mustRevalidate_ = false;
    proxyRevalidate_ = false;
  }

  /**
   * String representation of CacheControl. This string should be included in
   * response as header.
   * @return String representation of CasheControl.
   */
  public String getAsString() {
    StringBuffer buff = new StringBuffer();
    if (isPublicCacheable()) {
      appendString(buff, "public");
    }
    if (isPrivateCacheable()) {
      appendWithParameters(buff, "private", getPrivateFields());
    }
    if (isNoCache()) {
      appendWithParameters(buff, "no-cache", getNoCacheFields());
    }
    if (isNoStore()) {
      appendString(buff, "no-store");
    }
    if (isNoTransform()) {
      appendString(buff, "no-transform");
    }
    if (isMustRevalidate()) {
      appendString(buff, "must-revalidate");
    }
    if (isProxyRevalidate()) {
      appendString(buff, "proxy-revalidate");
    }
    if (getMaxAge() >= 0) {
      appendString(buff, getMaxAge() + "");
    }
    if (getSMaxAge() >= 0) {
      appendString(buff, getSMaxAge() + "");
    }
    for (Map.Entry<String, String> entry : getCacheExtension().entrySet()) {
      appendWithSingleParameter(buff, entry.getKey(), entry.getValue());
    }
    return buff.toString();
  }

  /**
   * Corresponds to the must-revalidate cache control directive.
   * @return true if the must-revalidate cache control directive will be
   *         included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">
   *      HTTP/1.1 section 14.9.4</a>
   */
  public boolean isMustRevalidate() {
    return mustRevalidate_;
  }

  /**
   * Corresponds to the must-revalidate cache control directive.
   * @param status true if the must-revalidate cache control directive should be
   *            included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">
   *      HTTP/1.1 section 14.9.4</a>
   */
  public void setMustRevalidate(boolean status) {
    mustRevalidate_ = status;
  }

  /**
   * Corresponds to the proxy-revalidate cache control directive.
   * @return true if the proxy-revalidate cache control directive will be
   *         included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">
   *      HTTP/1.1 section 14.9.4</a>
   */
  public boolean isProxyRevalidate() {
    return proxyRevalidate_;
  }

  /**
   * Corresponds to the must-revalidate cache control directive.
   * @param status true if the proxy-revalidate cache control directive should
   *            be included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1
   *      section 14.9.4</a>
   */
  public void setProxyRevalidate(boolean status) {
    proxyRevalidate_ = status;
  }

  /**
   * Corresponds to the max-age cache control directive.
   * @return the value of the max-age cache control directive, -1 if the
   *         directive is disabled.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">
   *      HTTP/1.1 section 14.9.3</a>
   */
  public int getMaxAge() {
    return maxAge_;
  }

  /**
   * Corresponds to the max-age cache control directive.
   * @param age the value of the max-age cache control directive, a value of -1
   *            will disable the directive.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1
   *      section 14.9.3</a>
   */
  public void setMaxAge(int age) {
    maxAge_ = age;
  }

  /**
   * Corresponds to the s-maxage cache control directive.
   * @return the value of the s-maxage cache control directive, -1 if the
   *         directive is disabled.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">
   *      HTTP/1.1 section 14.9.3</a>
   */
  public int getSMaxAge() {
    return smaxAge_;
  }

  /**
   * Corresponds to the s-maxage cache control directive.
   * @param age the value of the s-maxage cache control directive, a value of -1
   *            will disable the directive.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1
   *      section 14.9.3</a>
   */
  public void setSMaxAge(int age) {
    smaxAge_ = age;
  }

  /**
   * Corresponds to the no-cache cache control directive.
   * @return true if the no-cache cache control directive will be included in
   *         the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">
   *      HTTP/1.1 section 14.9.1</a>
   */
  public boolean isNoCache() {
    return noCache_;
  }

  /**
   * Corresponds to the no-cache cache control directive.
   * @param state true if the no-cache cache control directive should be
   *            included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
   *      section 14.9.1</a>
   */
  public void setNoCache(boolean state) {
    noCache_ = state;
  }

  /**
   * Corresponds to the no-cache cache control directive.
   * @param state true if the no-cache cache control directive should be
   *            included in the response, false otherwise.
   * @param fields array of fields.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
   *      section 14.9.1</a>
   */
  public void setNoCache(boolean state, List<String> fields) {
    noCache_ = state;
    noCacheFields_ = fields;
  }

  /**
   * Corresponds to the value of the no-cache cache control directive.
   * @return a mutable list of field-names that will form the value of the
   *         no-cache cache control directive. An empty list results in a bare
   *         no-cache directive.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">
   *      HTTP/1.1 section 14.9.1</a>
   */
  public List<String> getNoCacheFields() {
    if (noCacheFields_ == null) {
      noCacheFields_ = new ArrayList<String>();
    }
    return noCacheFields_;
  }

  /**
   * Corresponds to the public cache control directive.
   * @return true if the public cache control directive will be included in the
   *         response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">
   *      HTTP/1.1 section 14.9.1</a>
   */
  public boolean isPublicCacheable() {
    return publicCacheable_;
  }

  /**
   * Corresponds to the public cache control directive.
   * @param status true if the public cache control directive should be included
   *            in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
   *      section 14.9.1</a>
   */
  public void setPublicCacheable(boolean status) {
    publicCacheable_ = status;
  }

  /**
   * Corresponds to the private cache control directive.
   * @return true if the private cache control directive will be included in the
   *         response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">
   *      HTTP/1.1 section 14.9.1</a>
   */
  public boolean isPrivateCacheable() {
    return privateCacheable_;
  }

  /**
   * Corresponds to the private cache control directive.
   * @param status true if the private cache control directive should be
   *            included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
   *      section 14.9.1</a>
   */
  public void setPrivateCacheable(boolean status) {
    privateCacheable_ = status;
  }

  /**
   * Corresponds to the private cache control directive.
   * @param status true if the private cache control directive should be
   *            included in the response, false otherwise.
   * @param fields array of fields.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
   *      section 14.9.1</a>
   */
  public void setPrivateCacheable(boolean status, List<String> fields) {
    privateCacheable_ = status;
    privateFields_ = fields;
  }

  /**
   * Corresponds to the value of the private cache control directive.
   * @return a mutable list of field-names that will form the value of the
   *         private cache control directive. An empty list results in a bare
   *         no-cache directive.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
   *      section 14.9.1</a>
   */
  public List<String> getPrivateFields() {
    if (privateFields_ == null) {
      privateFields_ = new ArrayList<String>();
    }
    return privateFields_;
  }

  /**
   * Corresponds to the no-transform cache control directive.
   * @return true if the no-transform cache control directive will be included
   *         in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">
   *      HTTP/1.1 section 14.9.5</a>
   */
  public boolean isNoTransform() {
    return noTransform_;
  }

  /**
   * Corresponds to the no-transform cache control directive.
   * @param status true if the no-transform cache control directive should be
   *            included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1
   *      section 14.9.5</a>
   */
  public void setNoTransform(boolean status) {
    noTransform_ = status;
  }

  /**
   * Corresponds to the no-store cache control directive.
   * @return true if the no-store cache control directive will be included in
   *         the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">
   *      HTTP/1.1 section 14.9.2</a>
   */
  public boolean isNoStore() {
    return noStore_;
  }

  /**
   * Corresponds to the no-store cache control directive.
   * @param status true if the no-store cache control directive should be
   *            included in the response, false otherwise.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1
   *      section 14.9.2</a>
   */
  public void setNoStore(boolean status) {
    noStore_ = status;
  }

  /**
   * Corresponds to a set of extension cache control directives. A mutable map
   * of cache control extension names and their values. If a key has a null
   * value, it will appear as a bare directive. If a key has a value that
   * contains no whitespace then the directive will appear as a simple
   * name=value pair. If a key has a value that contains whitespace then the
   * directive will appear as a quoted name="value" pair.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.6">HTTP/1.1
   *      section 14.9.6</a>
   */
  public void setCacheExtension(Map<String, String> extensions) {
    cacheExtension_ = extensions;
  }

  /**
   * Corresponds to a set of extension cache control directives.
   * @return a mutable map of cache control extension names and their values. If
   *         a key has a null value, it will appear as a bare directive. If a
   *         key has a value that contains no whitespace then the directive will
   *         appear as a simple name=value pair. If a key has a value that
   *         contains whitespace then the directive will appear as a quoted
   *         name="value" pair.
   * @see <a
   *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.6">
   *      HTTP/1.1 section 14.9.6</a>
   */
  public Map<String, String> getCacheExtension() {
    if (cacheExtension_ == null) {
      cacheExtension_ = new HashMap<String, String>();
    }
    return cacheExtension_;
  }

  private void appendString(StringBuffer buff, String s) {
    if (buff.length() > 0) {
      buff.append(',');
      buff.append(' ');
    }
    buff.append(s);
  }

  private void appendWithSingleParameter(StringBuffer buff, String s,
      String param) {
    StringBuffer localBuff = new StringBuffer();
    localBuff.append(s);
    if (param != null && param.length() > 0) {
      localBuff.append('=');
      localBuff.append(addQuotes(param));
    }
    appendString(buff, localBuff.toString());
  }

  private void appendWithParameters(StringBuffer buff, String s,
      List<String> params) {
    appendString(buff, s);
    if (params.size() > 0) {
      StringBuffer localBuff = new StringBuffer();
      buff.append('=');
      buff.append('"');
      for (String t : params) {
        appendString(localBuff, t);
      }
      buff.append(localBuff.toString());
      buff.append('"');
    }
  }

  private String addQuotes(String s) {
    Matcher macther = SPACES_PATTERN.matcher(s);
    if (macther.find()) {
      return '"' + s + '"';
    }
    return s;
  }

}
