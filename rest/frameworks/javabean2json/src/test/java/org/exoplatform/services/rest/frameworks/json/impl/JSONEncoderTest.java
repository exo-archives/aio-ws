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
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.rest.frameworks.json.Bean;
import org.exoplatform.services.rest.frameworks.json.Item;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONEncoderTest extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testJSONEncoder() throws Exception {
    Bean rootBean = new Bean();
    rootBean.setBoolean(true);
    rootBean.setDouble(123.456);
    rootBean.setInt(123);
    rootBean.setChar('a');
    rootBean.setStringArray(new String[] {"1", "2", "3", "4", "5"});
    rootBean.setByte((byte) 32);
    rootBean.setString("root_bean_string");
    List<Item> l = new ArrayList<Item>();
    for (int i = 0; i < 5; i++) {
      Item item = new Item();
      item.setName("name" + i);
      item.setValue("value" + i);
      l.add(item);
    }
    rootBean.setItems(l);
    rootBean.setBean(new Item());
    System.out.println("------------- JSONEncoderTest -------------");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONEncoderImpl(out).writeObject(rootBean);
    System.out.println(new String(out.toByteArray()));
  }

}

