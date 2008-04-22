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
import java.util.Iterator;
import java.util.Set;
import java.util.List;

/**
 * Created by The eXo Platform SAS .<br/> A map of key-value pair. Each key
 * can have few value.<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class MultivaluedMetadata {

  private HashMap<String, List<String>> data;

  public MultivaluedMetadata() {
    data = new HashMap<String, List<String>>();
  }

  public MultivaluedMetadata(HashMap<String, List<String>> data) {
    this.data = data;
  }

  /**
   * Set key's value to one item list of values.
   * @param key the key
   * @param vals the value
   */
  public void put(String key, List<String> vals) {
    data.put(key.toLowerCase(), vals);
  }

  /**
   * Add one more value to the current list of values.
   * @param key the key
   * @param value the value to be add
   */
  public void putSingle(String key, String value) {
    List<String> vals = data.get(key.toLowerCase());
    if (vals == null) {
      vals = new ArrayList<String>();
    }
    vals.add(value);
    put(key.toLowerCase(), vals);
  }

  /**
   * Return the first value of the key.
   * @param key the key
   * @return the value
   */
  public String getFirst(String key) {
    List<String> vals = data.get(key.toLowerCase());
    if (vals == null || vals.size() == 0) {
      return null;
    }
    return vals.get(0);
  }

  /**
   * Return the all pair key-values and values convert from List &lt;String&gt;
   * to the String. This may be used to set HTTP headers.
   * @return key-valies pair represet by HashMap
   */
  public HashMap<String, String> getAll() {
    HashMap<String, String> h = new HashMap<String, String>();
    Set<String> keys = data.keySet();
    Iterator<String> ikeys = keys.iterator();
    while (ikeys.hasNext()) {
      String key = ikeys.next();
      List<String> vals = data.get(key);
      if (vals != null) {
        h.put(key, convertToString(vals));
      }
    }
    return h;
  }

  /**
   * Return values and values convert from List&lt;String&gt; to the String.
   * This may be used to set HTTP headers.
   * @param key the key
   * @return the value
   */
  public String get(String key) {
    List<String> vals = getList(key.toLowerCase());
    if (vals != null) {
      return convertToString(vals);
    }
    return null;
  }

  /**
   * Return values represented by List&lt;String&gt;.
   * @param key the key
   * @return the value
   */
  public List<String> getList(String key) {
    return data.get(key.toLowerCase());
  }

  /**
   * Get keys of HashMap.
   * @return keys.
   */
  public Set<String> keys() {
    return data.keySet();
  }

  private String convertToString(List<String> list) {
    if (list.size() == 0) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    for (String t : list) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(t);
    }
    return sb.toString();
  }
}
