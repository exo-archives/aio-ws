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

package org.exoplatform.services.rest.impl.uri;

import org.exoplatform.services.rest.BaseTest;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UriComponentTest extends BaseTest {
  
  public void testCheckHexCharacters() {
    String str = "%20%23%a0%ag";
    assertTrue(UriComponent.checkHexCharacters(str, 0));
    assertFalse(UriComponent.checkHexCharacters(str, 1));
    assertTrue(UriComponent.checkHexCharacters(str, 3));
    assertTrue(UriComponent.checkHexCharacters(str, 6));
    assertFalse(UriComponent.checkHexCharacters(str, 9));
    assertFalse(UriComponent.checkHexCharacters(str, 11));
  }
  
  public void testEncodeDecode() {
    String str = "\u041f?\u0440#\u0438 \u0432\u0456\u0442";
    String estr = "%D0%9F%3F%D1%80%23%D0%B8%20%D0%B2%D1%96%D1%82";
    assertEquals(estr, UriComponent.encode(str, UriComponent.HOST, false));
    assertEquals(str, UriComponent.decode(estr, UriComponent.HOST));
    
    // wrong encoded string, near %9g
    String estr1 = "%D0%9g%3F%D1%80%23%D0%B8%20%D0%B2%D1%96%D1%82";
    try {
      UriComponent.decode(estr1, UriComponent.HOST);
      fail();
    } catch(IllegalArgumentException e) {
    }
    // wrong encoded string, end %8
    estr1 = "%D0%9F%3F%D1%80%23%D0%B8%20%D0%B2%D1%96%D1%8";
    try {
      UriComponent.decode(estr1, UriComponent.HOST);
      fail();
    } catch(IllegalArgumentException e) {
    }
  }

}
