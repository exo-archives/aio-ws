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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.organization.rest.json.Count;
import org.exoplatform.services.organization.rest.json.Groups;
import org.exoplatform.services.organization.rest.json.RESTOrganizationServiceJSONImpl;
import org.exoplatform.services.organization.rest.json.Users;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.Request;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.ResourceIdentifier;
import org.exoplatform.services.rest.Response;
import org.exoplatform.ws.frameworks.json.transformer.Bean2JsonOutputTransformer;

/**
 * Created by The eXo Platform SAS Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */

public class JsonResponseOrgserviceTest extends TestCase {

  StandaloneContainer             container;

  OrganizationService             orgService;

  RESTOrganizationServiceJSONImpl jsonOrgService;

  ResourceDispatcher              dispatcher;

  static final String             baseURI = "http://localhost:8080/rest/";

  protected void setUp() throws Exception {
    super.setUp();

    String containerConf = JsonResponseOrgserviceTest.class.getResource("/conf/standalone/test-configuration.xml")
                                                           .toString();

    StandaloneContainer.setConfigurationURL(containerConf);
    container = StandaloneContainer.getInstance();
    orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    jsonOrgService = (RESTOrganizationServiceJSONImpl) container.getComponentInstanceOfType(RESTOrganizationServiceJSONImpl.class);

    dispatcher = (ResourceDispatcher) container.getComponentInstanceOfType(ResourceDispatcher.class);

  }

  
   protected void tearDown() throws Exception {
   super.tearDown();
   }
  
  // not implemented by DummyOrganizationService
  public void testCreateGroup() throws Exception {
  }

  // not implemented by DummyOrganizationServiceOrganizationService
  public void testCreateMembership() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testCreateUser() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testDeleteGroup() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testDeleteMembership() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testDeleteUser() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testDeleteUserFromGroup() throws Exception {
  }

  // not supported by DummyOrganizationServiceOrganizationService
  public void testFindMembership() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testGetGroups() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testUpdateGroup() throws Exception {
  }

  // not implemented by DummyOrganizationService
  public void testUpdateUser() throws Exception {
  }

  /**
   * However methods findMembershipsByUserAndGroup,
   * findMembershipByUserGroupAndType, findMembershipsByGroup are not
   * implemented by DummyOrganizationService, we should pass "username"
   * parameter only by QueryParam! others parameters are groupId and type
   */
//   public void testFindMemberships() throws Exception {
//  
//   MultivaluedMetadata mv = new MultivaluedMetadata();
//   MultivaluedMetadata qp = new MultivaluedMetadata();
//   // admin - user from DummyOrganizationService
//      
//   // qp.putSingle("command", "view-all");
//  
//   String extURI = "/organization/json/memberships/";
//      
//   final Bean2JsonOutputTransformer bt = new Bean2JsonOutputTransformer();
//   final PipedOutputStream po = new PipedOutputStream();
//   final PipedInputStream pi = new PipedInputStream(po);
//   new Thread(){
//   @Override
//   public void run() {
//   MembershipImpl m = new MembershipImpl();
//   m.setUserName("admin");
//   try {
//   bt.writeTo(m, po);
//   po.flush();
//   po.close();
//   } catch (IOException e) {
//   e.printStackTrace();
//   fail();
//   }
//   }
//   }.start();
//  
//   Request request = new Request(pi, new ResourceIdentifier(baseURI, extURI),
//   "POST", mv, qp);
//  
//   Response response = null;
//   response = dispatcher.dispatch(request);
//   assertNotNull(response);
//   assertEquals(HTTPStatus.OK, response.getStatus());
//  
//   response.writeEntity(System.out);
//  
//   }
  
  public void testFindUsers() {
    try {
      UserHandler hUser = orgService.getUserHandler();

      MultivaluedMetadata mv = new MultivaluedMetadata();
      MultivaluedMetadata qp = new MultivaluedMetadata();
      // admin - user from DummyOrganizationService

      String extURI = "/organization/json/userlike/";

      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      UserImpl user = new UserImpl();
      user.setUserName("admin");
      user.setEmail("email@mail.come");
      user.setId("admin");
      user.setFirstName("firstName");
      user.setLastName("lastName");
      user.setCreatedDate(new Date());
      new Bean2JsonOutputTransformer().writeTo(user, bout);

      Request request = new Request(new ByteArrayInputStream(bout.toByteArray()),
                                    new ResourceIdentifier(baseURI, extURI),
                                    "POST",
                                    mv,
                                    qp);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());

      Users entity = (Users) response.getEntity();

      Query query = new Query();
      query.setUserName("admin");

      Collection<User> list = hUser.findUsers(query).getAll();
      Users user_list_bean = new Users(list);
      assertEquals(user_list_bean.getUsers(), entity.getUsers());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testFindUsersRange() throws Exception {

    UserHandler hUser = orgService.getUserHandler();

    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();
    // admin - user from DummyOrganizationService

    UserImpl user = new UserImpl();
    user.setUserName("admin");
    user.setEmail("email@mail.come");
    user.setId("admin");
    user.setFirstName("firstName");
    user.setLastName("lastName");
    user.setCreatedDate(new Date());

    Integer from = 0, to = 5;

    String extURI = String.format("/organization/json/userrange/%s/%s/",
                                  from.toString(),
                                  to.toString());

    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    new Bean2JsonOutputTransformer().writeTo(user, bout);
    Request request = new Request(new ByteArrayInputStream(bout.toByteArray()),
                                  new ResourceIdentifier(baseURI, extURI),
                                  HTTPMethods.POST,
                                  mv,
                                  qp);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());

    Users entity = (Users) response.getEntity();

    Query query = new Query();
    query.setUserName("admin");

    Collection<User> list = hUser.findUsers(query).getAll().subList(from, to);
    Users user_list_bean = new Users(list);

    // See overrided method "equals" in UserListBean
    assertEquals(user_list_bean.getUsers(), entity.getUsers());

  }

