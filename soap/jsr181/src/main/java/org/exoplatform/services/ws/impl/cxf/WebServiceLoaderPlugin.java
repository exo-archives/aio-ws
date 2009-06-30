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
package org.exoplatform.services.ws.impl.cxf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS .
 * 
 * Usage:
 *  &lt;external-component-plugins&gt;
 *    &lt;target-component&gt;org.exoplatform.services.ws.impl.cxf.WebServiceLoader&lt;/target-component&gt;
 *    &lt;component-plugin&gt;
 *      &lt;name&gt;WebService loader plugin&lt;/name&gt;
 *      &lt;set-method&gt;addPlugin&lt;/set-method&gt;
 *      &lt;type&gt;org.exoplatform.services.ws.impl.cxf.WebServiceLoaderPlugin&lt;/type&gt;
 *      &lt;description&gt;Custom service loader plugin&lt;/description&gt;
 *      &lt;init-params&gt;
 *        &lt;values-param&gt;
 *          &lt;name&gt;&lt;/name&gt;
 *          &lt;value&gt;org.exoplatform.services.MySimpleService&lt;/value&gt;
 *        &lt;/values-param&gt;
 *      &lt;/init-params&gt;
 *    &lt;/component-plugin&gt;
 *  &lt;/external-component-plugins&gt;
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $ Nov 4, 2008
 */
public class WebServiceLoaderPlugin extends BaseComponentPlugin {
  private static final Log     LOG = ExoLogger.getLogger(WebServiceLoaderPlugin.class);

  private final List<Class<?>> jcs = new ArrayList<Class<?>>();

  /**
   * @param params initialize parameters
   * @see InitParams
   */
  @SuppressWarnings("unchecked")
  public WebServiceLoaderPlugin(InitParams params) {

    Iterator<ValuesParam> vparams = params.getValuesParamIterator();
    if (vparams.hasNext()) {
      ValuesParam nodeTypeParam = vparams.next();

      List<String> list = (ArrayList<String>) nodeTypeParam.getValues();

      if (list != null) {
        Iterator<String> i = list.iterator();
        while (i.hasNext()) {
          String fqn = i.next();
          try {
            Class c = Class.forName(fqn);
            jcs.add(c);
            LOG.info("Loaded class " + c.getName());
          } catch (ClassNotFoundException e) {
            LOG.warn("Can't find for class with name " + fqn, e);
          }
        }
      }
    }
  }

  public List<Class<?>> getJcs() {
    return jcs;
  }

}
