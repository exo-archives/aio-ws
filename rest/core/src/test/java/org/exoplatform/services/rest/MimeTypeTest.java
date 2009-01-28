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

import junit.framework.TestCase;
import org.exoplatform.services.rest.data.MimeTypes;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MimeTypeTest extends TestCase {

  MimeTypes mt;

  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() {
    mt = new MimeTypes("image/jpeg;q=0.8,image/gif;q=.7,image/png;q=.9");
  }

  public void testMimeTypes() {
    assertTrue(mt.hasMimeType("image/jpeg"));
    assertTrue(mt.hasMimeType("image/gif"));
    assertTrue(mt.hasMimeType("image/png"));
    assertFalse(mt.hasMimeType("image/jpg"));
    assertEquals("image/png, image/jpeg, image/gif", mt.toString());
  }

}