  public void testGetAllGroup() {
    try {
      MultivaluedMetadata mv = new MultivaluedMetadata();
      MultivaluedMetadata qp = new MultivaluedMetadata();

      // blank filter
      String extURI = "/organization/json/groupsall/";

      Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());

      Groups entity = (Groups) response.getEntity();
      
      GroupHandler hGroup = orgService.getGroupHandler();
      
      
      for (Iterator iterator = entity.getGroups().iterator(); iterator.hasNext();) {
        Group group = (Group) iterator.next();
        String id = group.getId();
        Group group2 = orgService.getGroupHandler().findGroupById(id);
        assertNotNull(group2);
      } 
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testGetGroup() throws Exception {
    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();

    String group_id = "/admin";

    String extURI = "/organization/json/group/" + "admin/";

    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());

    GroupHandler hGroup = orgService.getGroupHandler();
    UserHandler hUser = orgService.getUserHandler();

    Collection<User> members = hUser.findUsersByGroup(group_id).getAll();
    Group group = hGroup.findGroupById(group_id);

    GroupInfo m_entity = new GroupInfo(group, members);

    GroupInfo entity = (GroupInfo) response.getEntity();

    assertEquals(m_entity.getGroup().getId(), entity.getGroup().getId());
    assertEquals(m_entity.getMembers().size(), entity.getMembers().size());

  }

  public void testGetGroupsCount() throws Exception {
    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();

    qp.putSingle("command", "count");

    String extURI = "/organization/json/groupcount/";

    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());

    Count entity = (Count) response.getEntity();

    GroupHandler hGroup = orgService.getGroupHandler();
    int quantity = hGroup.getAllGroups().size();

    Count groupsBean = new Count(quantity);

    // See overrided method "equals" in CountBean
    assertEquals(entity.getCount(), groupsBean.getCount());
  }

  public void testGetGroupsOfUser() throws Exception {
    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();

    String username = "admin";

    String extURI = "/organization/json/groups-of-user/" + username;

    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());

    Groups entity = (Groups) response.getEntity();

    GroupHandler hGroup = orgService.getGroupHandler();
    Collection<Group> groups = hGroup.findGroupsOfUser(username);

    Groups groupsBean = new Groups(groups);

    // See overrided method "equals" in GroupListBean
    assertEquals(entity.getGroups().size(), groupsBean.getGroups().size());
  }
  
  
