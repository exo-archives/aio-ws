/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.common.util;

import java.util.Comparator;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jun 25, 2010  
 */
public class MediaTypeHelper {
  
  /**
   * Compare two mimetypes. The main rule for sorting media types is :
   * <p>
   * <li>type / subtype</li>
   * <li>type / *</li>
   * <li>* / *</li>
   * <p>
   */
  public static final Comparator<MediaType> MEDIA_TYPE_COMPARATOR = new Comparator<MediaType>()
  {

    public int compare(MediaType o1, MediaType o2)
      {
        if (o1.getType().equals(MediaType.MEDIA_TYPE_WILDCARD) && !o2.getType().equals(MediaType.MEDIA_TYPE_WILDCARD))
        {
          return 1;
        }

        if (!o1.getType().equals(MediaType.MEDIA_TYPE_WILDCARD) && o2.getType().equals(MediaType.MEDIA_TYPE_WILDCARD))
        {
          return -1;
        }

        if (o1.getSubtype().equals(MediaType.MEDIA_TYPE_WILDCARD)
           && !o2.getSubtype().equals(MediaType.MEDIA_TYPE_WILDCARD))
        {
          return 1;
        }

        if (!o1.getSubtype().equals(MediaType.MEDIA_TYPE_WILDCARD)
           && o2.getSubtype().equals(MediaType.MEDIA_TYPE_WILDCARD))
        {
          return -1;
        }
        return 0;
     }

  };

}
