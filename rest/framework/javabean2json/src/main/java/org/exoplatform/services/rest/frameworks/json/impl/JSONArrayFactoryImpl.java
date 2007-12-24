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

package org.exoplatform.services.rest.frameworks.json.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.rest.frameworks.json.JSONArrayFactory;
import org.exoplatform.services.rest.frameworks.json.utils.JSONUtils;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONArrayFactoryImpl implements JSONArrayFactory {

  protected JSONArrayFactoryImpl() throws JSONException {
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONArrayFactory#createJSONArray(java.util.List)
   */
  public Collection<Object> createJSONArray(List<? extends Object> source)
      throws JSONException {
    try {
      List<Object> target = new ArrayList<Object>();
      for (Object object : source) {
        if (JSONUtils.isObjectRepresentPrimitiveType(object)) {
          target.add(object);
        } else if (object.getClass().isArray()) {
          List<Object> subList = new ArrayList<Object>();
          int length = Array.getLength(object);
          for (int i = 0; i < length; i++)
            subList.add(Array.get(object, i));
          target.add(subList);
        } else if (object instanceof Collection) {
          List<Object> subList = new ArrayList<Object>((Collection)object);
          target.add(createJSONArray(subList));
        } else {
          target.add(new JSONObjectFactoryImpl().createJSONObject(object));
        }
      }
      return target;
    } catch(JSONException e) {
      throw new JSONException(e);
    }
  }

}
