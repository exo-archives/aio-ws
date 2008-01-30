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

package org.exoplatform.ws.framework.json.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.exoplatform.ws.framework.json.impl.JsonParserImpl;
import org.exoplatform.ws.framework.json.value.JsonValue;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JavaNumericTypeTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testLong() throws Exception {
    JsonParserImpl jsonParser = new JsonParserImpl();
    String jsonString = "{" +
    		"\"long\":[" +
    		"1, 0xAA, 077, 123, 32765, 77787, 123456789," +
    		"0x123456, 0123456, -2387648, -123456789" +
    		"]" +
    		"}";
    
    jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(jsonString.getBytes())));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    assertTrue(jv.getElement("long").isArray());
    Iterator<JsonValue> values = jv.getElement("long").getElements();
    while (values.hasNext()) {
      JsonValue v = values.next(); 
      assertTrue(v.isNumeric());
      assertTrue(v.isLong());
      assertFalse(v.isDouble());
      System.out.print((v.getLongValue() - 1) + ", ");
    }
    System.out.println();
  }
  
  public void testDouble() throws Exception {
    JsonParserImpl jsonParser = new JsonParserImpl();
    String jsonString = "{" +
            "\"double\":[" +
            "1.0, 0.0006382746, 111111.2222222, 9999999999999.9999999999999," +
            "9827394873249.8, 1.23456789E8, 123456.789E8, 3215478352478651238.0," +
            "982.8, 0.00000000000023456789E8, 1.789E8, 0.0000000000000000000321547835247865123," +
            "982.8, -0.00000000000023456789E8, -1.789E-8, -0.0000000000000000000321547835247865123" +
            "]" +
            "}";
    
    jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(jsonString.getBytes())));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    assertTrue(jv.getElement("double").isArray());
    Iterator<JsonValue> values = jv.getElement("double").getElements();
    while (values.hasNext()) {
      JsonValue v = values.next(); 
      assertTrue(v.isNumeric());
      assertFalse(v.isLong());
      assertTrue(v.isDouble());
      System.out.print(v.getDoubleValue() + ", ");
    }
    System.out.println();
  }

}

