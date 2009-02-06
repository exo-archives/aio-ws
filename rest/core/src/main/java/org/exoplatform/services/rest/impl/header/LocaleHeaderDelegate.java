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

import java.util.Locale;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocaleHeaderDelegate extends AbstractHeaderDelegate<Locale> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<Locale> support() {
    return Locale.class;
  }

  /**
   * {@inheritDoc}
   */
  public Locale fromString(String header) {
    if (header == null)
      throw new IllegalArgumentException();
    
    header = HeaderHelper.removeWhitespaces(header);
    int p;
    // Can be set multiple content language, the take first one
    if ((p = header.indexOf(',')) > 0)
      header = header.substring(0, p);
    
    p = header.indexOf('-');
    if (p != -1 && p < header.length() - 1)
      return new Locale(header.substring(0, p), header.substring(p + 1));
    else
      return new Locale(header);
  }

  /**
   * {@inheritDoc}
   */
  public String toString(Locale locale) {
    String lan = locale.getLanguage();
    // For output if language does not set correctly then ignore it.
    if ("".equals(lan) || "*".equals(lan))
      return null;

    String con = locale.getCountry();
    if ("".equals(lan))
      return lan.toLowerCase();

    return lan.toLowerCase() + "-" + con.toLowerCase();
  }

}
