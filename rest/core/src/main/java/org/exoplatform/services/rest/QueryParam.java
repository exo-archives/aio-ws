/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * QueryParam define the names of query parameters from HTTP request. In this
 * way ResourceContainer gets only query parameters wich it needs. For example:
 * for query string: ?param1=abc&param2=cba
 * 
 * <pre>
 * ...
 * public getMethod(@QueryParam(&quot;param1&quot;) String qparam) {
 * ...
 * }
 * </pre>
 * 
 * Method getMethod gets query parameter "param1"(in this example abc) as String
 * qparam.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Target(value = { PARAMETER })
@Retention(RUNTIME)
public @interface QueryParam {
  String value();
}
