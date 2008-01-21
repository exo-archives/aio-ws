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

package org.exoplatform.ws.framework.json;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JavaMapBean {
  // interface
  private Map<String, Book> map_items;
  
  private HashMap<String, Book> hashMap_items;
  private Hashtable<String, Book> hashtable_items;
  private LinkedHashMap<String, Book> linkedHashMap_items;
  
  private Map<String, String> map_strings;
  private Map<String, Integer> map_integers;
  private Map<String, Boolean> map_booleans;
  
  // set methods
  public void setStrings(Map<String, String> m) {
    map_strings = m;
  }
  public void setIntegers(Map<String, Integer> m) {
    map_integers = m;
  }
  public void setBooleans(Map<String, Boolean> m) {
    map_booleans = m;
  }
  ///////////////////////
  public void setMap(Map<String, Book> m) {
    map_items = m;
  }
  public void setHashMap(HashMap<String, Book> m) {
    hashMap_items = m;
  }
  public void setHashtable(Hashtable<String, Book> m) {
    hashtable_items = m;
  }
  public void setLinkedHashMap(LinkedHashMap<String, Book> m) {
    linkedHashMap_items = m;
  }
  // get methods
  public Map<String, String> getStrings() {
    return map_strings;
  }
  public Map<String, Integer> getIntegers() {
    return map_integers;
  }
  public Map<String, Boolean> getBooleans() {
    return map_booleans;
  }
  ///////////////////////
  public Map<String, Book> getMap() {
    return map_items;
  }
  public HashMap<String, Book> getHashMap() {
    return hashMap_items;
  }
  public Hashtable<String, Book> getHashtable() {
    return hashtable_items;
  }
  public LinkedHashMap<String, Book> getLinkedHashMap() {
    return linkedHashMap_items;
  }

}