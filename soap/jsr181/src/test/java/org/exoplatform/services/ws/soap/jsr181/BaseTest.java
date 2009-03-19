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
package org.exoplatform.services.ws.soap.jsr181;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $ Nov 5, 2008
 */
public class BaseTest extends TestCase {

  /**
   * Container.
   */
  protected StandaloneContainer container;

  /**
   * Set up.
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    System.out.println(">>> BaseTest.setUp() = entered ");
    StandaloneContainer.setConfigurationPath("src/test/java/conf/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    System.out.println(">>> BaseTest.setUp() container size is = "
        + container.getComponentInstances().size());
  }

}
