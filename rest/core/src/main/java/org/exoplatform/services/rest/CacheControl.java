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
 * 
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1
 *      section 14.9</a>
 */
public class CacheControl
{

   /**
    * Public cache control directive, see {@link #isPublicCacheable()} .
    */
   private boolean publicCacheable;

   /**
    * Private cache control directive, see {@link #isPrivateCacheable()} .
    */
   private boolean privateCacheable;

   /**
    * No-cache control directive, see {@link #noCache} .
    */
   private boolean noCache;

   /**
    * No-store control directive, see {@link #noStore} .
    */
   private boolean noStore;

   /**
    * No-transform directive, see {@link #isNoTransform()} .
    */
   private boolean noTransform;

   /**
    * must-revalidate cache control directive, see {@link #isMustRevalidate()} .
    */
   private boolean mustRevalidate;

   /**
    * Proxy-revalidate cache control directive, see {@link #isProxyRevalidate()}
    * .
    */
   private boolean proxyRevalidate;

   /**
    * Max-age cache control directive.
    */
   private int maxAge = -1;

   /**
    * S-maxage cache control directive.
    */
   private int smaxAge = -1;

   /**
    * Private cache control fields. Must not be store in shared cache.
    */
   private List<String> privateFields;

   /**
    * Must not be cached.
    */
   private List<String> noCacheFields;

   /**
    * Cache extensions.
    */
   private Map<String, String> cacheExtension;

   /**
    * Whitespaces pattern. Used for checking does String contains any whitespace
    * character or not.
    */
   private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");

   /**
    * Create a new instance of CacheControl.<br/>
    * The new instance will have the following default settings:
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
   public CacheControl()
   {
      publicCacheable = true;
      privateCacheable = false;
      noCache = false;
      noStore = false;
      noTransform = true;
      mustRevalidate = false;
      proxyRevalidate = false;
   }

   /**
    * String representation of CacheControl. This string should be included in
    * response as header.
    * 
    * @return String representation of CasheControl.
    */
   public String getAsString()
   {
      StringBuffer buff = new StringBuffer();
      if (isPublicCacheable())
      {
         appendString(buff, "public");
      }
      if (isPrivateCacheable())
      {
         appendWithParameters(buff, "private", getPrivateFields());
      }
      if (isNoCache())
      {
         appendWithParameters(buff, "no-cache", getNoCacheFields());
      }
      if (isNoStore())
      {
         appendString(buff, "no-store");
      }
      if (isNoTransform())
      {
         appendString(buff, "no-transform");
      }
      if (isMustRevalidate())
      {
         appendString(buff, "must-revalidate");
      }
      if (isProxyRevalidate())
      {
         appendString(buff, "proxy-revalidate");
      }
      if (getMaxAge() >= 0)
      {
         appendWithSingleParameter(buff, "max-age", Integer.toString(getMaxAge()));
      }
      if (getSMaxAge() >= 0)
      {
         appendWithSingleParameter(buff, "s-maxage", Integer.toString(getSMaxAge()));
      }
      for (Map.Entry<String, String> entry : getCacheExtension().entrySet())
      {
         appendWithSingleParameter(buff, entry.getKey(), entry.getValue());
      }
      return buff.toString();
   }

   /**
    * Corresponds to the must-revalidate cache control directive.
    * 
    * @return true if the must-revalidate cache control directive will be
    *         included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1
    *      section 14.9.4</a>
    */
   public boolean isMustRevalidate()
   {
      return mustRevalidate;
   }

   /**
    * Corresponds to the must-revalidate cache control directive.
    * 
    * @param status true if the must-revalidate cache control directive should
    *        be included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1
    *      section 14.9.4</a>
    */
   public void setMustRevalidate(boolean status)
   {
      mustRevalidate = status;
   }

   /**
    * Corresponds to the proxy-revalidate cache control directive.
    * 
    * @return true if the proxy-revalidate cache control directive will be
    *         included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1
    *      section 14.9.4</a>
    */
   public boolean isProxyRevalidate()
   {
      return proxyRevalidate;
   }

