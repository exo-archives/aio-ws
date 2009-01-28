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

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MultivaluedMetadataTest extends TestCase {

  public void testMetadataLang() {
    MultivaluedMetadata md = new MultivaluedMetadata();
    List<String> langs = new ArrayList<String>();
    langs.add("en");
    langs.add("ru");
    langs.add("da");
    langs.add("de");
    md.put("Content-Language", langs);
    assertEquals("en, ru, da, de", md.getAll().get("content-language"));
  }

  public void testMetadataEncod() {
    MultivaluedMetadata md = new MultivaluedMetadata();
    List<String> encs = new ArrayList<String>();
    encs.add("compress;q=0.5");
    encs.add("gzip;q=1.0");
    md.put("Content-Encoding", encs);
    assertEquals("compress;q=0.5, gzip;q=1.0", md.getAll().get(
        "content-encoding"));
  }

}
