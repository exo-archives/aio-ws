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
package org.exoplatform.services.rest.data;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MimeTypes {

  public static final String ALL = "*/*";
  private String[] mimeTypes;

  /**
   * @param s the Content-Type HTTP header
   */
  public MimeTypes(String s) {
    mimeTypes = HeaderUtils.parse(s);
  }

  /**
   * @return sorted array of mimetype.
   * @see org.exoplatform.services.rest.data.HeaderUtils
   */
  public String[] getMimeTypes() {
    return mimeTypes;
  }

  /**
   * Get mimetype by index.
   * @param i index of mimetype in array
   * @return mimetype
   */
  public String getMimeType(int i) {
    return mimeTypes[i];
  }

  /**
   * Check does array has requested mimetype.
   * @param s requested mimetype
   * @return result
   */
  public boolean hasMimeType(String s) {
    for (String m : mimeTypes) {
      if (m.equalsIgnoreCase(s)) {
        return true;
      }
    }
    return false;
  }

}
