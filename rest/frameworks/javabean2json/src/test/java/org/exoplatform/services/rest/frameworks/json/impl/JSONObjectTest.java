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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.services.rest.frameworks.json.Bean;
import org.exoplatform.services.rest.frameworks.json.Item;
import org.exoplatform.services.rest.frameworks.json.impl.JSONObjectFactoryImpl;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONObjectTest extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testSimpleObject() throws Exception {
    Bean rootBean = new Bean();
    rootBean.setBoolean(true);
    rootBean.setByte((byte) 32);
    rootBean.setChar('c');
    rootBean.setInt(121);
    rootBean.setDouble(121.121);
    rootBean.setString("test_string");
    rootBean.setStringArray(new String[] {"item1", "item2", "item3", "item4", "item5"});
    // create child Bean
    Item childBean = new Item();
    childBean.setName("name");
    childBean.setValue("value");
    rootBean.setItem(childBean);
    System.out.println("------------------ with child bean -------------------");
    Map<String, Object> m = new JSONObjectFactoryImpl().createJSONObject(rootBean);
    Set<String>  keys = m.keySet();
    for (String key : keys)
      System.out.println(key + " : " + m.get(key));
  }

  public void testSimpleObject2() throws Exception {
    Bean bean = new Bean();
    bean.setBoolean(true);
    bean.setByte((byte) 32);
    bean.setChar('c');
    bean.setInt(121);
    bean.setDouble(121.121);
    bean.setString("test_string");
    bean.setStringArray(new String[] {"item1", "item2", "item3", "item4", "item5"});
    bean.setItem(null); 
    List<Item> l = new ArrayList<Item>();
    for (int i = 0; i < 5; i++) {
      Item item = new Item();
      item.setName("name" + i);
      item.setValue("value" + i);
      l.add(item);
    }
    bean.setItems(l);
    System.out.println("------------- with array of child bean --------------");
    Map<String, Object> m = new JSONObjectFactoryImpl().createJSONObject(bean);
    Set<String>  keys = m.keySet();
    for (String key : keys)
      System.out.println(key + " : " + m.get(key));
//    System.out.println(m);
  }
}
