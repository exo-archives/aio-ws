/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.organization.rest.json;

import java.util.Collection;

import org.exoplatform.services.organization.User;

/**
 * Created by The eXo Platform SAS.
 * Author : Volodymyr Krasnikov
 *          volodymyr.krasnikov@exoplatform.com.ua
 */

/**
 * Wrapper for collection. JSON-bean framework can serialize it to json.
 */

public class Users {
  /**
   * 
   */
  private Collection<User> users;
  
  /**
   * 
   */
  public Users() {
  }

  /**
   * @param users the collection of org.exoplatform.services.organization.User;
   */
  public Users(Collection<User> users) {
    this.users = users;
  }

  /**
   * @return the collection of org.exoplatform.services.organization.User
   */
  public Collection<User> getUsers() {
    return users;
  }

  /**
   * @param value the collection of org.exoplatform.services.organization.User
   */
  public void setUsers(Collection<User> value) {
    this.users = value;
  }
  
}
