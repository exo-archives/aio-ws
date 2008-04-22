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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */

import junit.framework.TestCase;
import org.exoplatform.services.rest.data.HeaderUtils;

public class HeaderUtilTest extends TestCase {

  public void testHeaderNoremalizeAcceptString() {
    String t = HeaderUtils
        .normalizeAccepString("text/plain;    level= 2;  q=0.8,text/xml;   q=0.9");
    assertEquals("text/plain;level=2;q=0.8,text/xml;q=0.9", t);
  }

  public void testHeaderParse() {
    String[] acc = HeaderUtils
        .parse("image/jpeg,  text/xml  ;level=1;            q=0.7,"
            + "text/plain;          q=0.95, text/html;q=0.8,text/x-c; q=0.75,"
            + "                    text/xbel+xml;   q=0.9");
    assertEquals("image/jpeg", acc[0]);
    assertEquals("text/plain", acc[1]);
    assertEquals("text/xbel+xml", acc[2]);
    assertEquals("text/html", acc[3]);
    assertEquals("text/x-c", acc[4]);
    assertEquals("text/xml;level=1", acc[5]);
  }

}
