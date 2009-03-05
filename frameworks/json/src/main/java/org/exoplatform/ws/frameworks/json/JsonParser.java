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

import org.exoplatform.ws.frameworks.json.impl.JsonException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JsonParser {

  /**
   * Parse given character stream and build object.  
   * @param reader the Stream Reader.
   * @param handler JsonHandler, @see {@link JsonHandler}.
   * @throws JsonException if any error occurs during parsing.
   */
  void parse(Reader reader, JsonHandler handler) throws JsonException;
  
  /**
   * Parse given character stream and build object.  
   * @param in the Input Stream.
   * @param handler JsonHandler, @see {@link JsonHandler}.
   * @throws JsonException if any error occurs during parsing.
   */
  void parse(InputStream in, JsonHandler handler) throws JsonException;

}