////////////////////////////////////////////////////
  
  // // Find group is not supported by DummyOrganizationService
  // //
  // // public void testGetGroupsRange_NullParentID() throws Exception {
  // // MultivaluedMetadata mv = new MultivaluedMetadata();
  // // MultivaluedMetadata qp = new MultivaluedMetadata();
  // //
  // // Integer from = 0, to = 5;
  // // String extURI =
  // String.format("/organization/json/group/view-from-to/%s/%s/", from, to);
  // //
  // // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, qp);
  // //
  // // Response response = null;
  // // response = dispatcher.dispatch(request);
  // // assertNotNull(response);
  // // assertEquals(HTTPStatus.OK, response.getStatus());
  // //
  // // GroupListBean entity = (GroupListBean) response.getEntity();
  // //
  // // GroupHandler hGroup = orgService.getGroupHandler();
  // // Collection<Group> groups = hGroup.findGroups(null);
  // //
  // // GroupListBean groupsBean = new GroupListBean(groups);
  // //
  // // assertEquals(entity, groupsBean);
  // // }
  // //
  // // public void testGetGroupsRange_NotNullParentID() throws Exception {
  // // MultivaluedMetadata mv = new MultivaluedMetadata();
  // // MultivaluedMetadata qp = new MultivaluedMetadata();
  // //
  // // String parentId = "admin";
  // // qp.putSingle("parentId", parentId);
  // //
  // // Integer from = 0, to = 10;
  // // String extURI =
  // String.format("/organization/json/group/view-from-to/%s/%s/", from, to);
  // //
  // // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, qp);
  // //
  // // Response response = null;
  // // response = dispatcher.dispatch(request);
  // // assertNotNull(response);
  // // assertEquals(HTTPStatus.OK, response.getStatus());
  // //
  // // GroupListBean entity = (GroupListBean) response.getEntity();
  // //
  // // GroupHandler hGroup = orgService.getGroupHandler();
  // // Group parent = hGroup.findGroupById(parentId);
  // //
  // // assertNotNull(parent);
  // //
  // // Collection<Group> groups = hGroup.findGroups(parent);
  // //
  // // GroupListBean groupsBean = new GroupListBean(groups);
  // //
  // // assertEquals(entity, groupsBean);
  // // }
  //
  // // MembershipTypes is not supported by DummyOrganizationService
  //  
  // // public void testGetMembershipsTypes() throws Exception {
  // // MultivaluedMetadata mv = new MultivaluedMetadata();
  // //
  // // String extURI = "/organization/json/membership/types/";
  // //
  // // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, null);
  // //
  // // Response response = null;
  // // response = dispatcher.dispatch(request);
  // // assertNotNull(response);
  // // assertEquals(HTTPStatus.OK, response.getStatus());
  // //
  // // MembershipTypesListBean entity = (MembershipTypesListBean)
  // response.getEntity();
  // //
  // // MembershipTypeHandler hMembershipType =
  // orgService.getMembershipTypeHandler();
  // //
  // // Collection<MembershipType> membershipTypes =
  // hMembershipType.findMembershipTypes();
  // //
  // // MembershipTypesListBean membership_types_list_bean = new
  // MembershipTypesListBean(
  // // membershipTypes);
  // //
  // // assertEquals(entity, membership_types_list_bean);
  // // }
  //
  // public void testGetUser() throws Exception {
  // MultivaluedMetadata mv = new MultivaluedMetadata();
  //
  // String username = "admin";
  //
  // String extURI = String.format("/organization/json/user/%s/", username);
  //
  // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, null);
  //
  // Response response = null;
  // response = dispatcher.dispatch(request);
  // assertNotNull(response);
  // assertEquals(HTTPStatus.OK, response.getStatus());
  //
  // User entity = (User) response.getEntity();
  //
  // UserHandler hUser = orgService.getUserHandler();
  // User user = hUser.findUserByName(username);
  //
  // assertEquals(entity, user);
  //
  // }
  //
  // public void testGetUsers() throws Exception {
  // MultivaluedMetadata mv = new MultivaluedMetadata();
  //
  // String extURI = "/organization/json/users/";
  //
  // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, null);
  //
  // Response response = null;
  // response = dispatcher.dispatch(request);
  // assertNotNull(response);
  // assertEquals(HTTPStatus.OK, response.getStatus());
  //
  // UsersBean entity = (UsersBean) response.getEntity();
  //
  // UserHandler hUser = orgService.getUserHandler();
  // Collection<User> user_list = hUser.findUsers(new Query()).getAll();
  // UsersBean user_list_bean = new UsersBean(user_list);
  //
  // assertEquals(entity, user_list_bean);
  // }
  //
  // public void testGetUsersCount() throws Exception {
  // MultivaluedMetadata mv = new MultivaluedMetadata();
  // MultivaluedMetadata qp = new MultivaluedMetadata();
  //    
  // qp.putSingle("command", "count");
  //    
  // String extURI = "/organization/json/user/";
  //
  // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, qp);
  //
  // Response response = null;
  // response = dispatcher.dispatch(request);
  // assertNotNull(response);
  // assertEquals(HTTPStatus.OK, response.getStatus());
  //
  // Count entity = (Count) response.getEntity();
  //
  // UserHandler hUser = orgService.getUserHandler();
  // int quantity = hUser.findUsers(new Query()).getAll().size();
  //
  // Count usersBean = new Count(quantity);
  //
  // assertEquals(entity, usersBean);
  // }
  //
  // public void testUsersRange() throws Exception {
  // MultivaluedMetadata mv = new MultivaluedMetadata();
  // MultivaluedMetadata qp = new MultivaluedMetadata();
  // qp.putSingle("command", "view-range");
  //    
  // Integer offset = 0, amount = 5;
  // String extURI = String.format("/organization/json/user/%s/%s/", offset,
  // amount);
  //
  // Request request = new Request(null, new ResourceIdentifier(baseURI,
  // extURI), "GET", mv, qp);
  //
  // Response response = null;
  // response = dispatcher.dispatch(request);
  // assertNotNull(response);
  // assertEquals(HTTPStatus.OK, response.getStatus());
  //
  // UsersBean entity = (UsersBean) response.getEntity();
  //
  // UserHandler userHandler = orgService.getUserHandler();
  //    
  // List<User> list = userHandler.findUsers(new Query()).getAll();
  // int prevFrom = -1;
  // if (offset > 0)
  // prevFrom = ((offset - amount) > 0) ? offset - amount : 0;
  // int nextFrom = ((offset + amount) < list.size()) ? offset + amount : -1;
  // int to = (offset + amount < list.size()) ? offset + amount : list.size();
  //
  // UsersBean user_list_bean = new UsersBean(list.subList(offset, to));
  //    
  //
  // assertEquals(entity, user_list_bean );
  // }
  //
}
