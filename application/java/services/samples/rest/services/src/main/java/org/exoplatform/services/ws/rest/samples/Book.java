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
  
  /**
   * Title.
   */
  private String title;
  
  /**
   * Author. 
   */
  private String author;
  
  /**
   * Number of pages.
   */
  private int pages;
  
  /**
   * Price.
   */
  private double price;
  
  /**
   * ISDN.
   */
  private String isdn;
  
  /**
   * @return author name.
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Set author name.
   * @param author author name.
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * @return ISDN.
   */
  public String getIsdn() {
    return isdn;
  }

  /**
   * Set ISDN.
   * @param isdn ISDN.
   */
  public void setIsdn(String isdn) {
    this.isdn = isdn;
  }

  /**
   * @return number of pages.
   */
  public int getPages() {
    return pages;
  }

  /**
   * Set number of pages.
   * @param pages number of pages.
   */
  public void setPages(int pages) {
    this.pages = pages;
  }

  /**
   * @return price.
   */
  public Double getPrice() {
    return price;
  }

  /**
   * Set book's price.
   * @param price price.
   */
  public void setPrice(Double price) {
    this.price = price;
  }

  /**
   * @return book's title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Set book's title.
   * @param title title.
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * {@inheritDoc}
   */
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
}
