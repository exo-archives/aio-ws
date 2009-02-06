/*
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

package org.exoplatform.services.organization.rest.xml;


import generated.GroupInfo;
import generated.Memberships;
import generated.Users;

import java.util.Collection;

import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.rest.OrganizationServiceHelper;
import org.exoplatform.services.rest.ContextParam;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.QueryTemplate;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.JAXBInputTransformer;
import org.exoplatform.services.rest.transformer.JAXBOutputTransformer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * Created by The eXo Platform SAS .
 * @author Gennady Azarenkov
 * @version $Id:$
 */

@URITemplate("/organization/xml/")
public class RESTOrganizationServiceXMLImpl implements ResourceContainer {
  
  /**
   * type of content.
   */
  protected static final String XML_CONTENT_TYPE = "text/xml";
  
  /**
   * helper object do all operation with organization service.
   */
  private OrganizationServiceHelper helper;

  /**
   * @param organizationService implementation of OrganizationService.
   */
  public RESTOrganizationServiceXMLImpl(OrganizationService organizationService) {
    helper = new OrganizationServiceHelper(organizationService);
  }

  // Group handler methods

  /**
   * @param baseURI the base URI the base URI
   * @param groupJAXB the object wit information for new Group
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/newgroup/")
  @InputTransformer(JAXBInputTransformer.class)
  public Response createGroup(@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
      generated.Group groupJAXB) {

    String parentId = "/exo";
    String groupName = groupJAXB.getName();
    String label = groupJAXB.getLabel();
    String description = groupJAXB.getDescription();
    Group group = helper.createGroup(parentId, groupName, label, description);
    if (group != null)
      return Response.Builder.created(baseURI + "/organization/xml/group/" + group.getId()).build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Create group failed!").build();

  }

  
  
  /**
   * @param baseURI the base URI
   * @param membershipJAXB the object wit information for new Membership
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/newmembership/")
  @InputTransformer(JAXBInputTransformer.class)
  public Response createMembership(@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
      generated.Membership membershipJAXB) {

    String username = membershipJAXB.getUserName();
    String groupId = membershipJAXB.getGroupId();
    String type = membershipJAXB.getMembershipType();

    Membership membership = helper.createMembership(username, groupId, type);
    if (membership != null)
      return Response.Builder.created(baseURI + "/organization/xml/membership/" + membership.getId()).build();      

    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Create group failed!").build();
  }
  
  
  /**
   * @param baseURI the base URI
   * @param userJAXB the object wit information for new User
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/newuser/")
  @InputTransformer(JAXBInputTransformer.class)
  public Response createUser(@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
      generated.User userJAXB) {

    String username = userJAXB.getUserName();
    String password = userJAXB.getPassword();
    String firstname = userJAXB.getFirstName();
    String lastname = userJAXB.getLastName();
    String email = userJAXB.getEmail();

    User user = helper.createUser(username, password, firstname, lastname, email);
    if (user != null)
      return Response.Builder.created(baseURI
          + "/organization/xml/user/" + user.getUserName()).build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Create user failed!").build();
    
  }
  
  /**
   * @param groupId the group ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=delete")
  @URITemplate("/group/{groupId}/")
  public Response deleteGroup(@URIParam("groupId") String groupId) {

    if (helper.deleteGroupd(groupId))
      return Response.Builder.noContent().build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Delete group failed!").build();
    
  }
  
  /**
   * @param membershipId the membership ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=delete")
  @URITemplate("/membership/{membershipId}/")
  public Response deleteMembership(@URIParam("membershipId") String membershipId) {

    if (helper.deleteGroupd(membershipId))
      return Response.Builder.noContent().build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Delete membership failed!").build();
  
  }

  /**
   * @param username the user ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=delete")
  @URITemplate("/user/{username}/")
  public Response deleteUser(@URIParam("username") String username) {

    if (helper.deleteUser(username))
      return Response.Builder.noContent().build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Delete user failed!").build();
    
  }

  /**
   * @param groupId the group ID
   * @param username the user ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=delete")
  @URITemplate("/groups/{username}/{groupId}/")
  public Response deleteUserFromGroup(@URIParam("groupId") String groupId,
      @URIParam("username") String username) {

    if (helper.deleteUserFromGroup(username, groupId)) 
      return Response.Builder.noContent().build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Delete user " + username + " from group " + groupId + " failed!").build();
    
  }
  
  /**
   * @param membershipId the membership ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/membership/{membershipId}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response findMembership(@URIParam("membershipId") String membershipId) {

    try {
      Membership membership = helper.findMembership(membershipId); 
      
      if (membership != null)
        return Response.Builder.ok(JAXBTransformUtil.membershipToMembershipJAXB(membership), XML_CONTENT_TYPE).build();
    
      return Response.Builder.notFound().errorMessage(
          "Membership with id: '" + membershipId + "' not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Find membership failed!").build();
    }
  }
  
  
  /**
   * @param membershipJAXB the object with information for search
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/memberships/")
  @InputTransformer(JAXBInputTransformer.class)
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response findMemberships(generated.Membership membershipJAXB) {
    try {
      Collection<Membership> collection = helper.findMemberships(JAXBTransformUtil.membershipJAXBToMembership(membershipJAXB));
      if (collection != null) {
        Memberships memberships = JAXBTransformUtil.collectionMembershipToMembershipsJAXB(collection);
        return Response.Builder.ok(memberships, XML_CONTENT_TYPE).build();
      }
      return Response.Builder.withStatus(HTTPStatus.NOT_FOUND).build();      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Find memberships failed!").build();
    }
  }
  
  /**
   * @param userJAXB the object with information for search
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/userlike/")
  @InputTransformer(JAXBInputTransformer.class)
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response findUsers(generated.User userJAXB) {
    try {
      Users users = JAXBTransformUtil.collectionUserToUsersJAXB(helper.findUsers(JAXBTransformUtil.userJAXBToUser(userJAXB)));
      if (users != null)
        return Response.Builder.ok(users, XML_CONTENT_TYPE).build();
      return Response.Builder.withStatus(HTTPStatus.NOT_FOUND).build();      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Find users failed!").build();      
    }
  }
  
  /**
   * @param offset the down limit
   * @param amount the top limit
   * @param userJAXB the object with information for search
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/userrange/{from}/{to}/")
  @InputTransformer(JAXBInputTransformer.class)
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response findUsersRange(@URIParam("from") Integer offset, @URIParam("to") Integer amount,
      generated.User userJAXB) {
    try {
      Users users = JAXBTransformUtil.collectionUserToUsersJAXB((helper.findUsersRange(JAXBTransformUtil.userJAXBToUser(userJAXB), offset, amount)));
      if (users != null)
        return Response.Builder.ok(users, XML_CONTENT_TYPE).build();
      return Response.Builder.withStatus(HTTPStatus.NOT_FOUND).build();      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Find users failed!").build();      
    }
  }
  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groupsall/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getAllGroup() {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionGroupToGroupsJAXB(helper.getAllGroups()), XML_CONTENT_TYPE).build();
      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get list of groups failed!").build();      
    }
  }
  
  /**
   * @param filteredname filter for group id
   * @return Response object with HTTP status. Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groupfilter/{filteredname}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getFilteredGroup(@URIParam("filteredname") String filteredname) {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionGroupToGroupsJAXB(helper.getFilteredGroup(filteredname)), XML_CONTENT_TYPE).build();
      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get list of groups failed!").build();      
    }
  }
  
  /**
   * @param groupId the group ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/group/{groupId}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getGroup(@URIParam("groupId") String groupId) {
    try {
      GroupInfo groupInfo = JAXBTransformUtil.groupInfoToGroupInfoJAXB(helper.getGroupInfo(groupId));
      if (groupInfo != null)
        return Response.Builder.ok(groupInfo, XML_CONTENT_TYPE).build();
      return Response.Builder.withStatus(HTTPStatus.NOT_FOUND).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get info about group " + groupId + " failed!").build();
    }
  }
  
  /**
   * @param parentId ID of parent node
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groups/{parentId}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getGroups(@URIParam("parentId") String parentId) {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionGroupToGroupsJAXB(helper.getGroups(parentId)), XML_CONTENT_TYPE).build();
      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get list of groups failed!").build();      
    }
  }
  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groupcount/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response getGroupsCount() {
    try {
      int count = helper.getGroupsCount();
      String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><count>"
                    + count 
                    + "</count>"; 
      return Response.Builder.ok(xml, XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get groups number failed!").build();
    }
  }
  
  
  /**
   * @param userId user ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groups-of-user/{username}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getGroupsOfUser(@URIParam("username") String userId) {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionGroupToGroupsJAXB(helper.getGroupsOfUser(userId)), XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get groups for user " + userId + " failed!").build();
    }
  }
  
  /**
   * @param parentId the id of parent node
   * @param offset the down limit 
   * @param amount the top limit
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/grouprange/{parentId}/{from}/{to}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getGroupsRange(@URIParam("parentId") String parentId, @URIParam("from") Integer offset,
      @URIParam("to") Integer amount) {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionGroupToGroupsJAXB(helper.getGroupsRange(parentId, offset, amount)), XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get groups range failed1").build();
    }
  }
  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/membershiptypes/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getMembershipTypes() {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionMembershipTypesToMembershipTypesJAXB(helper.getMembershipTypes()), XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get membership types failed!").build();
    }
  }
  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groups/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getRootGroups() {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionGroupToGroupsJAXB(helper.getGroups(null)), XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get groups failed!").build();
    }
  }

  
  /**
   * @param userId the user ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/user/{username}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getUser(@URIParam("username") String userId) {
    try {
      User user = helper.getUser(userId);
      if (user != null)
        return Response.Builder.ok(JAXBTransformUtil.userToUserJAXB(user), XML_CONTENT_TYPE).build();
      
      return Response.Builder.notFound().errorMessage("User " + userId + " not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get user failed!").build();
    }
  }
  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/users/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getUsers() {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionUserToUsersJAXB(helper.getUsers()), XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get users failed!").build();
    }
  }
  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/usercount/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response getUsersCount() {
    try {
      int count = helper.getUsersCount();
      String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><count>" 
                    + count 
                    + "</count>";
      return Response.Builder.ok(xml, XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get users number failed!").build();
    }
  }
  
  /**
   * @param offset the down limit
   * @param amount the top limit
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/userrange/{from}/{number}/")
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response getUsersRange(@URIParam("from") Integer offset, @URIParam("number") Integer amount) {
    try {
      return Response.Builder.ok(JAXBTransformUtil.collectionUserToUsersJAXB(helper.getUsersRange(offset, amount)), XML_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get users failed!").build();
    }
  }
  
  /**
   * @param groupJAXB the object with information for update
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=put")
  @URITemplate("/group/")
  @InputTransformer(JAXBInputTransformer.class)
  public Response updateGroup(generated.Group groupJAXB) {
    try {
      String groupId = helper.updateGroup(JAXBTransformUtil.groupJAXBToGroup(groupJAXB));
      if (groupId != null)
        return Response.Builder.noContent().build();
      return Response.Builder.notFound().errorMessage("Group " + groupJAXB.getId() + " not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Update group failed!").build();
    }
  }
  
  /**
   * @param userJAXB the object with information for update
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=put")
  @URITemplate("/user/")
  @InputTransformer(JAXBInputTransformer.class)
  public Response updateUser(generated.User userJAXB) {
    try {
      String userId = helper.updateUser(JAXBTransformUtil.userJAXBToUser(userJAXB));
      if (userId != null)
        return Response.Builder.noContent().build();
      return Response.Builder.notFound().errorMessage("User " + userJAXB.getUserName() + " not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Update user failed!").build();
    }

  }
  
}
