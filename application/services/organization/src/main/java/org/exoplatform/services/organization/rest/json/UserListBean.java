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
import java.util.Iterator;

import org.exoplatform.services.organization.User;

/**
 * Created by The eXo Platform SARL
 * Author : Volodymyr Krasnikov
 *          volodymyr.krasnikov@exoplatform.com.ua
 */

public class UserListBean {
  private Collection<User> users;

  public UserListBean() {}

  public UserListBean(Collection<User> userList) {
    this.users = userList;
  }

  public Collection<User> getUsers() {
    return users;
  }

  public void setUsers(Collection<User> userList) {
    this.users = userList;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null)
      return false;
    if(obj == this)
      return true;
    if(this.hashCode() == obj.hashCode())
      return true;
    if(obj instanceof UserListBean){
      UserListBean bean = (UserListBean)obj;
      if( users.size() == bean.users.size() ){
        Iterator<?> it_1 = users.iterator();
        Iterator<?> it_2 = users.iterator();
        while(it_1.hasNext()){
          Object m_obj1 = it_1.next();
          Object m_obj2 = it_2.next();
          if(! m_obj1.equals(m_obj2))
            return false;
        }
        return true;
      }
    }
    return false;
  }
  
  private int h_ = 0;
  @Override
  public int hashCode() {
    if( h_ == 0){
      Iterator<?> i = users.iterator();
      while(i.hasNext()){
        h_ = h_ + i.next().hashCode();
      }
    }
    return h_;
  }
  
}
