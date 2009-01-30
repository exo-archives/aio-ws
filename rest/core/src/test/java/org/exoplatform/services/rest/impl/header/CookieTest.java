/**
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

package org.exoplatform.services.rest.impl.header;

import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.header.HeaderHelper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CookieTest extends BaseTest {

  public void testToString() {
    Cookie cookie = new Cookie("name", "andrew");
    assertEquals("$Version=1;name=andrew", cookie.toString());

    cookie = new Cookie("name", "andrew", "/exo", "exo.com");
    assertEquals("$Version=1;name=andrew;$Domain=exo.com;$Path=/exo", cookie.toString());
  }

  public void testValueOf() {
    String cookieHeader = "$Version=1;company=exo;$Path=/exo,$Domain=exo.com;";
    Cookie c = Cookie.valueOf(cookieHeader);
    assertEquals(c.getVersion(), 1);
    assertEquals(c.getName(), "company");
    assertEquals(c.getValue(), "exo");
    assertEquals(c.getPath(), "/exo");
    assertEquals(c.getDomain(), "exo.com");
  }

  public void testFromString() {
    String cookieHeader = "name=andrew";
    List<Cookie> cookies = HeaderHelper.parseCookies(cookieHeader);
    assertEquals(cookies.size(), 1);
    Cookie c = cookies.get(0);
    assertEquals(c.getVersion(), 0);
    assertEquals(c.getName(), "name");
    assertEquals(c.getValue(), "andrew");

    cookieHeader = "company=exo,name=andrew";
    cookies = HeaderHelper.parseCookies(cookieHeader);
    assertEquals(cookies.size(), 2);
    c = cookies.get(0);
    assertEquals(c.getVersion(), 0);
    assertEquals(c.getName(), "company");
    assertEquals(c.getValue(), "exo");
    c = cookies.get(1);
    assertEquals(c.getVersion(), 0);
    assertEquals(c.getName(), "name");
    assertEquals(c.getValue(), "andrew");

    cookieHeader = "company=exo;name=andrew";
    cookies = HeaderHelper.parseCookies(cookieHeader);
    assertEquals(cookies.size(), 2);
    c = cookies.get(0);
    assertEquals(c.getVersion(), 0);
    assertEquals(c.getName(), "company");
    assertEquals(c.getValue(), "exo");
    c = cookies.get(1);
    assertEquals(c.getVersion(), 0);
    assertEquals(c.getName(), "name");
    assertEquals(c.getValue(), "andrew");

    cookieHeader = "$Version=1;company=exo;$Path=/exo,$Domain=exo.com;name=andrew";
    cookies = HeaderHelper.parseCookies(cookieHeader);
    assertEquals(cookies.size(), 2);
    c = cookies.get(0);
    assertEquals(c.getVersion(), 1);
    assertEquals(c.getName(), "company");
    assertEquals(c.getValue(), "exo");
    assertEquals(c.getPath(), "/exo");
    assertEquals(c.getDomain(), "exo.com");
    c = cookies.get(1);
    assertEquals(c.getVersion(), 1);
    assertEquals(c.getName(), "name");
    assertEquals(c.getValue(), "andrew");

    cookieHeader = "$Version=1;  company=exo;  $Path=/exo, $Domain=exo.com;name=andrew,  $Domain=exo.org";
    cookies = HeaderHelper.parseCookies(cookieHeader);
    assertEquals(cookies.size(), 2);
    c = cookies.get(0);
    assertEquals(c.getVersion(), 1);
    assertEquals(c.getName(), "company");
    assertEquals(c.getValue(), "exo");
    assertEquals(c.getPath(), "/exo");
    assertEquals(c.getDomain(), "exo.com");
    c = cookies.get(1);
    assertEquals(c.getVersion(), 1);
    assertEquals(c.getName(), "name");
    assertEquals(c.getValue(), "andrew");
    assertEquals(c.getDomain(), "exo.org");
  }

  public void testToString2() {
    // NewCookie
    NewCookie cookie = new NewCookie("name", "andrew");
    assertEquals("name=andrew;Version=1", cookie.toString());

    cookie = new NewCookie("name", "andrew", "/exo", "exo.com", 0, "sample comment", 1200, true);
    assertEquals("name=andrew;Version=0;Comment=\"sample comment\";Domain=exo.com;Path=/exo;Max-Age=1200;Secure",
                 cookie.toString());
  }

}
