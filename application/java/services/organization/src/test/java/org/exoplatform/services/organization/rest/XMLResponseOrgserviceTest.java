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

import generated.GroupInfo;
import generated.Groups;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.OrganizationConfig.User;
import org.exoplatform.services.organization.rest.xml.RESTOrganizationServiceXMLImpl;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.Request;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.ResourceIdentifier;
import org.exoplatform.services.rest.Response;
import org.w3c.dom.Document;

/**
 * Created by The eXo Platform SAS Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */

public class XMLResponseOrgserviceTest extends TestCase {

  StandaloneContainer            container;

  OrganizationService            orgService;

  RESTOrganizationServiceXMLImpl xmlOrgService;

  ResourceDispatcher             dispatcher;

  static final String            baseURI = "http://localhost:8080/rest/";

  protected void setUp() throws Exception {
    super.setUp();

    String containerConf = XMLResponseOrgserviceTest.class.getResource("/conf/standalone/test-configuration.xml")
                                                          .toString();

    StandaloneContainer.setConfigurationURL(containerConf);
    container = StandaloneContainer.getInstance();
    orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    xmlOrgService = (RESTOrganizationServiceXMLImpl) container.getComponentInstanceOfType(RESTOrganizationServiceXMLImpl.class);

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

  // /**
  // * However methods findMembershipsByUserAndGroup,
  // * findMembershipByUserGroupAndType, findMembershipsByGroup are not
  // * implemented by DummyOrganizationService,
  // * we should pass "username" parameter only by QueryParam!
  // * others parameters are groupId and type
  // */
  // public void testFindMemberships() {
  // try{
  //
  // MembershipHandler hMembership = orgService.getMembershipHandler();
  //
  // MultivaluedMetadata mv = new MultivaluedMetadata();
  // MultivaluedMetadata qp = new MultivaluedMetadata();
  // // admin - user from DummyOrganizationService
  // String username = "admin";
  //
  //    
  // String extURI = "/organization/xml/memberships/";
  // File file = new File("src/main/resources/xml/membership.xml");
  // InputStream inputStream = new FileInputStream(file);
  //
  // Request request = new Request(inputStream, new ResourceIdentifier(baseURI,
  // extURI), "POST", mv, qp);
  //
  // Response response = null;
  // response = dispatcher.dispatch(request);
  // assertNotNull(response);
  // assertEquals(HTTPStatus.OK, response.getStatus());
  //
  // final Memberships membershipsJAXB = (Memberships)response.getEntity();
  //    
  //    
  // MembershipHandler mHandler = orgService.getMembershipHandler();
  // Collection<Membership> mms = mHandler.findMembershipsByUser(username);
  // System.out.println("size: " + mms.size());
  //        
  // assertEquals(membershipsJAXB.getMemberships().size(), mms.size());
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //    
  // }

  public void testFindUsers() {
    try {
      UserHandler hUser = orgService.getUserHandler();

      MultivaluedMetadata mv = new MultivaluedMetadata();
      MultivaluedMetadata qp = new MultivaluedMetadata();
      // admin - user from DummyOrganizationService
      String username = "admin";

      String extURI = "/organization/xml/userlike/";
      File file = new File("src/main/resources/xml/user.xml");
      InputStream inputStream = new FileInputStream(file);
      Request request = new Request(inputStream,
                                    new ResourceIdentifier(baseURI, extURI),
                                    "POST",
                                    mv,
                                    qp);
      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());
      generated.Users usersJAXB = (generated.Users) response.getEntity();
      assertNotNull(usersJAXB);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void testGetAllGroup() throws Exception {

    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();

    String group_exclude = "";
    qp.putSingle("filter", group_exclude);

    String extURI = "/organization/xml/groupsall/";

    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());
    Groups groupsJAXB = (Groups) response.getEntity();

    GroupHandler hGroup = orgService.getGroupHandler();
    Collection<Group> groups = hGroup.getAllGroups();

    assertEquals(groups.size(), groupsJAXB.getGroups().size());

  }

  public void testGetGroup() throws Exception {
    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();

    String group_id = "/admin";

    String extURI = "/organization/xml/group/" + "admin/";

    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());

    GroupHandler hGroup = orgService.getGroupHandler();
    UserHandler hUser = orgService.getUserHandler();

    Collection<org.exoplatform.services.organization.User> members = hUser.findUsersByGroup(group_id)
                                                                          .getAll();
    Group group = hGroup.findGroupById(group_id);

    org.exoplatform.services.organization.rest.GroupInfo gr = new org.exoplatform.services.organization.rest.GroupInfo();
    gr.setGroup(group);
    gr.setMembers(members);

    generated.GroupInfo entity = (GroupInfo) response.getEntity();

    assertEquals(gr.getGroup().getId(), entity.getGroup().getId());
    assertEquals(gr.getMembers().size(), entity.getMembers().getMembers().size());

  }

