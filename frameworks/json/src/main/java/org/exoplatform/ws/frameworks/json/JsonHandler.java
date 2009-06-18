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

package org.exoplatform.ws.frameworks.json;

import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface JsonHandler {
  
  /**
   * This method will be called by JSONParser when '{' found.
   */
  void startObject();
  
  /**
   * This method will be called by JSONParser when '}' found.
   */
  void endObject();
  
  /**
   * This method will be called by JSONParser when '[' found.
   */
  void startArray();
  
  /**
   * This method will be called by JSONParser when ']' found.
   */
  void endArray();
  
  /**
   * The key name found in the input JSON stream.
   * @param key the key.
   */
  void key(String key);
  
  /**
   * Characters set found, it can be any characters. 
   * @param characters the array of characters.
   */
  void characters(char[] characters);

  /**
   * @return return Json Object.
   */
  JsonValue getJsonObject();
  
}

