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

import org.exoplatform.services.rest.frameworks.json.Item;
import org.exoplatform.services.rest.frameworks.json.impl.JSONArrayFactoryImpl;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONArrayTest extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testSimpleList() throws Exception {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < 5; i++)
      list.add("test_json_array" + i);
    System.out.println("---------------- simple list ------------------");
    System.out.println(new JSONArrayFactoryImpl().createJSONArray(list));
  }
  
  public void testBeanList() throws Exception {
    List<Item> list = new ArrayList<Item>();
    for (int i = 0; i < 5; i++) {
      Item item = new Item();
      item.setName("name" + i);
      item.setValue("value" + i);
      list.add(item);
    }
    System.out.println("---------------- list of bean -----------------");
    System.out.println(new JSONArrayFactoryImpl().createJSONArray(list));
  }
  
  public void testListList() throws Exception {
    List<List<String>> list = new ArrayList<List<String>>();
    for (int i = 0; i < 5; i++) {
      List<String> inner = new ArrayList<String>();
      inner.add("test_json_array" + i);
      inner.add("test_json_array" + i);
      list.add(inner);
    }
    System.out.println("---------------- list of list -----------------");
    System.out.println(new JSONArrayFactoryImpl().createJSONArray(list));
  }
  
  public void testListListBean() throws Exception {
    List<List<Item>> list = new ArrayList<List<Item>>();
    for (int i = 0; i < 5; i++) {
      List<Item> inner = new ArrayList<Item>();
      for (int j = 0; j < 5; j++) {
        Item item1 = new Item();
        item1.setName("name" + j);
        item1.setValue("value" + j);
        inner.add(item1);
      }
      list.add(inner);
    }
    System.out.println("------------- list of beans list --------------");
    System.out.println(new JSONArrayFactoryImpl().createJSONArray(list));
  }

}
