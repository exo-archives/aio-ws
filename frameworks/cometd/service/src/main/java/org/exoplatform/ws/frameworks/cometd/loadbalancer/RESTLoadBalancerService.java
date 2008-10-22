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
package org.exoplatform.ws.frameworks.cometd.loadbalancer;

import org.apache.commons.logging.Log;
import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RESTLoadBalancerService implements ResourceContainer {
  /**
   * Class logger.
   */
  private final Log             log = ExoLogger.getLogger("ws.RESTLoadBalancerService");

  /**
   * 
   */
  private LoadBalancer balancer;

  /**
   * @param balancer the strategy for load balancing of cometd cluster.
   */
  public RESTLoadBalancerService(LoadBalancer balancer) {
    this.balancer = balancer;
  }

  /**
   * @param exoid the client id.
   * @return base URL of cometd server in cluster for user.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/cometdurl/{exoid}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response getCometdURL(@URIParam("exoid") String exoid) {
    String url = balancer.connection(exoid);
    if (!(url.equals("") || url.length() == 0)) {
      if (log.isInfoEnabled()) 
        log.info("Client with exoid " + exoid + " get URL " + url + " for cometd connection");
      return Response.Builder.ok(url).build();
    } else { 
      if (log.isErrorEnabled()) 
        log.error("All nodes are owerflow client with exoid " + exoid + " can't connect to cometd!");
      return Response.Builder.forbidden().errorMessage("Owerflow!").build();
    }
  }

  /**
   * @param exoid the client id.
   * @return OK if release successful.
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/releasecometd/{exoid}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response release(@URIParam("exoid") String exoid) {
    if (balancer.release(exoid))
      return Response.Builder.ok().build();
    else 
      return Response.Builder.badRequest().build();
  }

}
