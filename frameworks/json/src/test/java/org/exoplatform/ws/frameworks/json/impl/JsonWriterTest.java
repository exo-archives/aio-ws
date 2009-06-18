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

package org.exoplatform.ws.frameworks.json.impl;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class JsonWriterTest extends TestCase {
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testJSONWriter() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonWriterImpl jsw = new JsonWriterImpl(out);
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
    jsw.writeKey(key + "_string");
    jsw.writeString("string");
    jsw.writeKey(key + "_null");
    jsw.writeNull();
    jsw.writeKey(key + "_boolean");
    jsw.writeValue(true);
    jsw.writeKey(key + "_long");
    jsw.writeValue(121);
    jsw.writeKey(key + "_double");
    jsw.writeValue(121.121);
    jsw.writeEndObject();
    jsw.writeEndObject();
    jsw.writeKey(key + "_array");
    jsw.writeStartArray();
    for (int i = 0; i <= 5; i++)
      jsw.writeString(value + i);
//    try {
//      jsw.writeEndObject();
//      fail("JsonException should be here.");
//    } catch(JsonException e) {}
    jsw.writeEndArray();
    jsw.writeEndObject();
    jsw.flush();
    jsw.close();
    System.out.println(new String(out.toByteArray()));
  }

}
