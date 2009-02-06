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

package org.exoplatform.services.ws.rest.samples;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple service for REST demo.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BookStorage {
  
  /**
   * Keeps books.
   */
  private Map<String, Book> books = new HashMap<String, Book>();
  
  /**
   * Construct new instance of BookStorage.
   */
  public BookStorage() {
    init();
  }

  /**
   * Initialize BookStorgae.
   */
  private void init() {
    Book book = new Book();
    book.setTitle("JUnit in Action");
    book.setAuthor("Vincent Masson");
    book.setPages(386);
    book.setPrice(19.37);
    book.setIsdn("1234567890");
    books.put("1234567890", book);
  }
  
  /**
   * Get Book with specified key.
   * @param key the key.
   * @return Book.
   */
  public Book getBook(String key) {
    return books.get(key); 
  }
  
  /**
   * Add new Book in storage.
   * @param key the key.
   * @param book the book.
   */
  public void addBook(String key, Book book) {
    books.put(key, book);
  }
  
  /**
   * @return BookStorage size.
   */
  public int numberOfBooks() {
    return books.size();
  }
}
