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

package org.exoplatform.services.rest.frameworks.json;

import org.exoplatform.services.rest.frameworks.json.utils.JSONUtils;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONUtilsTest extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testGetJSONString() {
    assertEquals(JSONUtils.getJSONString("string"), "\"string\"");
    assertEquals(JSONUtils.getJSONString("s\ntring\n"), "\"s\\ntring\\n\"");
    assertEquals(JSONUtils.getJSONString("s\tring"), "\"s\\tring\"");
    assertEquals(JSONUtils.getJSONString("st\ring"), "\"st\\ring\"");
    assertEquals(JSONUtils.getJSONString("str\\ing"), "\"str\\\\ing\"");
    assertEquals(JSONUtils.getJSONString("stri\"ng"), "\"stri\\\"ng\"");
    assertEquals(JSONUtils.getJSONString("stri/ng"), "\"stri/ng\"");
    int i = 0;
    for (char c = '\u0000'; c < '\u0020'; c++, i++) {
      System.out.print(JSONUtils.getJSONString(c + "") + " ");
      if (i > 10) {
        System.out.println();
        i = 0;
      }
    }
    for (char c = '\u0080'; c < '\u00a0'; c++, i++) {
      System.out.print(JSONUtils.getJSONString(c + " "));
      if (i > 10) {
        System.out.println();
        i = 0;
      }
    }
    for (char c = '\u2000'; c < '\u2100'; c++, i++) {
      System.out.print(JSONUtils.getJSONString(c + " "));
      if (i > 10) {
        System.out.println();
        i = 0;
      }
    }
  }
  
}

