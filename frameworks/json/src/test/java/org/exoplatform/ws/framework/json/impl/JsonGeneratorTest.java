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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ws.framework.json.Book;
import org.exoplatform.ws.framework.json.BookStorage;
import org.exoplatform.ws.framework.json.BookWrapper;
import org.exoplatform.ws.framework.json.BookWrapper2;
import org.exoplatform.ws.framework.json.BookWrapper3;
import org.exoplatform.ws.framework.json.JsonWriter;
import org.exoplatform.ws.framework.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.framework.json.impl.JsonWriterImpl;
import org.exoplatform.ws.framework.json.value.JsonValue;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonGeneratorTest extends TestCase {

  private JsonWriter jsonWriter_;
  private ByteArrayOutputStream out_;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    out_ = new ByteArrayOutputStream();
    jsonWriter_ = new JsonWriterImpl(out_);
  }
  
  public void testSimpleObject() throws Exception {
    Book book = new Book();
    book.setAuthor("Vincent Masson");
    book.setTitle("JUnit in Action");
    book.setPages(386);
    book.setPrice(19.37);
    book.setIsdn(930110995);
    JsonValue jv = new JsonGeneratorImpl().createJsonObject(book);
    jv.writeTo(jsonWriter_);
    jsonWriter_.flush();
    jsonWriter_.close();
  }
  
  public void testSimpleObject2() throws Exception {
    Book book = new Book();
    book.setAuthor("Vincent Masson");
    book.setTitle("JUnit in Action");
    book.setPages(386);
    book.setPrice(19.37);
    book.setIsdn(930110995);
    BookWrapper bookWrapper = new BookWrapper();
    bookWrapper.setBook(book);
    JsonValue jv = new JsonGeneratorImpl().createJsonObject(bookWrapper);
    System.out.println(jv);
    // more includes
    BookWrapper2 bookWrapper2 = new BookWrapper2();
    bookWrapper2.setBookWrapper(bookWrapper);
    jv = new JsonGeneratorImpl().createJsonObject(bookWrapper2);
    System.out.println(jv);
    // more more includes
    BookWrapper3 bookWrapper3 = new BookWrapper3();
    bookWrapper3.setBookWrapper2(bookWrapper2);
    jv = new JsonGeneratorImpl().createJsonObject(bookWrapper3);
    System.out.println(jv);
  }

  public void testSimpleObject3() throws Exception {
    Book book = new Book();
    book.setAuthor("Vincent Masson");
    book.setTitle("JUnit in Action");
    book.setPages(386);
    book.setPrice(19.37);
    book.setIsdn(93011099534534L);
    List<Book> l = new ArrayList<Book>();
    l.add(book);
    book = new Book();
    book.setAuthor("Christian Gross");
    book.setTitle("Beginning C# 2008 from novice to professional");
    book.setPages(511);
    book.setPrice(23.56);
    book.setIsdn(9781590598696L);
    l.add(book);
    book = new Book();
    book.setAuthor("Chuck Easttom");
    book.setTitle("Advanced JavaScript, Third Edition");
    book.setPages(617);
    book.setPrice(25.99);
    book.setIsdn(9781598220339L);
    l.add(book);
    BookStorage bookViewer = new BookStorage();
    bookViewer.setBooks(l);
    JsonValue jv = new JsonGeneratorImpl().createJsonObject(bookViewer);
    jv.writeTo(jsonWriter_);
    jsonWriter_.flush();
    jsonWriter_.close();
    System.out.println(new String(out_.toByteArray()));
  }

}
