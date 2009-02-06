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

package org.exoplatform.ws.security.oauth.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GrantedAccessorStorageProperties {

  /**
   * Properties.
   */
  private List<Property> properties;

  /**
   * Add supplied properties.
   * 
   * @param props list of properties to be added
   */
  public void setProperties(List<Property> props) {
    properties().addAll(props);
  }

  /**
   * Set single property with supplied name and value.
   * 
   * @param name property name
   * @param value property value
   */
  public void setProperty(String name, String value) {
    properties().add(new Property(name, value));
  }

  /**
   * Get property with supplied name.
   * 
   * @param name property name
   * @return property value or null if there is no property with specified name
   */
  public String getProperty(String name) {
    for (Property p : properties()) {
      if (p.getName().equals(name))
        return p.getValue();
    }

    return null;
  }

  /**
   * @return properties list
   */
  private List<Property> properties() {
    return properties == null ? properties = new ArrayList<Property>() : properties;
  }

  /**
   * Key/Value pair.
   */
  public static class Property {

    /**
     * Property name.
     */
    private String name;

    /**
     * Property value.
     */
    private String value;

    /**
     * Default constructor.
     */
    public Property() {
    }

    /**
     * @param name property name
     * @param value property value
     */
    public Property(String name, String value) {
      this.name = name;
      this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
      return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
      this.value = value;
    }
  }

}
