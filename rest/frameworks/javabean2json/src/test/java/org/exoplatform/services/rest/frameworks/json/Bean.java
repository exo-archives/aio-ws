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

package org.exoplatform.services.rest.frameworks.json;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Bean {
    
    private boolean bool_;
    private byte byt_;
    private char ch_;
    private int i_;
    private double d_;
    private String string_;
    private String[] stringArray_;
    private Item bean_;
    private List<Item> items_;
    
    public Bean() {
    }
    
    public void setBoolean(boolean b) {
      bool_ = b;
    }
    public void setByte(byte b) {
      byt_ = b;
    }
    public void setChar(char c) {
      ch_ = c;
    }
    public void setInt(int i) {
      i_ = i;
    }
    public void setDouble(double d) {
      d_ = d;
    }
    public void setString(String s) {
      string_ = s;
    }
    public void setStringArray(String[] s) {
      stringArray_ = s;
    }
    public void setBean(Item bean) {
      bean_ = bean;
    }
    public void setItems(List<Item> i) {
      items_ = i;
    }
    
    public boolean getBoolean() {
      return bool_;
    }
    public byte getByte() {
      return byt_;
    }
    public char getChar() {
      return ch_;
    }
    public int getInt() {
      return i_;
    }
    public double getDouble() {
      return d_;
    }
    public String getString() {
      return string_;
    }
    public String[] getStringArray() {
      return stringArray_;
    }
    public Item getBean() {
      return bean_;
    }
    public List<Item> getItems() {
      return items_;
    }

}
