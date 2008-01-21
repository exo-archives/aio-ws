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

package org.exoplatform.ws.framework.json.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.exoplatform.ws.framework.json.Book;
import org.exoplatform.ws.framework.json.BookStorage;
import org.exoplatform.ws.framework.json.BookStorage2;
import org.exoplatform.ws.framework.json.JavaCollectionBean;
import org.exoplatform.ws.framework.json.JavaMapBean;
import org.exoplatform.ws.framework.json.JsonParser;
import org.exoplatform.ws.framework.json.impl.BeanBuilder;
import org.exoplatform.ws.framework.json.impl.JsonParserImpl;
import org.exoplatform.ws.framework.json.value.JsonValue;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonParserTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testCollection() throws Exception {
    // test restore Collection of standard Java Object from JSON source
    System.out.println("--------------------Simple Collection--------------------");
    JsonParserImpl jsonParser = new JsonParserImpl();
    String jsonString = "{"
        + "\"strings\":[\"JUnit in Action\",\"Advanced JavaScript\",\"Beginning C# 2008 \"]," 
        + "\"chars\":[\"b\",\"o\",\"o\",\"k\"]," 
        + "\"integers\":[386, 421, 565]"
        + "}";
    jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(jsonString
        .getBytes())));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    Object o = new BeanBuilder().createObject(JavaCollectionBean.class, jv);
    System.out.println("Strings:      " + ((JavaCollectionBean) o).getStrings());
    System.out.println("Characters:   " + ((JavaCollectionBean) o).getChars());
    System.out.println("Integers:     " + ((JavaCollectionBean) o).getIntegers());
    // more testing for other type of Collection with custom object
  }
  
  public void testCollection2() throws Exception {
    // test restore Collection of other Java Object from JSON source
    System.out.println("---------------------Other Collection---------------------");
    JsonParserImpl jsonParser = new JsonParserImpl();
    // check restore different type of Collection
    jsonParser.parse(new InputStreamReader(Thread.currentThread()
        .getContextClassLoader().getResourceAsStream("CollectionTest.txt")));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    Object o = new BeanBuilder().createObject(JavaCollectionBean.class, jv);
    System.out.println("\tArrayList:\n" + ((JavaCollectionBean) o).getArrayList());
    System.out.println("\tVector:\n" + ((JavaCollectionBean) o).getVector());
    System.out.println("\tLinkedList:\n" + ((JavaCollectionBean) o).getLinkedList());
    System.out.println("\tLinkedHashSet:\n" + ((JavaCollectionBean) o).getLinkedHashSet());
    System.out.println("\tHashSet:\n" + ((JavaCollectionBean) o).getHashSet());
    System.out.println("\tList:\n" + ((JavaCollectionBean) o).getList());
    System.out.println("\tSet:\n" + ((JavaCollectionBean) o).getSet());
    System.out.println("\tQueue:\n" + ((JavaCollectionBean) o).getQueue());
    System.out.println("\tArray[Object]:\n");
    for (Book i : ((JavaCollectionBean) o).getArray())
      System.out.print(i);
  }
  
  public void testMap() throws Exception {
    System.out.println("-----------------------Simple Map-----------------------");
    JsonParserImpl jsonParser = new JsonParserImpl();
    String jsonString = "{"
            + "\"strings\":{"
            + "\"book\":\"Beginning C# 2008 \","
            + "\"author\":\"Christian Gross\"" + "},"
            + "\"integers\":{"
            + "\"one\":11111,"
            + "\"two\":22222,"
            + "\"three\":33333" + "},"
            + "\"booleans\":{"
            + "\"true\":true,"
            + "\"false\":false" + "}"
            + "}";
    jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(jsonString.getBytes())));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    Object o = new BeanBuilder().createObject(JavaMapBean.class, jv);
    System.out.println("Strings:     " + ((JavaMapBean)o).getStrings());
    System.out.println("Integers:    " + ((JavaMapBean)o).getIntegers());
    System.out.println("Booleans:    " + ((JavaMapBean)o).getBooleans());
  }

  public void testMap2() throws Exception {
    System.out.println("---------------------Other Map---------------------------");
    JsonParserImpl jsonParser = new JsonParserImpl();
    jsonParser.parse(new InputStreamReader(Thread.currentThread()
        .getContextClassLoader().getResourceAsStream("MapTest.txt")));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    Object o = new BeanBuilder().createObject(JavaMapBean.class, jv);
    System.out.println("\tMap:\n" + ((JavaMapBean)o).getMap());
    System.out.println("\tHashMap:\n" + ((JavaMapBean)o).getHashMap());
    System.out.println("\tHashtable:\n" + ((JavaMapBean)o).getHashtable());
    System.out.println("\tLinkedHashMap:\n" + ((JavaMapBean)o).getLinkedHashMap());
  }
  
  public void testBean() throws Exception {
    System.out.println("------------------------Bean----------------------------");
    JsonParserImpl jsonParser = new JsonParserImpl();
    jsonParser.parse(new InputStreamReader(Thread.currentThread()
        .getContextClassLoader().getResourceAsStream("BookStorage.txt")));
    JsonValue jv = jsonParser.getJsonHandler().getJsonObject();
    Object o = new BeanBuilder().createObject(BookStorage.class, jv);
    System.out.println(((BookStorage)o).toString());
  }
  
  public void testMultiDimensionArray() throws Exception {
    System.out.println("-----------------Multi-Dimension Array------------------");
    JsonParser jsonParser = new JsonParserImpl();
    jsonParser.parse(new InputStreamReader(Thread.currentThread()
        .getContextClassLoader().getResourceAsStream("MultiDimension.txt")));
    JsonValue jsonValue = jsonParser.getJsonHandler().getJsonObject();
    System.out.println(jsonValue);
    Object o = new BeanBuilder().createObject(BookStorage2.class, jsonValue);
    Book[][][] array = ((BookStorage2)o).getBooks();
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        for (int k = 0; k < array[i][j].length; k++) {
          if (k > 0)
            System.out.print(",");
          System.out.print(array[i][j][k]);
        }
        System.out.println();
      }
      System.out.println();
    }
  }
  
}
