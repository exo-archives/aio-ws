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

  private static final Log log = ExoLogger.getLogger("StandaloneRestServer");
  private static final String[] DUMMY_ROLES = new String[] { "user" };
  private static final String REALM = "exo-realm";
  private static final String CONFIG_FILTER_NAME_PREFIX = "filter-class";
  private static final String CONFIG_FILTER_MAPPING_PREFIX = "filter-mapping";
  private static final String CONFIG_SERVER_PORT = "server-port";
  private static final String CONFIG_CONTEXT_PATH = "context-path";
  private static final String CONFIG_SERVLET_MAPPING = "servlet-mapping";
  private static final String CONFIG_AUTHENTICATION = "authentication";
  private static final String CONFIG_LOGIN_MODULE_NAME = "login-module-name";
  private static final String CONFIG_SECURITY_CONSTRAINT_MAPPING = "security-constraint-mapping";
  private final int port;
  private final String authentication;
  private final String loginModuleName;
  private final String servletContextPath;
  private final String servletURIPattern;
  private final String securityConstraintURIPattern;
  private final List<String> filterURIPatterns;
  private final List<String> filters;
  private Server server;

  public StandaloneRestServer(InitParams initParams) {
    PropertiesParam params = initParams
        .getPropertiesParam("standalone-rest-server.properties");

    Iterator<Property> iterator = params.getPropertyIterator();
    filters = new ArrayList<String>();
    filterURIPatterns = new ArrayList<String>();
    for (int i = 0; iterator.hasNext(); i++) {
      Property f = iterator.next();
      String pname = f.getName();
      if (pname.startsWith(CONFIG_FILTER_NAME_PREFIX)) {
        filters.add(f.getValue());
        log.info("Filter: " + f.getValue());
        String fmap = params.getProperty(CONFIG_FILTER_MAPPING_PREFIX +
            pname.substring(CONFIG_FILTER_NAME_PREFIX.length()));
        filterURIPatterns.add((fmap != null) ? fmap : "/*");
        log.info("Filter mapping: " + fmap);
      }
    }

    port = (params.getProperty(CONFIG_SERVER_PORT) != null) ? Integer
        .valueOf(params.getProperty(CONFIG_SERVER_PORT)) : 8080;
    if (log.isDebugEnabled()) {
      log.debug("port: " + port);
    }

    servletContextPath = params.getProperty(CONFIG_CONTEXT_PATH);
    if (servletContextPath == null) {
      throw new NullPointerException("ContextPath is null");
    }
    if (log.isDebugEnabled()) {
      log.debug("RestServlet ContextPath: " + servletContextPath);
    }

    servletURIPattern = (params.getProperty(CONFIG_SERVLET_MAPPING) != null) ? params
        .getProperty(CONFIG_SERVLET_MAPPING)
        : "/*";
    if (log.isDebugEnabled()) {
      log.debug("RestServlet URI pattern: " + servletURIPattern);
    }

    authentication = (params.getProperty(CONFIG_AUTHENTICATION) != null) ? params
        .getProperty(CONFIG_AUTHENTICATION)
        : "BASIC";
    if (log.isDebugEnabled()) {
      log.debug("Authentication: " + authentication);
    }

    loginModuleName = (params.getProperty(CONFIG_LOGIN_MODULE_NAME) != null) ? params
        .getProperty(CONFIG_LOGIN_MODULE_NAME)
        : "exo-domain";
    if (log.isDebugEnabled()) {
      log.debug("Login module: " + loginModuleName);
    }

    securityConstraintURIPattern = (params
        .getProperty(CONFIG_SECURITY_CONSTRAINT_MAPPING) != null) ? params
        .getProperty(CONFIG_SECURITY_CONSTRAINT_MAPPING) : "/*";
    if (log.isDebugEnabled()) {
      log.debug("Security constraint URI pattern: " +
          securityConstraintURIPattern);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.picocontainer.Startable#start()
   */
  public void start() {
    server = createServer();
    try {
      server.start();
      log.info("StandaloneRestServer started.");
    } catch (Exception e) {
      log.error("Start StandaloneRestServer failed!");
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.picocontainer.Startable#stop()
   */
  public void stop() {
    try {
      server.stop();
      log.info("StandaloneRestServer stoped.");
    } catch (Exception e) {
      log.error("Stop StandaloneRestServer failed!");
    }
  }

  public Server getServer() {
    return server;
  }

  private Server createServer() {// throws Exception {
    // create context
    Server server_ = new Server(port);
    Context context = new Context(server_, servletContextPath, Context.SESSIONS);
    context.setSecurityHandler(createSecurityHandler());
    ServletHolder holder = new ServletHolder(new RestServlet());
    context.addServlet(holder, servletURIPattern);
    int i = 0;
    for (String filter : filters) {
      context.addFilter(filter, filterURIPatterns.get(i++), Context.SESSIONS);
    }
    return server_;

  }

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
    jaasUserRealm.setLoginModuleName(loginModuleName);
    jaasUserRealm.setCallbackHandlerClass(DefaultCallbackHandler.class
        .getName());
    jaasUserRealm.setRoleCheckPolicy(new DummyRoleCheckPolicy());
    SecurityHandler securityHandler = new SecurityHandler();
    securityHandler.setUserRealm(jaasUserRealm);
    securityHandler.setConstraintMappings(new ConstraintMapping[] { mapping });
    return securityHandler;
  }

  /**
   * For Servlet Authentication.
   */
  static class DummyRoleCheckPolicy implements RoleCheckPolicy {

    /*
     * (non-Javadoc)
     * @see org.mortbay.jetty.plus.jaas.RoleCheckPolicy#checkRole(
     *      java.lang.String, java.security.Principal, java.security.acl.Group)
     */
    public boolean checkRole(String role, Principal userPrincipal, Group group) {
      // always true
      return true;
    }
  }

}