  public void testGetGroupsCount() {
    try {
      MultivaluedMetadata mv = new MultivaluedMetadata();

      String extURI = "/organization/xml/groupcount/";

      Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, null);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());

      final String entry = (String) response.getEntity();
      InputStream inputStream = new ByteArrayInputStream(entry.getBytes());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(inputStream);
      GroupHandler hGroup = orgService.getGroupHandler();
      int quantity = hGroup.getAllGroups().size();
      String number = document.getElementsByTagName("count").item(0).getTextContent();
      assertEquals(quantity, Integer.parseInt(number));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testGetGroupsOfUser() {
    try {
      MultivaluedMetadata mv = new MultivaluedMetadata();
      MultivaluedMetadata qp = new MultivaluedMetadata();

      String username = "admin";

      String extURI = "/organization/xml/groups-of-user/" + username + "/";

      Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());
      Groups groupsJAXB = (Groups) response.getEntity();
      GroupHandler hGroup = orgService.getGroupHandler();
      Collection<Group> groups = hGroup.findGroupsOfUser(username);
      int size = groupsJAXB.getGroups().size();
      assertEquals(size, groups.size());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void testGetUser() {
    try {
      MultivaluedMetadata mv = new MultivaluedMetadata();

      String username = "admin";

      String extURI = "/organization/xml/user/" + username + "/";

      Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, null);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());

      generated.User userJAXB = (generated.User) response.getEntity();

      UserHandler hUser = orgService.getUserHandler();
      org.exoplatform.services.organization.User user = hUser.findUserByName(username);
      assertEquals(user.getUserName(), userJAXB.getUserName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testGetUsers() throws Exception {
    MultivaluedMetadata mv = new MultivaluedMetadata();

    String extURI = "/organization/xml/users/";

    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, null);

    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());
    generated.Users usersJAXB = (generated.Users) response.getEntity();

    UserHandler hUser = orgService.getUserHandler();
    Collection<User> user_list = hUser.findUsers(new Query()).getAll();

    assertEquals(usersJAXB.getUsers().size(), user_list.size());

  }

  public void testGetUsersCount() {
    try {
      MultivaluedMetadata mv = new MultivaluedMetadata();

      String extURI = "/organization/xml/usercount/";

      Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, null);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());

      final String entry = (String) response.getEntity();
      System.out.println(entry);
      InputStream inputStream = new ByteArrayInputStream(entry.getBytes());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(inputStream);

      UserHandler hUser = orgService.getUserHandler();
      int quantity = hUser.findUsers(new Query()).getAll().size();

      String number = document.getElementsByTagName("count").item(0).getTextContent();

      assertEquals(quantity, Integer.parseInt(number));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testUsersRange() {
    try {
      MultivaluedMetadata mv = new MultivaluedMetadata();

      Integer offset = 0, amount = 5;
      String extURI = String.format("/organization/xml/userrange/%s/%s/", offset, amount);
      File file = new File("src/main/resources/xml/user.xml");
      InputStream inputStream = new FileInputStream(file);
      Request request = new Request(inputStream,
                                    new ResourceIdentifier(baseURI, extURI),
                                    HTTPMethods.POST,
                                    mv,
                                    null);

      Response response = null;
      response = dispatcher.dispatch(request);
      assertNotNull(response);
      assertEquals(HTTPStatus.OK, response.getStatus());

      final generated.Users usersJAXB = (generated.Users) response.getEntity();

      UserHandler userHandler = orgService.getUserHandler();

      List<org.exoplatform.services.organization.User> list = userHandler.findUsers(new Query())
                                                                         .getAll();
      int prevFrom = -1;
      if (offset > 0)
        prevFrom = ((offset - amount) > 0) ? offset - amount : 0;
      int nextFrom = ((offset + amount) < list.size()) ? offset + amount : -1;
      int to = (offset + amount < list.size()) ? offset + amount : list.size();

      int size = list.subList(offset, to).size();

      int user_list_size = usersJAXB.getUsers().size();

      assertEquals(size, user_list_size);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // /////////////////////////////////////////////

  // public void testCreateGroup(){
  // try{
  // System.out.println("=========================== testCreateGroup
  // ===========================");
  // MultivaluedMetadata mv = new MultivaluedMetadata();
  // String extURI = "/organization/xml/newgroup/";
  // File file = new File("src/main/resources/xml/group.xml");
  // InputStream inputStream = new FileInputStream(file);
  // Request request = new Request(inputStream, new ResourceIdentifier(baseURI,
  // extURI), "POST", mv, null);
  // Response response = null;
  // response = dispatcher.dispatch(request);
  // assertNotNull(response);
  // assertEquals(HTTPStatus.OK, response.getStatus());
  //      
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //    
  // }

}
