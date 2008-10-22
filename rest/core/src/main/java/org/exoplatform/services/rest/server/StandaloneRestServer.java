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

package org.exoplatform.services.rest.server;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.servlet.RestServlet;

import org.picocontainer.Startable;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.jaas.JAASUserRealm;
import org.mortbay.jetty.plus.jaas.RoleCheckPolicy;
import org.mortbay.jetty.plus.jaas.callback.DefaultCallbackHandler;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Standalone REST server based on Jetty.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StandaloneRestServer implements Startable {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("StandaloneRestServer");
  
  /**
   * User roles list. 
   */
  private static final String[] DUMMY_ROLES = new String[] {"users"};
  
  /**
   * Security domain.
   */
  private static final String REALM = "exo-domain";
  
  /**
   * Prefix for filter declaration in configuration.
   */
  private static final String CONFIG_FILTER_NAME_PREFIX = "filter-class";
  
  /**
   * Prefix for filter mapping declaration in configuration.
   */
  private static final String CONFIG_FILTER_MAPPING_PREFIX = "filter-mapping";
  
  /**
   * Server port description in configuration. 
   */
  private static final String CONFIG_SERVER_PORT = "server-port";
  
  /**
   * Context path for rest servlet. 
   */
  private static final String CONFIG_CONTEXT_PATH = "context-path";
  
  /**
   * Servlet mapping for rest servlet.
   */
  private static final String CONFIG_SERVLET_MAPPING = "servlet-mapping";
  
  /**
   * Authentication type declaration in configuration. 
   */
  private static final String CONFIG_AUTHENTICATION = "authentication";
  
  /**
   * Login module configuration.
   */
  private static final String LOGIN_MODULE = "login-module";
  
  /**
   * Security constraint mapping. 
   */
  private static final String CONFIG_SECURITY_CONSTRAINT_MAPPING = "security-constraint-mapping";
  
  /**
   * Actual server port.
   */
  private final int port;
  
  /**
   * Authentication type. 
   */
  private final String authentication;
  
  /**
   * Login module.
   */
  private final String loginModule;
  
  /**
   * Servlet context.
   */
  private final String servletContextPath;
  
  /**
   * Servlet URL pattern.
   */
  private final String servletURIPattern;
  
  /**
   * Security constraint URL pattern.
   */
  private final String securityConstraintURIPattern;
  
  /**
   * Filters URI paterns.
   */
  private final List<String> filterURIPatterns;
  
  /**
   * Filters.
   */
  private final List<String> filters;
  
  /**
   * Server.
   */
  private Server server;

  /**
   * @param initParams initialize parameters. 
   */
  public StandaloneRestServer(InitParams initParams) {
    PropertiesParam params = initParams.getPropertiesParam("standalone-rest-server.properties");

    Iterator<Property> iterator = params.getPropertyIterator();
    filters = new ArrayList<String>();
    filterURIPatterns = new ArrayList<String>();
    
    for (int i = 0; iterator.hasNext(); i++) {
      Property f = iterator.next();
      String pname = f.getName();
      if (pname.startsWith(CONFIG_FILTER_NAME_PREFIX)) {
        filters.add(f.getValue());
        LOG.info("Filter: " + f.getValue());
        String fmap = params.getProperty(CONFIG_FILTER_MAPPING_PREFIX
            +  pname.substring(CONFIG_FILTER_NAME_PREFIX.length()));
        filterURIPatterns.add(fmap != null ? fmap : "/*");
        LOG.info("Filter mapping: " + fmap);
      }
    }

    port = params.getProperty(CONFIG_SERVER_PORT) != null
        ? Integer.valueOf(params.getProperty(CONFIG_SERVER_PORT)) : 8080;
    if (LOG.isDebugEnabled()) {
      LOG.debug("port: " + port);
    }

    servletContextPath = params.getProperty(CONFIG_CONTEXT_PATH);
    if (servletContextPath == null) {
      throw new NullPointerException("ContextPath is null");
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("RestServlet ContextPath: "
          + servletContextPath);
    }

    servletURIPattern = params.getProperty(CONFIG_SERVLET_MAPPING) != null
        ? params.getProperty(CONFIG_SERVLET_MAPPING) : "/*";
    if (LOG.isDebugEnabled()) {
      LOG.debug("RestServlet URI pattern: " + servletURIPattern);
    }

    authentication = params.getProperty(CONFIG_AUTHENTICATION) != null
        ? params.getProperty(CONFIG_AUTHENTICATION) : "BASIC";
    if (LOG.isDebugEnabled()) {
      LOG.debug("Authentication: " + authentication);
    }

    loginModule = params.getProperty(LOGIN_MODULE) != null
        ? params.getProperty(LOGIN_MODULE) : "exo-domain";
    if (LOG.isDebugEnabled()) {
      LOG.debug("Login module: " + loginModule);
    }

    securityConstraintURIPattern = params.getProperty(CONFIG_SECURITY_CONSTRAINT_MAPPING) != null
        ? params.getProperty(CONFIG_SECURITY_CONSTRAINT_MAPPING) : "/*";
    if (LOG.isDebugEnabled()) {
      LOG.debug("Security constraint URI pattern: "
          + securityConstraintURIPattern);
    }
  }

  /**
   * Server starter.
   * {@inheritDoc}
   */
  public void start() {
    server = createServer();
    try {
      server.start();
      LOG.info("StandaloneRestServer started.");
    } catch (Exception e) {
      LOG.error("Start StandaloneRestServer failed!");
      e.printStackTrace();
    }
  }

  /**
   * Server destroy.
   * {@inheritDoc}
   */
  public void stop() {
    try {
      server.stop();
      LOG.info("StandaloneRestServer stoped.");
    } catch (Exception e) {
      LOG.error("Stop StandaloneRestServer failed!");
    }
  }

  /**
   * @return instance of server.
   */
  public Server getServer() {
    return server;
  }

  /**
   * @return create server instance.
   */
  private Server createServer() {
    Server ser = new Server(port);
    Context context = new Context(ser, servletContextPath, Context.SESSIONS);
    context.setSecurityHandler(createSecurityHandler());
    ServletHolder holder = new ServletHolder(new RestServlet());
    context.addServlet(holder, servletURIPattern);
    int i = 0;
    for (String filter : filters) {
      context.addFilter(filter, filterURIPatterns.get(i++), Context.SESSIONS);
    }
    return ser;

  }

  /**
   * @return security handler for server.
   */
  private SecurityHandler createSecurityHandler() {
    // create security constraint for RestServlet
    Constraint constraint = new Constraint();
    constraint.setName(authentication);
    constraint.setRoles(DUMMY_ROLES);
    constraint.setAuthenticate(true);
    ConstraintMapping mapping = new ConstraintMapping();
    mapping.setPathSpec(securityConstraintURIPattern);
    mapping.setConstraint(constraint);
    // create realm and security handler for RestServlet
    JAASUserRealm jaasUserRealm = new JAASUserRealm(REALM);
    jaasUserRealm.setLoginModuleName(loginModule);
    jaasUserRealm.setCallbackHandlerClass(DefaultCallbackHandler.class
        .getName());
    jaasUserRealm.setRoleCheckPolicy(new DummyRoleCheckPolicy());
    SecurityHandler securityHandler = new SecurityHandler();
    securityHandler.setUserRealm(jaasUserRealm);
    securityHandler.setConstraintMappings(new ConstraintMapping[] {mapping});
    return securityHandler;
  }

  /**
   * For Servlet Authentication.
   */
  static class DummyRoleCheckPolicy implements RoleCheckPolicy {

    /**
     * Check user roles.
     * {@inheritDoc}
     */
    public boolean checkRole(String role, Principal userPrincipal, Group group) {
      // always true
      return true;
    }
  }

}
