/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.services.rest.ext.filter;

import java.net.URI;

import javax.ws.rs.ext.RuntimeDelegate;

import junit.framework.TestCase;

import org.exoplatform.services.rest.impl.ContainerRequest;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;

/**
 * Created by The eXo Platform SAS Author : Dmytro Katayev
 * work.visor.ck@gmail.com 25 Sep 2008
 */
public class UriNormalizationFilterTest extends TestCase {

  private String[] testUris       = { 
      "http://localhost:8080/servlet/../1//2/3/./../../4",
      "http://localhost:8080/servlet/./1//2/3/./../../4",
      "http://localhost:8080/servlet/1//2/3/./../../4",
      "http://localhost:8080/servlet/1//2./3/./../4",
      "http://localhost:8080/servlEt/1//.2/3/./../4",
      "http://localhost:8080/servlet/1..//.2/3/./../4",
      "http://localhost:8080/servlet/./1//2/3/./../../4", 
      "http://localhost:8080/servlet/.",
      "http://localhost:8080/servlet/..",
      "http://localhost:8080/servlet/1"};

  private String[] normalizedUris = { 
      "http://localhost:8080/1/4",
      "http://localhost:8080/servlet/1/4",
      "http://localhost:8080/servlet/1/4",
      "http://localhost:8080/servlet/1/2./4",
      "http://localhost:8080/servlet/1/.2/4",
      "http://localhost:8080/servlet/1../.2/4",
      "http://localhost:8080/servlet/1/4",
      "http://localhost:8080/servlet/",
      "http://localhost:8080/",
      "http://localhost:8080/servlet/1"};

  public void setUp() {
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
  }

  public void testURIFilter() throws Exception {

    URI baseUri = new URI("http://localhost:8080/servlet/");
    UriNormalizationFilter filter = new UriNormalizationFilter();

    for (int i = 0; i < testUris.length; i++) {
      URI requestUri = new URI(testUris[i]);
      ContainerRequest request = new ContainerRequest("", requestUri, baseUri, null, null);
      filter.doFilter(request);
      assertEquals(normalizedUris[i], request.getRequestUri().toString());
    }
  }
}
