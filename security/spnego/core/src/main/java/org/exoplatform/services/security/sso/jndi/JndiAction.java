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

/**
 * 
 */
package org.exoplatform.services.security.sso.jndi;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.sso.config.Config;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JndiAction {

  private final static Log log = ExoLogger.getLogger("ws.security.JndiAction");

  private JndiAction() {
  }

  public static List<String> getGroups(String user) throws LoginException {
    Config config = Config.getInstance();
    /* Login context for JNDI action.
     */
    LoginContext lc = new LoginContext(config.getJaasContext());
    lc.login();
    if (log.isDebugEnabled()) {
      log.debug("Login for privileged user success, try go get LDAP.");
    }
    Subject subject = lc.getSubject();
    Object o = Subject.doAs(subject, new JndiAction().new Action(user));
    if (o != null) {
      List<String> groups = (List<String>) o;
      if (log.isDebugEnabled()) {
        log.debug("Groups for user \"" + user + "\": " + groups);
      }
      return groups;
    }
    return null;
  }

  /**
   * Transform domain name to LDAP address.
   * For example: EXODOMAIN.ORG -> DC=EXODOMAIN,DC=ORG
   * @param domain the domain name.
   * @return the LDAP address equals to given domain name.
   */
  private static String domainName2LdapAddress(String domain) {
    StringBuffer sb = new StringBuffer();
    int lenght = domain.length();
    for (int i = 0, pos = 0; i < lenght; i = pos + 1) {
      pos = domain.indexOf('.', i);
      if (pos == -1)
        pos = lenght;
      sb.append("DC=");
      sb.append(domain.substring(i, pos));
      if (pos < lenght)
        sb.append(',');
    }
    return sb.toString();
  }

  private class Action implements PrivilegedAction<List<String>> {

    private final String user;

    Action(String user) {
      this.user = user;
    }

    /*
     * (non-Javadoc)
     * @see java.security.PrivilegedAction#run()
     */
    public List<String> run() {
      try {
        Config config = Config.getInstance();
        
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, config.getLdapServer());
        env.put(Context.SECURITY_AUTHENTICATION, "GSSAPI");
        LdapContext ldapCtx = new InitialLdapContext(env, null);
        
        SearchControls searchCtrls = new SearchControls();
        searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(&(objectClass=user)(CN=" + user + "))";
        String searchBase = "CN=Users," + JndiAction.domainName2LdapAddress(config.getDomain());
        String[] returnAttributes = { "memberOf" };
        searchCtrls.setReturningAttributes(returnAttributes);

        List<String> list = new ArrayList<String>();
        
        NamingEnumeration<SearchResult> answer = ldapCtx.search(searchBase,
            searchFilter, searchCtrls);
        
        while (answer.hasMoreElements()) {
          SearchResult sr = answer.next();
          if (log.isDebugEnabled()) {
            log.debug("Search result: " + sr.getName());
          }
          Attributes attrs = sr.getAttributes();
          if (attrs != null) {
            for (NamingEnumeration<? extends Attribute> ne = attrs.getAll(); ne.hasMore();) {
              Attribute attr = ne.next();
              if (log.isDebugEnabled()) {
                log.debug("Attribute: getID() " + attr.getID());
                log.debug("Attribute: size()  " + attr.size());
              }
              int results = attr.size();
              for (int i = 0; i < results; i++) {
                String s = (String) attr.get(i);
                int comma = s.indexOf(',');
                list.add(s.substring(3, comma));
              }
            }
          }
        }
        return list;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

}
