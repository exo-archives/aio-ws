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

package org.exoplatform.services.organization.rest.html;

import generated.Membership;
import generated.User;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.rest.xml.RESTOrganizationServiceXMLImpl;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.QueryParam;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.JAXBInputTransformer;
import org.exoplatform.services.rest.transformer.JAXBOutputTransformer;
import org.exoplatform.services.rest.transformer.XSLT4SourceOutputTransformer;
import org.exoplatform.services.rest.transformer.XSLTConstants;

/**
 * Created by The eXo Platform SAS .
 * 
 * Note: we use
 * 
 * @QueryTemplate annotation to simplify XSL
 * @author Gennady Azarenkov
 * @version $Id:$
 */
@URITemplate("/organization/html/")
public class RESTOrganizationServiceHTMLImpl implements ResourceContainer {

  /**
   * Logger.
   */
  private static final Log    LOGGER = ExoLogger.getLogger(RESTOrganizationServiceHTMLImpl.class);

  /**
   * 
   */
  private RESTOrganizationServiceXMLImpl xmlImpl;

  /**
   * 
   */
  protected static final String  GROUP_INFO_SCHEMA           = "group-info";

  /**
   * 
   */
  protected static final String  GROUP_EDIT_SCHEMA           = "group-edit";

  /**
   * 
   */
  protected static final String  GROUPS_LIST_SCHEMA          = "groups-list";

  /**
   * 
   */
  protected static final String  GROUPS_LIST_FRAGMENT_SCHEMA = "groups-list-fragment";

  /**
   * 
   */
  protected static final String  USER_INFO_SCHEMA            = "user-info";

  /**
   * 
   */
  /**
   * 
   */
  protected static final String  USERS_LIST_SCHEMA           = "users-list";

  /**
   * 
   */
  protected static final String  USERS_LIST_SEARCH_SCHEMA    = "users-list-search";

  /**
   * 
   */
  protected static final String  MEMBERSHIP_LIST_SCHEMA      = "memberships-list";
  
  /**
   * 
   */
  protected static final String  MEMBERSHIPTYPE_LIST_SCHEMA      = "membershiptypes-list";

  /**
   * @param organizationService implementation of OrganizationService.
   */
  public RESTOrganizationServiceHTMLImpl(OrganizationService organizationService) {
    xmlImpl = new RESTOrganizationServiceXMLImpl(organizationService);
  }


