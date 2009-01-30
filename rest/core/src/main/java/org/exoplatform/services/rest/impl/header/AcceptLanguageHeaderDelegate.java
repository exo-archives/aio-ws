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

import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;
import org.exoplatform.services.rest.header.QualityValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AcceptLanguageHeaderDelegate extends AbstractHeaderDelegate<AcceptLanguage> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<AcceptLanguage> support() {
    return AcceptLanguage.class;
  }

  /**
   * {@inheritDoc}
   */
  public AcceptLanguage fromString(String header) {
    if (header == null)
      throw new IllegalArgumentException();
    
    try {
      header = HeaderHelper.removeWhitespaces(header);
      String tag;
      Map<String, String> m = null;

      int p = header.indexOf(';');
      if (p != -1 && p < header.length() - 1) { // header has quality value
        tag = header.substring(0, p);
        m = new HeaderParameterParser().parse(header);
      } else { // no quality value
        tag = header;
      }

      p = tag.indexOf('-');
      String primaryTag;
      String subTag = null;

      if (p != -1 && p < tag.length() - 1) { // has sub-tag
        primaryTag = tag.substring(0, p);
        subTag = tag.substring(p + 1);
      } else { // no sub-tag
        primaryTag = tag;
      }

      if (m == null) // no quality value
        return new AcceptLanguage(new Locale(primaryTag, subTag != null ? subTag : ""));
      else
        return new AcceptLanguage(new Locale(primaryTag, subTag != null ? subTag : ""),
                                    HeaderHelper.parseQualityValue(m.get(QualityValue.QVALUE)));

    } catch (ParseException e) {
      throw new IllegalArgumentException("Accept language header malformed");
    }
  }

  /**
   * {@inheritDoc}
   */
  public String toString(AcceptLanguage language) {
    throw new UnsupportedOperationException("Accepted language header used only for request.");
  }

}
