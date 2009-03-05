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

package org.exoplatform.services.organization.rest.json;

import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.impl.GroupImpl;
import org.exoplatform.services.organization.impl.MembershipImpl;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.organization.rest.GroupInfo;
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
import org.exoplatform.ws.frameworks.json.transformer.Bean2JsonOutputTransformer;
import org.exoplatform.ws.frameworks.json.transformer.Json2BeanInputTransformer;


/**
 * Created by The eXo Platform SAS.
 * @author Gennady Azarenkov
 * @version $Id:$ REST operations and appropriate HTTP methods
 */
@URITemplate("/organization/json/")
public class RESTOrganizationServiceJSONImpl implements ResourceContainer {

  /**
   * type of content.
   */
  protected static final  String JSON_CONTENT_TYPE = "application/json";

  /**
   * helper object do all operation with organization service.
   */
  private OrganizationServiceHelper helper;

  /**
   * @param organizationService implementation of OrganizationService. 
   */
  public RESTOrganizationServiceJSONImpl(OrganizationService organizationService) {
    helper = new OrganizationServiceHelper(organizationService);
  }

  /**
   * @param baseURI the base URI
   * @param groupBean the object with information for new Group
   * @return Response object with HTTP status. Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/newgroup/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response createGroup(@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
                              GroupImpl groupBean) {

    String parentId = groupBean.getParentId();
    String groupName = groupBean.getGroupName();
    String label = groupBean.getLabel();
    String description = groupBean.getDescription();
    
    Group group = helper.createGroup(parentId, groupName, label, description);
    if (group != null)
      return Response.Builder.created(baseURI + "/organization/json/group/" + group.getId()).build();
    
    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Create group failed!").build();

  }

  /**
   * @param baseURI the base URI
   * @param membershipBean the object with information for new Membership
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/newmembership/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response createMembership(@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
                                   MembershipImpl membershipBean) {

    String username = membershipBean.getUserName();
    String groupId = membershipBean.getGroupId();
    String type = membershipBean.getMembershipType();

    Membership membership = helper.createMembership(username, groupId, type);
    if (membership != null)
      return Response.Builder.created(baseURI
          + "/organization/json/membership/" + membership.getId()).build();      

    return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Create group failed!").build();
  }

  /**
   * @param baseURI the base URI
   * @param userBean the object with information for new User
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/newuser/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response createUser(@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
                             UserImpl userBean) {

    String username = userBean.getUserName();
    String password = userBean.getPassword();
    String firstname = userBean.getFirstName();
    String lastname = userBean.getLastName();
    String email = userBean.getEmail();

    User user = helper.createUser(username, password, firstname, lastname, email);
    if (user != null)
      return Response.Builder.created(baseURI
          + "/organization/json/user/" + user.getUserName()).build();
    
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
   * @param username the user name
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response findMembership(@URIParam("membershipId") String membershipId) {

    try {
      Membership membership = helper.findMembership(membershipId); 
      if (membership != null)
        return Response.Builder.ok(membership, JSON_CONTENT_TYPE).build();
    
      return Response.Builder.notFound().errorMessage(
          "Membership with id: '" + membershipId + "' not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Find membership failed!").build();
    }
  }

  /**
   * @param membership the object with information for search
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/memberships/")
  @InputTransformer(Json2BeanInputTransformer.class)
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response findMemberships(MembershipImpl membership) {
    try {
      Memberships memberships = JSONTransformUtil.collectionMembershipToMemberships(helper.findMemberships(membership));
      if (memberships != null)
        return Response.Builder.ok(memberships, JSON_CONTENT_TYPE).build();
      return Response.Builder.withStatus(HTTPStatus.NOT_FOUND).build();      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Find memberships failed!").build();
    }
  }

  /**
   * @param user the object with information for search
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/userlike/")
  @InputTransformer(Json2BeanInputTransformer.class)
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response findUsers(UserImpl user) {
    try {
      Users users = JSONTransformUtil.colectionUserToUsers(helper.findUsers(user));
      
      if (users != null)
        return Response.Builder.ok(users, JSON_CONTENT_TYPE).build();
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
   * @param user the object with information for search
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/userrange/{from}/{to}/")
  @InputTransformer(Json2BeanInputTransformer.class)
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response findUsersRange(@URIParam("from") Integer offset, @URIParam("to") Integer amount,
      UserImpl user) {
    try {
      Users users = JSONTransformUtil.colectionUserToUsers(helper.findUsersRange(user, offset, amount));
      if (users != null)
        return Response.Builder.ok(users, JSON_CONTENT_TYPE).build();
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getAllGroup() {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionGroupToGroups(helper.getAllGroups()), JSON_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get list of groups failed!").build();      
    }
  }

  /**
   * @param filteredname the filter for group id
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groupfilter/{filteredname}/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getFilteredGroup(@URIParam("filteredname") String filteredname) {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionGroupToGroups(helper.getFilteredGroup(filteredname)), JSON_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Get list of groups failed!").build();      
    }
  }

  /**
   * @param groupId the group ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/group/{groupId}/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getGroup(@URIParam("groupId") String groupId) {
    try {
      GroupInfo groupInfo = helper.getGroupInfo(groupId);
      if (groupInfo != null)
        return Response.Builder.ok(groupInfo, JSON_CONTENT_TYPE).build();
      
      return Response.Builder.withStatus(HTTPStatus.NOT_FOUND).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get info about group " + groupId + " failed!").build();
    }
  }

  /**
   * @param parentId the id of parent node
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groups/{parentId}/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getGroups(@URIParam("parentId") String parentId) {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionGroupToGroups(helper.getGroups(parentId)), JSON_CONTENT_TYPE).build();
      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Get list of groups failed!").build();      
    }
  }

  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groupcount/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getGroupsCount() {
    try {
      Count count = new Count(helper.getGroupsCount());
      return Response.Builder.ok(count, JSON_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Get groups number failed!").build();
    }
  }

  /**
   * @param userId the user id
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groups-of-user/{username}/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getGroupsOfUser(@URIParam("username") String userId) {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionGroupToGroups(helper.getGroupsOfUser(userId)), JSON_CONTENT_TYPE).build();
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getGroupsRange(@URIParam("parentId") String parentId, @URIParam("from") Integer offset,
      @URIParam("to") Integer amount) {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionGroupToGroups(helper.getGroupsRange(parentId, offset, amount)), JSON_CONTENT_TYPE).build();
      
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getMembershipTypes() {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionMembershipTypesToMembershipTypes(helper.getMembershipTypes()), JSON_CONTENT_TYPE).build();
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getRootGroups() {
    try {
      return Response.Builder.ok(JSONTransformUtil.collectionGroupToGroups(helper.getGroups(null)), JSON_CONTENT_TYPE).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get groups failed!").build();
    }
  }

  /**
   * @param userId the user id
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/user/{username}/")
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getUser(@URIParam("username") String userId) {
    try {
      User user = helper.getUser(userId);
      if (user != null)
        return Response.Builder.ok(user, JSON_CONTENT_TYPE).build();
      
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getUsers() {
    try {
      return Response.Builder.ok(JSONTransformUtil.colectionUserToUsers(helper.getUsers()), JSON_CONTENT_TYPE).build();
      
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getUsersCount() {
    try {
      return Response.Builder.ok(helper.getUsersCount(), JSON_CONTENT_TYPE).build();
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
  @OutputTransformer(Bean2JsonOutputTransformer.class)
  public Response getUsersRange(@URIParam("from") Integer offset, @URIParam("number") Integer amount) {
    try {
      return Response.Builder.ok(JSONTransformUtil.colectionUserToUsers(helper.getUsersRange(offset, amount)), JSON_CONTENT_TYPE).build();
      
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Get users failed!").build();
    }
  }

  /**
   * @param group the update object 
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=put")
  @URITemplate("/group/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response updateGroup(GroupImpl group) {
    try {
      String groupId = helper.updateGroup(group);
      if (groupId != null)
        return Response.Builder.noContent().build();
      
      return Response.Builder.notFound().errorMessage("Group " + group.getId() + " not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Update group failed!").build();
    }
  }

  /**
   * @param user the update object
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.POST)
  @QueryTemplate("method=put")
  @URITemplate("/user/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response updateUser(UserImpl user) {
    try {
      String userId = helper.updateUser(user);
      if (userId != null)
        return Response.Builder.noContent().build();
      
      return Response.Builder.notFound().errorMessage("User " + user.getUserName() + " not found!").build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
          "Update user failed!").build();
    }
  }
}