   /**
    * Corresponds to the must-revalidate cache control directive.
    * 
    * @param status true if the proxy-revalidate cache control directive should
    *        be included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1
    *      section 14.9.4</a>
    */
   public void setProxyRevalidate(boolean status)
   {
      proxyRevalidate = status;
   }

   /**
    * Corresponds to the max-age cache control directive.
    * 
    * @return the value of the max-age cache control directive, -1 if the
    *         directive is disabled.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1
    *      section 14.9.3</a>
    */
   public int getMaxAge()
   {
      return maxAge;
   }

   /**
    * Corresponds to the max-age cache control directive.
    * 
    * @param age the value of the max-age cache control directive, a value of -1
    *        will disable the directive.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1
    *      section 14.9.3</a>
    */
   public void setMaxAge(int age)
   {
      maxAge = age;
   }

   /**
    * Corresponds to the s-maxage cache control directive.
    * 
    * @return the value of the s-maxage cache control directive, -1 if the
    *         directive is disabled.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1
    *      section 14.9.3</a>
    */
   public int getSMaxAge()
   {
      return smaxAge;
   }

   /**
    * Corresponds to the s-maxage cache control directive.
    * 
    * @param age the value of the s-maxage cache control directive, a value of
    *        -1 will disable the directive.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1
    *      section 14.9.3</a>
    */
   public void setSMaxAge(int age)
   {
      smaxAge = age;
   }

   /**
    * Corresponds to the no-cache cache control directive.
    * 
    * @return true if the no-cache cache control directive will be included in
    *         the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public boolean isNoCache()
   {
      return noCache;
   }

   /**
    * Corresponds to the no-cache cache control directive.
    * 
    * @param state true if the no-cache cache control directive should be
    *        included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public void setNoCache(boolean state)
   {
      noCache = state;
   }

   /**
    * Corresponds to the no-cache cache control directive.
    * 
    * @param state true if the no-cache cache control directive should be
    *        included in the response, false otherwise.
    * @param fields array of fields.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public void setNoCache(boolean state, List<String> fields)
   {
      noCache = state;
      noCacheFields = fields;
   }

   /**
    * Corresponds to the value of the no-cache cache control directive.
    * 
    * @return a mutable list of field-names that will form the value of the
    *         no-cache cache control directive. An empty list results in a bare
    *         no-cache directive.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public List<String> getNoCacheFields()
   {
      if (noCacheFields == null)
      {
         noCacheFields = new ArrayList<String>();
      }
      return noCacheFields;
   }

   /**
    * Corresponds to the public cache control directive.
    * 
    * @return true if the public cache control directive will be included in the
    *         response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public boolean isPublicCacheable()
   {
      return publicCacheable;
   }

   /**
    * Corresponds to the public cache control directive.
    * 
    * @param status true if the public cache control directive should be
    *        included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public void setPublicCacheable(boolean status)
   {
      publicCacheable = status;
   }

   /**
    * Corresponds to the private cache control directive.
    * 
    * @return true if the private cache control directive will be included in
    *         the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public boolean isPrivateCacheable()
   {
      return privateCacheable;
   }

   /**
    * Corresponds to the private cache control directive.
    * 
    * @param status true if the private cache control directive should be
    *        included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public void setPrivateCacheable(boolean status)
   {
      privateCacheable = status;
   }

   /**
    * Corresponds to the private cache control directive.
    * 
    * @param status true if the private cache control directive should be
    *        included in the response, false otherwise.
    * @param fields array of fields.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public void setPrivateCacheable(boolean status, List<String> fields)
   {
      privateCacheable = status;
      privateFields = fields;
   }

   /**
    * Corresponds to the value of the private cache control directive.
    * 
    * @return a mutable list of field-names that will form the value of the
    *         private cache control directive. An empty list results in a bare
    *         no-cache directive.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1
    *      section 14.9.1</a>
    */
   public List<String> getPrivateFields()
   {
      if (privateFields == null)
      {
         privateFields = new ArrayList<String>();
      }
      return privateFields;
   }

