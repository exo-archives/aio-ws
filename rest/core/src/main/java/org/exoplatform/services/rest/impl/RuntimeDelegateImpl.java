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

package org.exoplatform.services.rest.impl;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.services.rest.impl.header.AcceptLanguage;
import org.exoplatform.services.rest.impl.header.AcceptLanguageHeaderDelegate;
import org.exoplatform.services.rest.impl.header.AcceptMediaType;
import org.exoplatform.services.rest.impl.header.AcceptMediaTypeHeaderDelegate;
import org.exoplatform.services.rest.impl.header.CacheControlHeaderDelegate;
import org.exoplatform.services.rest.impl.header.CookieHeaderDelegate;
import org.exoplatform.services.rest.impl.header.DateHeaderDelegate;
import org.exoplatform.services.rest.impl.header.EntityTagHeaderDelegate;
import org.exoplatform.services.rest.impl.header.LocaleHeaderDelegate;
import org.exoplatform.services.rest.impl.header.MediaTypeHeaderDelegate;
import org.exoplatform.services.rest.impl.header.NewCookieHeaderDelegate;
import org.exoplatform.services.rest.impl.header.StringHeaderDelegate;
import org.exoplatform.services.rest.impl.header.URIHeaderDelegate;
import org.exoplatform.services.rest.impl.uri.UriBuilderImpl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RuntimeDelegateImpl extends RuntimeDelegate {

  /**
   * Should be used only once for initialize.
   * 
   * @see RuntimeDelegate#setInstance(RuntimeDelegate)
   */
  public RuntimeDelegateImpl() {
  }

  /**
   * End Points is not supported. {@inheritDoc}
   */
  @Override
  public <T> T createEndpoint(Application applicationConfig, Class<T> type) {
    throw new UnsupportedOperationException();
  }

  /**
   * HeaderDelegate cache.
   */
  @SuppressWarnings("unchecked")
  private static final Map<Class<?>, HeaderDelegate> HDS = new HashMap<Class<?>, HeaderDelegate>();

  static {
    // add prepared HeaderDelegate according to JSR-311 and some eternal
    HDS.put(MediaType.class, new MediaTypeHeaderDelegate());
    HDS.put(CacheControl.class, new CacheControlHeaderDelegate());
    HDS.put(Cookie.class, new CookieHeaderDelegate());
    HDS.put(NewCookie.class, new NewCookieHeaderDelegate());
    HDS.put(EntityTag.class, new EntityTagHeaderDelegate());
    HDS.put(Date.class, new DateHeaderDelegate());
    // addition
    HDS.put(AcceptLanguage.class, new AcceptLanguageHeaderDelegate());
    HDS.put(AcceptMediaType.class, new AcceptMediaTypeHeaderDelegate());
    HDS.put(String.class, new StringHeaderDelegate());
    HDS.put(URI.class, new URIHeaderDelegate());
    HDS.put(Locale.class, new LocaleHeaderDelegate());
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
    // TODO mechanism for use external HeaderDelegate
    return HDS.get(type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResponseBuilder createResponseBuilder() {
    return new ResponseImpl.ResponseBuilderImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UriBuilder createUriBuilder() {
    return new UriBuilderImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VariantListBuilder createVariantListBuilder() {
    return new VariantListBuilderImpl();
  }

}
