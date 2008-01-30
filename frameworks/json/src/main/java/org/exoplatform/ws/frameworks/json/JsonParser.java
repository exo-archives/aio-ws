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

import java.io.InputStream;
import java.io.Reader;

import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JsonParser {

  /**
   * Parse given character stream and build object.  
   * @param reader the Stream Reader.
   * @throws JsonException
   */
  void parse(Reader reader) throws JsonException;
  
  /**
   * Parse given character stream and build object.  
   * @param reader the Input Stream.
   * @throws JsonException
   */
  void parse(InputStream in) throws JsonException;

  /**
   * Set handler for JSON parser.
   * @param handler the JsonHandler.
   * @throws JsonNException.
   */
  void setHandler(JsonHandler handler) throws JsonException;
  
  /**
   * Get JsonHandler.
   * @return the JsonHandler.
   */
  JsonHandler getJsonHandler();
  
}