   /**
    * Corresponds to the no-transform cache control directive.
    * 
    * @return true if the no-transform cache control directive will be included
    *         in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1
    *      section 14.9.5</a>
    */
   public boolean isNoTransform()
   {
      return noTransform;
   }

   /**
    * Corresponds to the no-transform cache control directive.
    * 
    * @param status true if the no-transform cache control directive should be
    *        included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1
    *      section 14.9.5</a>
    */
   public void setNoTransform(boolean status)
   {
      noTransform = status;
   }

   /**
    * Corresponds to the no-store cache control directive.
    * 
    * @return true if the no-store cache control directive will be included in
    *         the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1
    *      section 14.9.2</a>
    */
   public boolean isNoStore()
   {
      return noStore;
   }

   /**
    * Corresponds to the no-store cache control directive.
    * 
    * @param status true if the no-store cache control directive should be
    *        included in the response, false otherwise.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1
    *      section 14.9.2</a>
    */
   public void setNoStore(boolean status)
   {
      noStore = status;
   }

   /**
    * Corresponds to a set of extension cache control directives. A mutable map
    * of cache control extension names and their values. If a key has a null
    * value, it will appear as a bare directive. If a key has a value that
    * contains no whitespace then the directive will appear as a simple
    * name=value pair. If a key has a value that contains whitespace then the
    * directive will appear as a quoted name="value" pair.
    * 
    * @param extensions the cache extensions.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.6">HTTP/1.1
    *      section 14.9.6</a>
    */
   public void setCacheExtension(Map<String, String> extensions)
   {
      cacheExtension = extensions;
   }

   /**
    * Corresponds to a set of extension cache control directives.
    * 
    * @return a mutable map of cache control extension names and their values.
    *         If a key has a null value, it will appear as a bare directive. If
    *         a key has a value that contains no whitespace then the directive
    *         will appear as a simple name=value pair. If a key has a value that
    *         contains whitespace then the directive will appear as a quoted
    *         name="value" pair.
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.6">HTTP/1.1
    *      section 14.9.6</a>
    */
   public Map<String, String> getCacheExtension()
   {
      if (cacheExtension == null)
      {
         cacheExtension = new HashMap<String, String>();
      }
      return cacheExtension;
   }

   /**
    * Add single <code>String</code> to <code>StringBuffer</code> .
    * 
    * @param buff the StringBuffer.
    * @param s single String.
    */
   private static void appendString(StringBuffer buff, String s)
   {
      if (buff.length() > 0)
      {
         buff.append(',');
         buff.append(' ');
      }
      buff.append(s);
   }

   /**
    * Add single pair key=value to <code>StringBuffer</code> . If value contains
    * whitespaces then quotes will be added.
    * 
    * @param buff the StringBuffer.
    * @param key the key.
    * @param value the value.
    */
   private static void appendWithSingleParameter(StringBuffer buff, String key, String value)
   {
      StringBuffer localBuff = new StringBuffer();
      localBuff.append(key);
      if (value != null && value.length() > 0)
      {
         localBuff.append('=');
         localBuff.append(addQuotes(value));
      }
      appendString(buff, localBuff.toString());
   }

   /**
    * Add to pair key="value1, value2" to <code>StringBuffer</code> .
    * 
    * @param buff the StringBuffer.
    * @param key the key
    * @param values the collection of values.
    */
   private static void appendWithParameters(StringBuffer buff, String key, List<String> values)
   {
      appendString(buff, key);
      if (values.size() > 0)
      {
         StringBuffer localBuff = new StringBuffer();
         buff.append('=');
         buff.append('"');
         for (String t : values)
         {
            appendString(localBuff, t);
         }
         buff.append(localBuff.toString());
         buff.append('"');
      }
   }

   /**
    * Add quotes to <code>String</code> if it consists whitespaces, otherwise
    * <code>String</code> will be returned without changes.
    * 
    * @param s the source string.
    * @return new string.
    */
   private static String addQuotes(String s)
   {
      Matcher macther = SPACES_PATTERN.matcher(s);
      if (macther.find())
      {
         return '"' + s + '"';
      }
      return s;
   }

}
