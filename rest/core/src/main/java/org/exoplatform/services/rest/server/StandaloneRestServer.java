/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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

  private static final Log LOGGER = ExoLogger.getLogger("StandaloneRestServer");
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
  private final List < String > filterURIPatterns;
  private final List < String > filters;
  private Server server;
  
  public StandaloneRestServer(InitParams initParams) {
    PropertiesParam params = initParams.getPropertiesParam("standalone-rest-server.properties");

    Iterator < Property > iterator = params.getPropertyIterator();
    filters = new ArrayList < String >();
    filterURIPatterns = new ArrayList < String >();
    for (int i= 0; iterator.hasNext(); i++) {
      Property f = iterator.next();
      String pname = f.getName();
      if (pname.startsWith(CONFIG_FILTER_NAME_PREFIX)) {
        filters.add(f.getValue());
        LOGGER.info("Filter: " + f.getValue());
        String fmap = params.getProperty(CONFIG_FILTER_MAPPING_PREFIX
        		+ pname.substring(CONFIG_FILTER_NAME_PREFIX.length()));
        filterURIPatterns.add((fmap != null) ? fmap : "/*");
        LOGGER.info("Filter mapping: " + fmap);
      }
    }
    
    port = (params.getProperty(CONFIG_SERVER_PORT) != null) ?
        Integer.valueOf(params.getProperty(CONFIG_SERVER_PORT)) : 8080;
    LOGGER.debug("port: " + port);
    
    servletContextPath = params.getProperty(CONFIG_CONTEXT_PATH);
    if (servletContextPath == null) {
      throw new NullPointerException("ContextPath is null");
    }
    LOGGER.debug("RestServlet ContextPath: " + servletContextPath);

    servletURIPattern = (params.getProperty(CONFIG_SERVLET_MAPPING) != null) ?
        params.getProperty(CONFIG_SERVLET_MAPPING) : "/*";
    LOGGER.debug("RestServlet URI pattern: " + servletURIPattern);

    authentication = (params.getProperty(CONFIG_AUTHENTICATION) != null) ?
        params.getProperty(CONFIG_AUTHENTICATION) : "BASIC";
    LOGGER.debug("Authentication: " + authentication);
    
    loginModuleName = (params.getProperty(CONFIG_LOGIN_MODULE_NAME) != null) ?
        params.getProperty(CONFIG_LOGIN_MODULE_NAME) : "exo-domain";
    LOGGER.debug("Login module: " + loginModuleName);
    
    securityConstraintURIPattern = (params.getProperty(CONFIG_SECURITY_CONSTRAINT_MAPPING) != null) ? params
        .getProperty(CONFIG_SECURITY_CONSTRAINT_MAPPING) : "/*";
    LOGGER.debug("Security constraint URI pattern: " + securityConstraintURIPattern);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.picocontainer.Startable#start()
   */
  public void start() {
    server = createServer();
    try {
      server.start();
      LOGGER.info("StandaloneRestServer started.");
    } catch (Exception e) {
      LOGGER.error("Start StandaloneRestServer failed!");
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.picocontainer.Startable#stop()
   */
  public void stop() {
    try {
      server.stop();
      LOGGER.info("StandaloneRestServer stoped.");
    } catch (Exception e) {
      LOGGER.error("Stop StandaloneRestServer failed!");
    }
  }
  
  public Server getServer() {
    return server;
  }

  private Server createServer() {//throws Exception {
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
    jaasUserRealm.setCallbackHandlerClass(DefaultCallbackHandler.class.getName());
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

    /* (non-Javadoc)
     * @see org.mortbay.jetty.plus.jaas.RoleCheckPolicy#checkRole(
     * java.lang.String, java.security.Principal, java.security.acl.Group)
     */
    public boolean checkRole(String role, Principal userPrincipal, Group group) {
      // always true
      return true;
    }
  }

}
