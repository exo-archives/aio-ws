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

package org.exoplatform.ws.frameworks.json;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Book {
  
  private String author;
  private String title;
  private double price;
  private long isdn;
  private int pages;
  
  public void setAuthor(String s) {
    author = s;
  }
  public void setTitle(String s) {
    title = s;
  }
  public void setPrice(double d) {
    price = d;
  }
  public void setIsdn(long i) {
    isdn = i;
  }
  public void setPages(int i) {
    pages = i;
  }

  public String getAuthor() {
    return author;
  }
  public String getTitle() {
    return title;
  }
  public double getPrice() {
    return price;
  }
  public long getIsdn() {
    return isdn;
  }
  public int getPages() {
    return pages;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Book:{")
    .append("Author: ").append(author).append(" ")
    .append("Title: ").append(title).append(" ")
    .append("Pages: ").append(pages).append(" ")
    .append("Price: ").append(price).append(" ")
    .append("ISDN: ").append(isdn).append("} ");
    return sb.toString();
  }
  
  public boolean equals(Book book) {
    return book.getAuthor().equals(author)
      && book.getTitle().equals(title)
      && book.getIsdn() == isdn
      && book.getPages() == pages
      && book.getPrice() == price;
  }
}