  /**
   * @param membershipId the membership ID
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/membership/{membershipId}/")
  @OutputTransformer(XSLT4SourceOutputTransformer.class)
  public Response findMembership(@URIParam("membershipId")  String membershipId) {

    Response response = xmlImpl.findMembership(membershipId);

    if (response.getStatus() != HTTPStatus.OK)
      return response;
    try {
      return modifyResponse(response, MEMBERSHIP_LIST_SCHEMA);
    } catch (IOException e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR)
                             .errorMessage("Thrown exception : " + e)
                             .build();
    }
  }

   /**
   * @param membreshipsJAXB the object generated.Membership with field for search
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.POST)
   @URITemplate("/memberships/")
   @InputTransformer(JAXBInputTransformer.class)
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response   findMemberships(Membership membreshipsJAXB) {
   Response response = xmlImpl.findMemberships(membreshipsJAXB);
   if (response.getStatus() != HTTPStatus.OK)
     return response;
   try {
     return modifyResponse(response, MEMBERSHIP_LIST_SCHEMA);
   } catch (IOException e) {
     e.printStackTrace();
     return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
   }
   }
  
   /**
   * @param userJAXB the object generated.User with field for search
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.POST)
   @URITemplate("/userlike/")
   @InputTransformer(JAXBInputTransformer.class)
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response  findUsers(User userJAXB) {
     Response response = xmlImpl.findUsers(userJAXB);
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, USERS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
       "Thrown exception : " + e).build();
     }
   }
  
   /**
   * @param offset the down limit
   * @param amount the top limit
   * @param userJAXB the object generated.User with field for search
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.POST)
   @URITemplate("/userrange/{from}/{to}/")
   @InputTransformer(JAXBInputTransformer.class)
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response findUsersRange(@URIParam("from")  Integer offset,
                                    @URIParam("to")   Integer amount,
                                    User userJAXB) {
     Response response = xmlImpl.findUsersRange(offset, amount, userJAXB);
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, USERS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
       "Thrown exception : " + e).build();
     }
   }
  
   /**
   * @param filter the filter for search
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/groupfilter/{filter}/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response  getFilteredGroup(@URIParam("filter")  String filter) {
     Response response = xmlImpl.getFilteredGroup(filter);
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, GROUPS_LIST_SCHEMA);
     } catch (IOException e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
    }
   }
  
  /**
   * @param groupId the group id
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/group/{groupId}/")
  @OutputTransformer(XSLT4SourceOutputTransformer.class)
  public Response getGroup(@URIParam("groupId")  String groupId) {
    Response response = xmlImpl.getGroup(groupId);
    if (response.getStatus() != HTTPStatus.OK)
      return response;
    try {
      String schemaName = GROUP_INFO_SCHEMA;
      return modifyResponse(response, schemaName);
    } catch (IOException e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e)
                             .build();
    }
  }

  
  /**
   * @return Response object with HTTP status.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/groupsall/")
  @OutputTransformer(XSLT4SourceOutputTransformer.class)
  public Response getGroups() {
    Response response = xmlImpl.getAllGroup();
    if (response.getStatus() != HTTPStatus.OK)
      return response;
    try {
      return modifyResponse(response, GROUPS_LIST_SCHEMA);
    } catch (IOException e) {
      e.printStackTrace();
      return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR)
                             .errorMessage("Thrown exception : " + e)
                             .build();
    }
  }

  
   /**
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/group/count/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response getGroupsCount() {
     Response response = xmlImpl.getGroupsCount();
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, GROUPS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
     }
   }
  
   /**
   * @param username the user name
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/groups-of-user/{username}/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response getGroupsOfUser(@QueryParam("username")  String username) {
     Response response = xmlImpl.getGroupsOfUser(username);
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, GROUPS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
     }
   }
  
   /**
   * @param offset the down limit
   * @param amount the top limit
   * @param parentId the id of parent node
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/grouprange/{parentId}/{from}/{to}/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response getGroupsRange(@URIParam("from")  Integer offset, 
                                  @URIParam("to")  Integer amount,
                                  @QueryParam("parentId")   String parentId) {
   Response response = xmlImpl.getGroupsRange(parentId, offset, amount);
   if (response.getStatus() != HTTPStatus.OK)
     return response;
     try {
       return modifyResponse(response, GROUPS_LIST_FRAGMENT_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
     }
   }
    
  
   /**
   * @param username the user name
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/user/{username}/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response getUser(@URIParam("username")   String username) {
     Response response = xmlImpl.getUser(username);
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, USER_INFO_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
     return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
       "Thrown exception : " + e).build();
     }
   }
  
   /**
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/users/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response  getUsers() {
     Response response = xmlImpl.getUsers();
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, USERS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage(
        "Thrown exception : " + e).build();
     }
   }
  
   /**
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/user/count/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response getUsersCount() {
     Response response = xmlImpl.getUsersCount();
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, GROUPS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
     }
   }
  
   /**
   * @param offset the down limit
   * @param amount the top limit
   * @return Response object with HTTP status.
   */
   @HTTPMethod(HTTPMethods.GET)
   @URITemplate("/user/view-range/{from}/{number}/")
   @OutputTransformer(XSLT4SourceOutputTransformer.class)
   public Response   getUsersRange(@URIParam("from")  Integer offset,
                                   @URIParam("number") Integer amount) {
     Response response = xmlImpl.getUsersRange(offset, amount);
     if (response.getStatus() != HTTPStatus.OK)
       return response;
     try {
       return modifyResponse(response, USERS_LIST_SCHEMA);
     } catch (IOException e) {
       e.printStackTrace();
       return Response.Builder.withStatus(HTTPStatus.INTERNAL_ERROR).errorMessage("Thrown exception : " + e).build();
     }
   }
  
  /**
   * @param response the response for modify
   * @param xsltSchemaName XSLT schema name for transformation XML to HTML 
   * @return modify response 
   * @throws IOException throw IOException
   */
  private Response modifyResponse(final Response response, String xsltSchemaName) throws IOException {
    response.setTransformer(new JAXBOutputTransformer());
    final PipedOutputStream po = new PipedOutputStream();
    final PipedInputStream pi = new PipedInputStream(po);
    response.writeEntity(System.out); 
    new Thread() {
      public void run() {
        try {
          response.writeEntity(po);
        } catch (IOException e) {
          LOGGER.error("Thrown exception : " + e);
        } finally {
          try {
            po.flush();
            po.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
    StreamSource s = new StreamSource(pi);
    Map<String, String> p = new HashMap<String, String>();
    p.put(XSLTConstants.XSLT_TEMPLATE, xsltSchemaName);
    return Response.Builder.ok(s, "text/html").setTransformerParameters(p).build();
  }
}
