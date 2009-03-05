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
package org.exoplatform.services.organization.rest.xml;

import generated.Group;
import generated.GroupInfo;
import generated.Groups;
import generated.Members;
import generated.Membership;
import generated.MembershipType;
import generated.MembershipTypes;
import generated.Memberships;
import generated.Users;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.impl.GroupImpl;
import org.exoplatform.services.organization.impl.MembershipImpl;
import org.exoplatform.services.organization.impl.UserImpl;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class JAXBTransformUtil {
  
  
  /**
   * Transform org.exoplatform.services.organization.User to generated.User. 
   * @param user the user
   * @return generated.User
   */
  public static generated.User userToUserJAXB(User user) {
    generated.User userJAXB = new generated.User();
    userJAXB.setEmail(user.getEmail());
    userJAXB.setFirstName(user.getFirstName());
    userJAXB.setLastName(user.getLastName());
    userJAXB.setPassword(user.getPassword());
    userJAXB.setUserName(user.getUserName());
    return userJAXB;
  }
  
  /**
   * Transform generated.User to org.exoplatform.services.organization.User.
   * @param userJAXB the generated.User
   * @return user
   */
  public static User userJAXBToUser(generated.User userJAXB) {
    UserImpl user = new UserImpl();
    user.setEmail(userJAXB.getEmail());
    user.setFirstName(userJAXB.getFirstName());
    user.setLastName(userJAXB.getLastName());
    user.setPassword(userJAXB.getPassword());
    user.setUserName(userJAXB.getUserName());
    return user;
  }
  
  
  /**
   * Transform Collection of org.exoplatform.services.organization.User to generated.Users.
   * @param users collection of user
   * @return generate.Users
   */
  public static Users collectionUserToUsersJAXB(Collection<User> users) {
    Users usersJAXB = new Users();
    for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
      User user = iterator.next();
      usersJAXB.getUsers().add(userToUserJAXB(user));
    }
    return usersJAXB;
  }
  
  /**
   * Transform org.exoplatform.services.organization.Membership to generated.Membership.
   * @param membership org.exoplatform.services.organization.Membership
   * @return generated.Membership
   */
  public static Membership membershipToMembershipJAXB(org.exoplatform.services.organization.Membership membership) {
    Membership membershipJAXB = new Membership();
    membershipJAXB.setGroupId(membership.getGroupId());
    membershipJAXB.setId(membership.getId());
    membershipJAXB.setMembershipType(membership.getMembershipType());
    membershipJAXB.setUserName(membership.getUserName());
    return membershipJAXB;
  }
  
  /**
   * Transform generated.Membership to org.exoplatform.services.organization.Membership.
   * @param membershipJAXB generated.Membership
   * @return org.exoplatform.services.organization.Membership
   */
  public static org.exoplatform.services.organization.Membership membershipJAXBToMembership(Membership membershipJAXB) {
    MembershipImpl membership = new MembershipImpl();
    membership.setGroupId(membershipJAXB.getGroupId());
    membership.setId(membershipJAXB.getId());
    membership.setMembershipType(membershipJAXB.getMembershipType());
    membership.setUserName(membershipJAXB.getUserName());
    return membership;
  }
  
  /**
   * Transform Collection of org.exoplatform.services.organization.Membership to generated.Memberships.
   * @param collectionMembership Collection of org.exoplatform.services.organization.Membership
   * @return generated.Membership
   */
  public static Memberships collectionMembershipToMembershipsJAXB(Collection<org.exoplatform.services.organization.Membership> collectionMembership) {
    Memberships membershipsJAXB = new Memberships();
    for (Iterator<org.exoplatform.services.organization.Membership> iterator = collectionMembership.iterator(); iterator.hasNext();) {
      org.exoplatform.services.organization.Membership membership =  iterator.next();
      membershipsJAXB.getMemberships().add(membershipToMembershipJAXB(membership));
    }
    return membershipsJAXB;
  }
  
  /**
   * Transform org.exoplatform.services.organization.Group to generated.Group.
   * @param group org.exoplatform.services.organization.Group
   * @return generated.Group
   */
  public static Group groupToGroupJAXB(org.exoplatform.services.organization.Group group) {
    Group groupJAXB = new Group();
    groupJAXB.setDescription(group.getDescription());
    groupJAXB.setId(group.getId());
    groupJAXB.setLabel(group.getLabel());
    groupJAXB.setName(group.getGroupName());
    groupJAXB.setParentId(group.getParentId());
    return groupJAXB;
  }
  
  /**
   * Transform generated.Group to org.exoplatform.services.organization.Group.
   * @param groupJAXB generated.Group
   * @return org.exoplatform.services.organization.Group
   */
  public static org.exoplatform.services.organization.Group groupJAXBToGroup(Group groupJAXB) {
    GroupImpl group = new GroupImpl();
    group.setDescription(groupJAXB.getDescription());
    group.setId(groupJAXB.getId());
    group.setLabel(groupJAXB.getLabel());
    group.setGroupName(groupJAXB.getName());
    group.setParentId(groupJAXB.getParentId());
    return group;
  }
  
  /**
   * Transform Collection of org.exoplatform.services.organization.Group to generated.groups.
   * @param groups Collection of org.exoplatform.services.organization.Group
   * @return generated.Groups
   */
  public static Groups collectionGroupToGroupsJAXB(Collection<org.exoplatform.services.organization.Group> groups) {
    Groups groupsJAXB = new Groups();
    for (Iterator<org.exoplatform.services.organization.Group> iterator = groups.iterator(); iterator.hasNext();) {
      org.exoplatform.services.organization.Group group =  iterator.next();
      groupsJAXB.getGroups().add(groupToGroupJAXB(group));
    }
    return groupsJAXB;
  }
  
  /**
   * Transform org.exoplatform.services.organization.rest.GroupInfo to generated.GroupInfo.
   * @param groupInfo org.exoplatform.services.organization.rest.GroupInfo
   * @return generated.GroupInfo
   */
  public static GroupInfo groupInfoToGroupInfoJAXB(org.exoplatform.services.organization.rest.GroupInfo groupInfo) {
    GroupInfo groupInfoJAXB = new GroupInfo();
    groupInfoJAXB.setGroup(groupToGroupJAXB(groupInfo.getGroup()));
    Members members = new Members();
    Collection<User> users = groupInfo.getMembers();
    for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
      User user = iterator.next();
      members.getMembers().add(userToUserJAXB(user));
    }
    groupInfoJAXB.setMembers(members);
    return groupInfoJAXB;
  }
  
  /**
   * Transform org.exoplatform.services.organization.MembershipType to generated.MembershipType. 
   * @param membershipType org.exoplatform.services.organization.MembershipType
   * @return generated.MembershipType
   */
  public static MembershipType membershipTypeToMembershipTypeJAXB(org.exoplatform.services.organization.MembershipType  membershipType) {
    try {
      MembershipType membershipTypeJAXB = new MembershipType();
      GregorianCalendar gregorianCalendar = new GregorianCalendar();
      gregorianCalendar.setTime(membershipType.getCreatedDate());
      XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
      membershipTypeJAXB.setCreatedDate(calendar);
      gregorianCalendar.setTime(membershipType.getModifiedDate());
      calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
      membershipTypeJAXB.setModifiedDate(calendar);
      membershipTypeJAXB.setDescription(membershipType.getDescription());
      membershipTypeJAXB.setName(membershipType.getName());
      membershipTypeJAXB.setOwner(membershipType.getOwner());
      return membershipTypeJAXB;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Transform Collection of org.exoplatform.services.organization.MembershipType to generated.MembershipTypes.
   * @param membershipTypes Collection of org.exoplatform.services.organization.MembershipType
   * @return generated.MembershipTypes
   */
  public static MembershipTypes collectionMembershipTypesToMembershipTypesJAXB(Collection<org.exoplatform.services.organization.MembershipType> membershipTypes) {
    MembershipTypes membershipTypesJAXB = new MembershipTypes();
    for (Iterator<org.exoplatform.services.organization.MembershipType> iterator = membershipTypes.iterator(); iterator.hasNext();) {
      org.exoplatform.services.organization.MembershipType membershipType = iterator.next();
      membershipTypesJAXB.getMembershipTypes().add(membershipTypeToMembershipTypeJAXB(membershipType));
    }
    return membershipTypesJAXB;
  }
}
