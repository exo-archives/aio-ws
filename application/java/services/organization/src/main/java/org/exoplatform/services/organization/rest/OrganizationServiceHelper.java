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

package org.exoplatform.services.organization.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.MembershipTypeHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OrganizationServiceHelper {

  /**
   * Logger.
   */
  protected static final Log LOG = ExoLogger.getLogger("ws.OrganizationServiceHelper");

  /**
   * 
   */
  private final GroupHandler groupHandler;
  
  /**
   * 
   */
  private final MembershipHandler membershipHandler;
  
  /**
   * 
   */
  private final MembershipTypeHandler membershipTypeHandler;
  
  /**
   * 
   */
  private final UserHandler userHandler;
  
  /**
   * @param organizationService implementation of OrganizationService
   */
  public OrganizationServiceHelper(OrganizationService organizationService) {
    groupHandler = organizationService.getGroupHandler();
    userHandler = organizationService.getUserHandler();
    membershipHandler = organizationService.getMembershipHandler();
    membershipTypeHandler = organizationService.getMembershipTypeHandler();
  }
  
  /**
   * @param parentId id of parent Node
   * @param groupName group name
   * @param label group label
   * @param description group description
   * @return new Group
   */
  public Group createGroup(String parentId, String groupName, String label, String description) {
    try {
      Group parent = null;
      if (parentId != null && parentId.length() > 0) {
        parentId = parentId.startsWith("/") ? parentId : "/" + parentId;
        parent = groupHandler.findGroupById(parentId);
        if (parent == null)
          throw new IllegalArgumentException("Parent group '" + parentId + "' not found!");
        
      }
      Group group = groupHandler.createGroupInstance();
      group.setGroupName(groupName);
      group.setLabel(label);
      group.setDescription(description);
      groupHandler.addChild(parent, group, true);

      if (LOG.isDebugEnabled()) {
        LOG.debug("New group created " + group.getId());
      }
      return group;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * @param username user name
   * @param groupId group id
   * @param type type of membership
   * @return new Membership
   */
  public Membership createMembership(String username, String groupId, String type) {
    try {

      User user = userHandler.findUserByName(username);
      if (user == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("User : '" + username + "' not found!");
        }
      }

      groupId = (groupId.startsWith("/")) ? groupId : "/" + groupId;
      Group group = groupHandler.findGroupById(groupId);
      if (group == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Group : '" + groupId + "' not found!");
        }
      }

      MembershipType membershipType = membershipTypeHandler.findMembershipType(type);
      if (membershipType == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("MembershipType : '" + type + "' not found!");
        }
      }
      
      if (user == null || group == null || membershipType == null)
        throw new IllegalArgumentException("User , group or membershiptype with"
            + " specified name not found!");

      membershipHandler.linkMembership(user, group, membershipType, true);
      Membership membership = membershipHandler.findMembershipByUserGroupAndType(username, groupId, type);
      if (LOG.isDebugEnabled()) {
        LOG.debug("New membership created " + membership.getId());
      }
      return membership;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * @param username user name
   * @param password password
   * @param firstname first name
   * @param lastname last name
   * @param email email
   * @return new User
   */
  public User createUser(String username, String password, String firstname,
      String lastname, String email) {
    try {
      User user = userHandler.createUserInstance(username);
      if (user == null)
        throw new IllegalArgumentException("Can't create user instance!");
      
      user.setPassword(password);
      user.setFirstName(firstname);
      user.setLastName(lastname);
      user.setEmail(email);
      userHandler.createUser(user, true);
      if (LOG.isDebugEnabled()) {
        LOG.debug("New user created " + user.getUserName());
      }
      return user;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * @param groupId group id
   * @return true if delete successful else false
   */
  public boolean deleteGroupd(String groupId) {
    try {
      groupId = (groupId.startsWith("/")) ? groupId : "/" + groupId;
      Group group = groupHandler.findGroupById(groupId);
      if (group != null)
        groupHandler.removeGroup(group, true);
      else
        throw new IllegalArgumentException("Group " + groupId + " not found!");
        
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Delete group " + groupId);
    }
    return true;
  }
  
  /**
   * @param membershipId membership id
   * @return true if delete successful else false
   */
  public boolean deleteMembership(String membershipId) {
    try {
      membershipHandler.removeMembership(membershipId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Delete membership created " + membershipId);
    }
    return true;
  }
  
  /**
   * @param username user name 
   * @return true if delete successful else false
   */
  public boolean deleteUser(String username) {
    try {
      User user = userHandler.findUserByName(username);
      if (user == null)
        throw new IllegalArgumentException("User " + username + " not found!");
      
      userHandler.removeUser(username, true);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Delete user " + username);
    }
    return true;
  }
  
  /**
   * @param username user name
   * @param groupId group id
   * @return true if delete successful else return false
   */
  public boolean deleteUserFromGroup(String username, String groupId) {
    try {
      User user = userHandler.findUserByName(username);
      if (user == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("User : '" + username + "' not found!");
        }
      }

      groupId = (groupId.startsWith("/")) ? groupId : "/" + groupId;
      Group group = groupHandler.findGroupById(groupId);
      if (group == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Group : '" + groupId + "' not found!");
        }
      }
      
      if (user == null || group == null)
        throw new IllegalArgumentException("User or group with specified name not found!");

      Collection<Membership> memberships = membershipHandler.findMembershipsByUserAndGroup(username, groupId);
      for (Membership m : memberships)
        membershipHandler.removeMembership(m.getId(), true);

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("User " + username + " deleted from group " + groupId);
    }
    return true;
  }
  
  /**
   * @param membershipId membership id
   * @return found membership by id 
   * @throws Exception throw exception
   */
  public Membership findMembership(String membershipId) throws Exception {
    Membership membership = membershipHandler.findMembership(membershipId);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Find membership: " + membership);
    }
    return membership;
  }
  
  /**
   * @param findMembership membership 
   * @return found Collection of membership
   * @throws Exception throws exception 
   */
  public Collection<Membership> findMemberships(Membership findMembership) throws Exception {
    String groupId = findMembership.getGroupId();
    if (groupId != null && !groupId.startsWith("/"))
      groupId = "/" + groupId;

    String username = findMembership.getUserName();
    String type = findMembership.getMembershipType();

    Collection<Membership> memberships = new ArrayList<Membership>();
    if (groupId != null && username != null && type != null) {
      Membership membership = membershipHandler.findMembershipByUserGroupAndType(username, groupId, type);
      if (membership != null)
        memberships.add(membership);

    } else if (groupId != null && username != null) {
      memberships.addAll(membershipHandler.findMembershipsByUserAndGroup(username, groupId));

    } else if (groupId != null) {
      Group group = groupHandler.findGroupById(groupId);
      if (group == null)
        throw new IllegalArgumentException("Group " + groupId + " not found,"
                                           + " username and membership type are null!");

      memberships.addAll(membershipHandler.findMembershipsByGroup(group));

    } else if (username != null) {
      User user = userHandler.findUserByName(username);
      if (user == null)
        throw new IllegalArgumentException("User " + username + " not found,"
                                           + " groupId and membership type are null!");
      memberships.addAll(membershipHandler.findMembershipsByUser(username));

    } else if (type != null) {
      Membership membership = membershipHandler.findMembership(type);
      if (membership != null)
        memberships.add(membership);
      
    } else {
      throw new IllegalArgumentException("Username, groupId or membership type "
          + "should be specified!");
    }

    if (memberships.size() > 0) {
      return memberships;
    }
    
    return null;

  }
  
  /**
   * @param user user
   * @return collection of found user
   * @throws Exception throws exception
   */
  public Collection<User> findUsers(User user) throws Exception {

    Query query = new Query();
    query.setUserName(user.getUserName());
    query.setFirstName(user.getFirstName());
    query.setLastName(user.getLastName());
    query.setEmail(user.getEmail());

    Collection<User> users = userHandler.findUsers(query).getAll();
    if (users.size() > 0) {
      return users;
    }
    
    return null;
  }

  /**
   * @param user user
   * @param offset down limit
   * @param amount top limit
   * @return collection of found user 
   * @throws Exception throws exception
   */
  public Collection<User> findUsersRange(User user, int offset, int amount) throws Exception {

    Query query = new Query();
    query.setUserName(user.getUserName());
    query.setFirstName(user.getFirstName());
    query.setLastName(user.getLastName());
    query.setEmail(user.getEmail());

    List<User> users = userHandler.findUsers(query).getAll();
    if (users.size() > 0) {
      if (offset > amount) 
        throw new IllegalArgumentException("Start index is higher then end!");
        
      if (amount > users.size())
        amount = users.size();
      
      return users.subList(offset, amount);
    }
    
    return null;
  }
  
  /**
   * @return collection of group (all groups)
   * @throws Exception throws exception
   */
  public Collection<Group> getAllGroups() throws Exception {
    Collection<Group> groups = groupHandler.getAllGroups();
    return groups;
  }
  
  /**
   * @param filter string for filter
   * @return collection of group
   * @throws Exception throws exception
   */
  public Collection<Group> getFilteredGroup(String filter) throws Exception {
    Collection<Group> groups = groupHandler.getAllGroups();
    Iterator<Group> iter = groups.iterator();
    while (iter.hasNext()) {
      Group g = iter.next();
      if (!g.getId().contains(filter)) 
        iter.remove();
    }
    return groups;
  }
  
  /**
   * @param groupId group id
   * @return information about group
   * @throws Exception throws exception
   */
  public GroupInfo getGroupInfo(String groupId) throws Exception {
    groupId = groupId.startsWith("/") ? groupId : "/" + groupId;
    Group group = groupHandler.findGroupById(groupId);
    if (group == null)
      return null;
    
    Collection<User> members = userHandler.findUsersByGroup(groupId).getAll();

    GroupInfo groupInfo = new GroupInfo();
    groupInfo.setGroup(group);
    groupInfo.setMembers(members);
    return groupInfo;
  }
  
  /**
   * @param parentId id of parent node
   * @return collection of child group
   * @throws Exception throws exception
   */
  public Collection<Group> getGroups(String parentId) throws Exception {
    if (parentId != null)
      parentId = parentId.startsWith("/") ? parentId : "/" + parentId;
    
    Group parent = groupHandler.findGroupById(parentId);
    if (parent == null)
      throw new IllegalArgumentException("Parent " + parentId + " not found!");
    
    Collection<Group> groups = groupHandler.findGroups(parent);
    return groups;
  }
  
  /**
   * @return total count of group
   * @throws Exception throws exception
   */
  public Integer getGroupsCount() throws Exception {
    return groupHandler.getAllGroups().size();
  }
  
  /**
   * @param userId id of user
   * @return collection of group of user
   * @throws Exception throws exception
   */
  public Collection<Group> getGroupsOfUser(String userId) throws Exception {
    if (userHandler.findUserByName(userId) == null)
      throw new IllegalArgumentException("User " + userId + " not found!");
    
    Collection<Group> groups = groupHandler.findGroupsOfUser(userId);

    return groups;
  }

  /**
   * @param parentId id of parent node
   * @param offset down limit
   * @param amount top limit
   * @return collection of group
   * @throws Exception throws exception
   */
  public Collection<Group> getGroupsRange(String parentId, int offset, int amount) throws Exception {
    if (parentId != null)
      parentId = parentId.startsWith("/") ? parentId : "/" + parentId;
    
    Group parent = groupHandler.findGroupById(parentId);
    if (parent == null)
      throw new IllegalArgumentException("Parent " + parentId + " not found!");
    
    Collection<Group> groups = groupHandler.findGroups(parent);
    if (offset > amount) 
      throw new IllegalArgumentException("Start index is higher then end!");
      
    if (amount > groups.size())
      amount = groups.size();

    return new ArrayList<Group>(groups).subList(offset, amount);
  }
  
  /**
   * @return collection of type membership
   * @throws Exception throws exception
   */
  public Collection<MembershipType> getMembershipTypes() throws Exception {
    Collection<MembershipType> membershipTypes = membershipTypeHandler.findMembershipTypes();
    return membershipTypes;
  }
  
  /**
   * @param userId user id
   * @return information about user
   * @throws Exception throws exception
   */
  public User getUser(String userId) throws Exception {
    User user = userHandler.findUserByName(userId);
    return user;
  }
  
  /**
   * @return collection of user
   * @throws Exception throws exception
   */
  public Collection<User> getUsers() throws Exception {
    Query query = new Query();
    Collection<User> users = userHandler.findUsers(query).getAll();
    return users;
  }
  
  /**
   * @return total count of user
   * @throws Exception throws exception
   */
  public Integer getUsersCount() throws Exception {
    return getUsers().size();
  }

  /**
   * @param offset down limit
   * @param amount top limit
   * @return collection of user
   * @throws Exception throws exception
   */
  public Collection<User> getUsersRange(int offset, int amount) throws Exception {
    Query query = new Query();

    List<User> users = userHandler.findUsers(query).getAll();

    if (offset > amount) 
      throw new IllegalArgumentException("Start index is higher then end!");
      
    if (amount > users.size())
      amount = users.size();
    
    return users.subList(offset, amount);
  }
  
  /**
   * @param updGroup group for update
   * @return id of updated group
   * @throws Exception throws exception
   */
  public String updateGroup(Group updGroup) throws Exception {
    String groupId = updGroup.getId();
    
    groupId = groupId.startsWith("/") ? groupId : "/" + groupId;
    
    Group group = groupHandler.findGroupById(groupId);
    if (group != null) {
      if (updGroup.getGroupName() != null)
        group.setGroupName(updGroup.getGroupName());
      if (updGroup.getLabel() != null)
        group.setLabel(updGroup.getLabel());
      if (updGroup.getDescription() != null)
        group.setDescription(updGroup.getDescription());
      groupHandler.saveGroup(group, true);
      
      if (LOG.isDebugEnabled()) {
        LOG.debug("Group " + groupId + " updated.");
      }
        
      return groupId;
    }
    
    return null;
  }
  
  /**
   * @param updUser user for update
   * @return id of updated user
   * @throws Exception throws exception
   */
  public String updateUser(User updUser) throws Exception {
    String userId = updUser.getUserName();
    
    User user = userHandler.findUserByName(userId);
    if (user != null) {
      if (updUser.getFirstName() != null)
        user.setFirstName(updUser.getFirstName());
      if (updUser.getLastName() != null)
        user.setLastName(updUser.getLastName());
      if (updUser.getPassword() != null)
        user.setPassword(updUser.getPassword());
      if (updUser.getEmail() != null)
        user.setEmail(updUser.getEmail());
      return userId;
    }
    
    return null;
  }
  
}
