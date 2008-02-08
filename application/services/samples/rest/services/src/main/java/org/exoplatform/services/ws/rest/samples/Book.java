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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Book {
  
  private String title_;
  private String author_;
  private int pages_;
  private double price_;
  private String isdn_;
  
  public String getAuthor() {
    return author_;
  }

  public void setAuthor(String author) {
    this.author_ = author;
  }

  public String getIsdn() {
    return isdn_;
  }

  public void setIsdn(String isdn) {
    this.isdn_ = isdn;
  }

  public int getPages() {
    return pages_;
  }

  public void setPages(int pages) {
    this.pages_ = pages;
  }

  public Double getPrice() {
    return price_;
  }

  public void setPrice(Double price) {
    this.price_ = price;
  }

  public String getTitle() {
    return title_;
  }

  public void setTitle(String title) {
    this.title_ = title;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Book:{")
    .append("Author: ").append(author_).append(" ")
    .append("Title: ").append(title_).append(" ")
    .append("Pages: ").append(pages_).append(" ")
    .append("Price: ").append(price_).append(" ")
    .append("ISDN: ").append(isdn_).append("} ");
    return sb.toString();
  }
}
