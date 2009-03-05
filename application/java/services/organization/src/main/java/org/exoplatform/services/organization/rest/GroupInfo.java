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

package org.exoplatform.services.organization.rest;

import java.util.Collection;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.User;

/**
 * Created by The eXo Platform SARL.
 * Author : Volodymyr Krasnikov
 *          volodymyr.krasnikov@exoplatform.com.ua
 */

public class GroupInfo {
 
  /**
   * 
   */
  private Group group;
  
  /**
   * 
   */
  private Collection<User>members;
  
  /**
   * Default constructor.
   */
  public GroupInfo() {
  }
  
  
  /**
   * @param group the Group 
   * @param members the members of group 
   */
  public GroupInfo(Group group, Collection<User> members) {
    super();
    this.group = group;
    this.members = members;
  }

  /**
   * @return the group 
   */
  public Group getGroup() {
    return group;
  }
  
  /**
   * @param value the group
   */
  public void setGroup(Group value) {
    this.group = value;
  }
  
  /**
   * @return the members of group 
   */
  public Collection<User> getMembers() {
    return members;
  }
  
  /**
   * @param value the members of group
   */
  public void setMembers(Collection<User> value) {
    this.members = value;
  }
  
}
