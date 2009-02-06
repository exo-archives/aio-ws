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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.Collection;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.rest.html.RESTOrganizationServiceHTMLImpl;
import org.exoplatform.services.organization.rest.json.RESTOrganizationServiceJSONImpl;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.Request;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.ResourceIdentifier;
import org.exoplatform.services.rest.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class HTMLResponseOrgserviceTest extends TestCase {
  /**
   * Class logger.
   */
  private final Log log = ExoLogger.getLogger("jcr.HTMLResponseOrgserviceTest");
  
  StandaloneContainer             container;

  OrganizationService             orgService;

  RESTOrganizationServiceHTMLImpl htmlOrgService;

  ResourceDispatcher              dispatcher;

  static final String             baseURI = "http://localhost:8080/rest/";

  protected void setUp() throws Exception {
    super.setUp();

    String containerConf = HTMLResponseOrgserviceTest.class.getResource("/conf/standalone/test-configuration.xml")
                                                           .toString();

    StandaloneContainer.setConfigurationURL(containerConf);
    container = StandaloneContainer.getInstance();
    orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    htmlOrgService = (RESTOrganizationServiceHTMLImpl) container.getComponentInstanceOfType(RESTOrganizationServiceHTMLImpl.class);

    dispatcher = (ResourceDispatcher) container.getComponentInstanceOfType(ResourceDispatcher.class);

  }

  
  
  public void testGetGroup() throws Exception {
    MultivaluedMetadata mv = new MultivaluedMetadata();
    MultivaluedMetadata qp = new MultivaluedMetadata();
    String extURI = "/organization/html/group/" + "admin/";
    Request request = new Request(null, new ResourceIdentifier(baseURI, extURI), "GET", mv, qp);
    Response response = null;
    response = dispatcher.dispatch(request);
    assertNotNull(response);
    assertEquals(HTTPStatus.OK, response.getStatus());
    response.writeEntity(System.out);

  }
  
}
