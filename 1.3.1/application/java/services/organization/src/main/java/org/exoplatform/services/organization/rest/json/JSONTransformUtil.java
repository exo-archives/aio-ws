/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.User;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class JSONTransformUtil {
  /**
   * Class logger.
   */
  private final Log log = ExoLogger.getLogger("ws.JSONTransformUtil");
  
  /**
   * @param memmbershipCollection collection of org.exoplatform.services.organization.Membership
   * @return Memberships
   */
  public static Memberships collectionMembershipToMemberships(Collection<Membership> memmbershipCollection) {
    return new Memberships(memmbershipCollection); 
  }
  
  /**
   * @param membershipTypesCollection org.exoplatform.services.organization.MembershipType
   * @return MembershipTypes 
   */
  public static MembershipTypes collectionMembershipTypesToMembershipTypes(Collection<MembershipType> membershipTypesCollection) {
    return new MembershipTypes(membershipTypesCollection); 
  }
  
  
  /**
   * @param userCollection collection org.exoplatform.services.organization.User 
   * @return Users
   */
  public static Users colectionUserToUsers(Collection<User> userCollection) {
    return new Users(userCollection);
  }
  
  /**
   * @param groupCollection org.exoplatform.services.organization.Group
   * @return groups
   */
  public static Groups collectionGroupToGroups(Collection<Group> groupCollection) {
    return new Groups(groupCollection);
  }
}
