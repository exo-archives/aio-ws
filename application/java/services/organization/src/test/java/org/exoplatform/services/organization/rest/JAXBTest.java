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
package org.exoplatform.services.organization.rest;

import generated.Group;
import generated.Groups;
import generated.Membership;
import generated.MembershipType;
import generated.Memberships;
import generated.User;
import generated.Users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.rest.xml.RESTOrganizationServiceXMLImpl;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.w3c.dom.Document;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class JAXBTest extends TestCase {
  /**
   * Class logger.
   */
  private final Log log = ExoLogger.getLogger("jcr.TestJAXB");
  
  
  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testMarshalingUser(){
    System.out.println("======================== testMarshalingUser() ======================== ");
    User user = new User();
    user.setEmail("gam@mail.com");
    user.setFirstName("Root");
    user.setLastName("exo");
    user.setUserName("root");
    try{
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(user.getClass());
      context.createMarshaller().marshal(user, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void testUnmarshalingUser(){
    try{
      System.out.println("\n ======================== testUnMarshalingUser() ======================== ");
      File file = new File("src/main/resources/xml/user.xml"); 
      InputStream inputStream = new FileInputStream(file);
      JAXBContext context = JAXBContext.newInstance(User.class);
      User user = (User) context.createUnmarshaller().unmarshal(inputStream);
      assertNotNull(user);
    }catch (Exception e) {
      e.printStackTrace();
    }

  }
  
  public void testMarshalingUsers(){
    System.out.println("\n ======================== testMarshalingUsers() ======================== ");
    generated.Users users = new generated.Users();
    User user = new User();
    user.setEmail("gam@mail.com");
    user.setFirstName("Root");
    user.setLastName("exo");
    user.setUserName("root");
    users.getUsers().add(user);
    user.setEmail("demo@mail.com");
    user.setFirstName("Demo");
    user.setLastName("exo");
    user.setUserName("demo");
    users.getUsers().add(user);
    try{
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(users.getClass());
      context.createMarshaller().marshal(users, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void testUnmarshalUsers(){
    try{
      System.out.println("======================== testUnMarshalingUser() ======================== ");
      File file = new File("src/main/resources/xml/users.xml"); 
      InputStream inputStream = new FileInputStream(file);
      JAXBContext context = JAXBContext.newInstance(Users.class);
      Users users = (Users) context.createUnmarshaller().unmarshal(inputStream);
      assertNotNull(users);
    }catch (Exception e) {
      // TODO: handle exception
    }
  }
  
  
  public void testMarshalingGroup(){
    System.out.println("\n ======================= testMarshalingGroup ========================");
    Group group = new Group();
    group.setDescription("description");
    group.setId("gfdhfghdgfhd");
    group.setLabel("label");
    group.setName("name");
    group.setParentId("/exo");
    try{
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(group.getClass());
      context.createMarshaller().marshal(group, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  
  public void testUnmarshalGroup(){
    try{
      System.out.println("\n ====================== testUnmarshalGroup =======================");
      File file = new File("src/main/resources/xml/group.xml"); 
      InputStream inputStream = new FileInputStream(file);
      JAXBContext context = JAXBContext.newInstance(Group.class);
      Group group =  (Group) context.createUnmarshaller().unmarshal(inputStream);
      assertNotNull(group);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  
  public void testMarshalingGroups(){
    System.out.println("\n ============================ testMarshalingGroups ========================");
    Groups groups = new Groups();
    Group group = new Group();
//    Members members = new Members();
//    User user = new User();
//    user.setEmail("gam@mail.com");
//    user.setFirstName("Root");
//    user.setLastName("exo");
//    user.setUserName("root");
//    members.getMembers().add(user);
//    user.setEmail("demo@mail.com");
//    user.setFirstName("Demo");
//    user.setLastName("exo");
//    user.setUserName("demo");
//    members.getMembers().add(user);
//    group.setMembers(members);
    group.setDescription("description");
    group.setId("gfdhfghdgfhd");
    group.setLabel("label");
    group.setName("name");
    group.setParentId("/exo");
    groups.getGroups().add(group);
    try{
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(groups.getClass());
      context.createMarshaller().marshal(groups, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  
  public void testUnmarshalGroups(){
    try{
      System.out.println("\n ========================== testUnmarshalGroups =======================");
      File file = new File("src/main/resources/xml/groups.xml"); 
      InputStream inputStream = new FileInputStream(file);
      JAXBContext context = JAXBContext.newInstance(Groups.class);
      Groups groups =  (Groups) context.createUnmarshaller().unmarshal(inputStream);
      assertNotNull(groups);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  public void testMarshalingMembership(){
    System.out.println("\n ========================== testMarshalingMembership =======================");
    Membership membership = new Membership();
    membership.setGroupId("group");
    membership.setId("111");
    membership.setMembershipType("type-78");
    membership.setUserName("root");
    try{
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(membership.getClass());
      context.createMarshaller().marshal(membership, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  public void testMarshalingMemberships(){
    System.out.println("\n ================= testMarshalingMemberships =======================");
    Memberships memberships = new Memberships();
    Membership membership = new Membership();
    membership.setGroupId("group");
    membership.setId("111");
    membership.setMembershipType("type-78");
    membership.setUserName("root");
    memberships.getMemberships().add(membership);
    Membership membership2 = new Membership();
    membership2.setGroupId("group2");
    membership2.setId("222");
    membership2.setMembershipType("type-78");
    membership2.setUserName("demo");
    memberships.getMemberships().add(membership2);
    try{
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(memberships.getClass());
      context.createMarshaller().marshal(memberships, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void testUnmarshalingMmemberships(){
    try{
      System.out.println("\n ========================== testUnmarshalingMmemberships =======================");
      File file = new File("src/main/resources/xml/memberships.xml"); 
      InputStream inputStream = new FileInputStream(file);
      JAXBContext context = JAXBContext.newInstance(Memberships.class);
      Memberships memberships =  (Memberships) context.createUnmarshaller().unmarshal(inputStream);
      assertNotNull(memberships);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  public void testMarshalingMembershipType(){
    try{
      System.out.println("\n ================= testMarshalingMembershipType =======================");
      GregorianCalendar gregorianCalendar = new GregorianCalendar();
      XMLGregorianCalendar calendar = DatatypeFactory.newInstance()
          .newXMLGregorianCalendar(gregorianCalendar);
      MembershipType membershipType = new MembershipType();
      membershipType.setCreatedDate(calendar);
      membershipType.setDescription("description");
      membershipType.setModifiedDate(calendar);
      membershipType.setName("name");
      membershipType.setOwner("root");
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JAXBContext context = JAXBContext.newInstance(membershipType.getClass());
      context.createMarshaller().marshal(membershipType, outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
      assertNotNull(document);
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void testUnmarshalingMmembershipType(){
    try{
      System.out.println("\n ========================== testUnmarshalingMmembershipType =======================");
      File file = new File("src/main/resources/xml/membershiptype.xml"); 
      InputStream inputStream = new FileInputStream(file);
      JAXBContext context = JAXBContext.newInstance(MembershipType.class);
      MembershipType membershipType =  (MembershipType) context.createUnmarshaller().unmarshal(inputStream);
      assertNotNull(membershipType);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

}
