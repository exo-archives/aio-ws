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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.services.rest.frameworks.json.JSONObjectFactory;
import org.exoplatform.services.rest.frameworks.json.utils.JSONUtils;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONObjectFactoryImpl implements JSONObjectFactory{

  private final static String GET_METHOD_PREFIX = "get";
  private final static int GET_METHOD_PREFIX_LENGTH = GET_METHOD_PREFIX.length();
  private final static String GET_CLASS_METHOD = "getClass";

  protected JSONObjectFactoryImpl() throws JSONException {
  }

  public Map<String, Object> createJSONObject(Object bean) throws JSONException {
    Method[] methods = bean.getClass().getMethods();
    Map<String, Object> objectMap = new HashMap<String, Object>();
    for (Method method : methods) {
      String methodName = method.getName();
      if (methodName.startsWith(GET_METHOD_PREFIX) 
          && methodName.length() > GET_METHOD_PREFIX_LENGTH
          && !GET_CLASS_METHOD.equals(methodName)) {

        String key = methodName.substring(GET_METHOD_PREFIX_LENGTH);
        // first letter of key to lower case.
        key = (key.length() > 1) ? Character.toLowerCase(key.charAt(0))
            + key.substring(1) : key.toLowerCase(); 
        try {
          // get result of invoke method get...  
          Object object = method.invoke(bean, new Object[0]);
          if (object == null) {
            objectMap.put(key, new NullObject());
          } else if (JSONUtils.isObjectRepresentPrimitiveType(object)) {
            // if object is primitive then just put it in the map.
            objectMap.put(key, object);
          } else if (object.getClass().isArray()){
            // if object is array then create a JSONList.
            List<Object> list = new ArrayList<Object>();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
              list.add(Array.get(object, i));
            objectMap.put(key, new JSONArrayFactoryImpl().createJSONArray(list));
          } else if (object instanceof Collection) {
            // if object is collection then create a JSONList
            List<Object> list = new ArrayList<Object>((Collection)object);
            objectMap.put(key, new JSONArrayFactoryImpl().createJSONArray(list));
          } else {
            // if object is not Collection or primitive type the do 
            // recursive calling createJSONObject for this object.
            objectMap.put(key, createJSONObject(object));
          }
        } catch (InvocationTargetException e) {
          throw new JSONException(e);
        } catch (IllegalAccessException e) {
          throw new JSONException(e);
        }
      }
    }
    return objectMap;
  }

}
