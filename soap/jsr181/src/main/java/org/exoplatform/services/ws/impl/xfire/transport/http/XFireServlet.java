/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.ws.impl.xfire.transport.http;

import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.logging.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.http.XFireServletController;

import org.exoplatform.services.log.ExoLogger;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class XFireServlet extends HttpServlet {

  public final static String XFIRE_INSTANCE = "xfire.instance";
  protected XFire xfire;
  protected XFireServletController controller;

  private final static Log logger = ExoLogger.getLogger(XFireServlet.class);

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#init()
   */
  public void init() throws ServletException {
    try {
      super.init();
      xfire = createXFire();
      controller = createController();

      logger.info(xfire);
      logger.info(controller);
    } catch (Exception e) {
      logger.error("Error initializing XFireServlet.", e);
      throw new ServletException("Error initializing XFireServlet.", e);
    }
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // logger.info(">>>>> doGet called");
    controller.doService(request, response);
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    // logger.info(">>>>> doPost called");
    controller.doService(req, res);
  }

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#destroy()
   */
  public void destroy() {
    logger.debug("Destroying Servlet");
    // Ensure all transports are closed
    for (Iterator < ? > iterator =
      xfire.getTransportManager().getTransports().iterator(); iterator.hasNext();) {
      
      Transport transport = (Transport) iterator.next();
      transport.dispose();
    }
    super.destroy();
  }

  /**
   * Create new instance of XFire.
   * @return XFire instance.
   * @throws ServletException if something going wrong.
   */
  private XFire createXFire() throws ServletException {
    try {
      if (getServletContext().getAttribute(XFIRE_INSTANCE) != null) {
        return (XFire) getServletContext().getAttribute(XFIRE_INSTANCE);
      }
      XFireFactory factory = XFireFactory.newInstance();
      return factory.getXFire();
    } catch (Exception e) {
      throw new ServletException("Couldn't start XFire.", e);
    }
  }

  /**
   * Create new instance of XFireServletController.
   * @return XfireServletController instance.
   */
  private XFireServletController createController() {
    return new XFireServletController(xfire, getServletContext());
  }
}
