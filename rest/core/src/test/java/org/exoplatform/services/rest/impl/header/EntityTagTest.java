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

package org.exoplatform.services.rest.impl.header;

import javax.ws.rs.core.EntityTag;

import org.exoplatform.services.rest.BaseTest;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EntityTagTest extends BaseTest {

  public void testToString() {
    EntityTag entityTag = new EntityTag("test", true);
    assertEquals("W/\"test\"", entityTag.toString());
    
    entityTag = new EntityTag("test \"test\"", true);
    assertEquals("W/\"test \\\"test\\\"\"", entityTag.toString());

    entityTag = new EntityTag("test \"test\"", false);
    assertEquals("\"test \\\"test\\\"\"", entityTag.toString());
  }
  
  public void testFromString() {
    String header = "W/\"test\"";
    EntityTag entityTag = EntityTag.valueOf(header);
    assertTrue(entityTag.isWeak());
    assertEquals("test", entityTag.getValue());

    header = "\"test\"";
    entityTag = EntityTag.valueOf(header);
    assertFalse(entityTag.isWeak());
    assertEquals("test", entityTag.getValue());
    
    header = "W/\"test \\\"test\\\"\"";
    entityTag = EntityTag.valueOf(header);
    assertTrue(entityTag.isWeak());
    assertEquals("test \"test\"", entityTag.getValue());
  }

}
