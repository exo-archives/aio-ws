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

package org.exoplatform.services.rest.frameworks.json.impl;

import java.io.ByteArrayOutputStream;

import org.exoplatform.services.rest.frameworks.json.impl.JSONWriterImpl;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONWriterTest extends TestCase {
  
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testJSONWriter() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JSONWriterImpl jsw = new JSONWriterImpl(out);
    String key = "key";
    String value = "value";

    jsw.writeStartObject();
    jsw.writeKey(key + "_top");
    jsw.writeStartObject();
    for (int i = 0; i <= 5; i++) {
      jsw.writeKey(key + i);
      jsw.writeString(value + i);
    }
    jsw.writeKey(key + "_inner_top");
    jsw.writeStartObject();
    jsw.writeKey("string");
    jsw.writeString("string");
    jsw.writeKey("_null");
    jsw.writeNull();
    jsw.writeKey("boolean");
    jsw.writeValue(true);
    jsw.writeKey("long");
    jsw.writeValue(121);
    jsw.writeKey("double");
    jsw.writeValue(121.121);
    jsw.writeEndObject();
    jsw.writeEndObject();
    jsw.writeKey(key + "_array");
    jsw.writeStartArray();
    for (int i = 0; i <= 5; i++)
      jsw.writeString(value + i);
    jsw.writeEndArray();
    jsw.writeEndObject();
    jsw.flush();
    jsw.close();
    System.out.println(new String(out.toByteArray()));
  }

}
