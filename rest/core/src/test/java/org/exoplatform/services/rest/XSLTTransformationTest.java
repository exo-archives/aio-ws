/**
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

package org.exoplatform.services.rest;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.container.XSLTResourceContainer;
import org.exoplatform.services.xml.transform.trax.TRAXTemplatesService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class XSLTTransformationTest extends TestCase {
  
  private StandaloneContainer container_;
  private TRAXTemplatesService templatesService_;
  private ResourceBinder binder_;
  private ResourceDispatcher dispatcher_;
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container_ = StandaloneContainer.getInstance();
    templatesService_ = (TRAXTemplatesService) container_
        .getComponentInstanceOfType(TRAXTemplatesService.class);
    assertNotNull(templatesService_);
    binder_ = (ResourceBinder) container_
        .getComponentInstanceOfType(ResourceBinder.class);
    assertNotNull(binder_);
    dispatcher_ = (ResourceDispatcher) container_
        .getComponentInstanceOfType(ResourceDispatcher.class);
    assertNotNull(dispatcher_);
  }
  
  public void testGetTemplates() {
    assertNotNull(templatesService_.getTemplates("book"));
  }
  
  public void testTransformation() throws Exception {
    XSLTResourceContainer xr = new XSLTResourceContainer();
    binder_.bind(xr);
    Request req = new Request(null, new ResourceIdentifier("/test/xslt/book/"), "GET",
        new MultivaluedMetadata(), new MultivaluedMetadata());
    Response res  = dispatcher_.dispatch(req);
    res.writeEntity(System.out);
    binder_.unbind(xr);
  }
  
  public void testTransformation2() throws Exception {
    XSLTResourceContainer xr = new XSLTResourceContainer();
    binder_.bind(xr);
    Request req = new Request(null, new ResourceIdentifier("/test/xslt2/test/"), "GET",
        new MultivaluedMetadata(), new MultivaluedMetadata());
    Response res  = dispatcher_.dispatch(req);
    res.writeEntity(System.out);
    binder_.unbind(xr);
  }

}

