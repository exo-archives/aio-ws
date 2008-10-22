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

import org.exoplatform.services.organization.MembershipType;

/**
 * Created by The eXo Platform SAS.
 * Author : Volodymyr Krasnikov
 *          volodymyr.krasnikov@exoplatform.com.ua
 */


/**
 * Wrapper for collection. JSON-bean framework can serialize it to json.
 */

public class MembershipTypes {
  
  /**
   * 
   */
  private Collection<MembershipType> membershipTypes;
  
  /**
   * 
   */
  public MembershipTypes() {
  }

  /**
   * @param membershipTypes the collection of org.exoplatform.services.organization.MembershipType
   */
  public MembershipTypes(Collection<MembershipType> membershipTypes) {
    super();
    this.membershipTypes = membershipTypes;
  }

  /**
   * @return the collection of org.exoplatform.services.organization.MembershipType
   */
  public Collection<MembershipType> getMembershipTypes() {
    return membershipTypes;
  }
  
  /**
   * @param value collection of org.exoplatform.services.organization.MembershipType
   */
  public void setMembershipTypes(Collection<MembershipType> value) {
    this.membershipTypes = value;
  }
  
}
