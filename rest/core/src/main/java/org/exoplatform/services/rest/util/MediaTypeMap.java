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

package org.exoplatform.services.rest.util;

import java.util.Comparator;

import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.impl.header.MediaTypeHelper;

/**
 * Keeps sorted values.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MediaTypeMap<T> extends java.util.TreeMap<MediaType, T> {
  
  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = -4713556573521776577L;

  /**
   * Create new instance of MedaTypeMap with {@link Comparator}.
   */
  public MediaTypeMap() {
    super(COMPARATOR);
  }

  /**
   * See {@link Comparator}.
   */
  protected static final Comparator<MediaType> COMPARATOR = new Comparator<MediaType>() {

    /**
     * Compare two {@link MediaType}.
     * @param o1 first MediaType to be compared
     * @param o2 second MediaType to be compared
     * @return result of comparison
     * @see Comparator#compare(Object, Object)
     * @see MediaTypeHelper
     * @see MediaType
     */
    public int compare(MediaType o1, MediaType o2) {
      int r = MediaTypeHelper.MEDIA_TYPE_COMPARATOR.compare(o1, o2);
      // If media type has the same 'weight' (i.e. 'application/xml' and
      // 'text/xml' has the same 'weight'), then order does not matter but
      // should e compared lexicographically, otherwise new entry with the
      // same 'weight' will be not added in map.
      if (r == 0)
        // TODO weak solution
        r = toString0(o1).compareToIgnoreCase(toString0(o2));
      return r;
    }
    
    private String toString0(MediaType mime) {
      return mime.getType() + "/" + mime.getSubtype();
    }
    
  };
  
}
