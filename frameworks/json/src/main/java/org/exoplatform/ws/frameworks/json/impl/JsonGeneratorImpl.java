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

package org.exoplatform.ws.frameworks.json.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.ws.frameworks.json.JsonGenerator;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils.Types;
import org.exoplatform.ws.frameworks.json.value.JsonValue;
import org.exoplatform.ws.frameworks.json.value.impl.ArrayValue;
import org.exoplatform.ws.frameworks.json.value.impl.BooleanValue;
import org.exoplatform.ws.frameworks.json.value.impl.DoubleValue;
import org.exoplatform.ws.frameworks.json.value.impl.LongValue;
import org.exoplatform.ws.frameworks.json.value.impl.NullValue;
import org.exoplatform.ws.frameworks.json.value.impl.ObjectValue;
import org.exoplatform.ws.frameworks.json.value.impl.StringValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonGeneratorImpl implements JsonGenerator {

  /**
   * {@inheritDoc}
   */
  public JsonValue createJsonObject(Object object) throws JsonException {
    Method[] methods = object.getClass().getMethods();
    
    List<String> transientFields = getTransientFields(object.getClass());

    JsonValue jsonRootValue = new ObjectValue();

    for (Method method : methods) {
      String methodName = method.getName();

      /* Method must be as follow:
       * 1. Name start from "get" plus at least one character;
       * 2. Must be without parameters.
       * 3. Not "getClass" method. 
       */
      if (methodName.startsWith("get") && methodName.length() > "get".length()
          && method.getParameterTypes().length == 0 && !"getClass".equals(methodName)) {

        String key = methodName.substring("get".length());

        // first letter of key to lower case.
        key = (key.length() > 1) ? Character.toLowerCase(key.charAt(0)) + key.substring(1)
            : key.toLowerCase();
        
        // Check is this field in list of transient field.
        if (transientFields.contains(key))
          continue;

        try {
          // get result of invoke method get...
          Object invokeResult = method.invoke(object, new Object[0]);

          if (JsonUtils.getType(invokeResult) != null)
            jsonRootValue.addElement(key, createJsonValue(invokeResult));
         else
            jsonRootValue.addElement(key, createJsonObject(invokeResult));
          
        } catch (InvocationTargetException e) {
          throw new JsonException(e);
        } catch (IllegalAccessException e) {
          throw new JsonException(e);
        }
      }
    }
    return jsonRootValue;
  }
  
  /**
   * Create JsonValue corresponding to Java object.
   * @param object source object.
   * @return JsonValue.
   * @throws JsonException if any errors occurs.
   */
  protected JsonValue createJsonValue(Object object) throws JsonException {
    Types t = JsonUtils.getType(object);
    switch (t) {
      case NULL:
        return new NullValue();
      case BOOLEAN:
        return new BooleanValue((Boolean) object);
      case BYTE:
        return new LongValue((Byte) object);
      case SHORT:
        return new LongValue((Short) object);
      case INT:
        return new LongValue((Integer) object);
      case LONG:
        return new LongValue((Long) object);
      case FLOAT:
        return new DoubleValue((Float) object);
      case DOUBLE:
        return new DoubleValue((Double) object);
      case CHAR:
        return new StringValue(Character.toString((Character) object));
      case STRING:
        return new StringValue((String) object);
      case ARRAY_BOOLEAN:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new BooleanValue(Array.getBoolean(object, i)));
        return jsonArray;
      }
      case ARRAY_BYTE:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new LongValue(Array.getByte(object, i)));
        return jsonArray;
      }
      case ARRAY_SHORT:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new LongValue(Array.getShort(object, i)));
        return jsonArray;
      }
      case ARRAY_INT:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new LongValue(Array.getInt(object, i)));
        return jsonArray;
      }
      case ARRAY_LONG:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new LongValue(Array.getLong(object, i)));
        return jsonArray;
      }
      case ARRAY_FLOAT:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new DoubleValue(Array.getFloat(object, i)));
        return jsonArray;
      }
      case ARRAY_DOUBLE:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new DoubleValue(Array.getDouble(object, i)));
        return jsonArray;
      }
      case ARRAY_CHAR:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new StringValue(Character.toString(Array
              .getChar(object, i))));
        return jsonArray;
      }
      case ARRAY_STRING:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
          jsonArray.addElement(new StringValue((String) Array.get(object, i)));
        return jsonArray;
      }
      case ARRAY_OBJECT:
      {
        JsonValue jsonArray = new ArrayValue();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++) {
          Object el = Array.get(object, i);
          if (JsonUtils.getType(el) != null)
            jsonArray.addElement(createJsonValue(el));
          else
            jsonArray.addElement(createJsonObject(el));
        }

        return jsonArray;
      }
      case COLLECTION:
      {
        JsonValue jsonArray = new ArrayValue();
        List<Object> list = new ArrayList<Object>((Collection<?>) object);
        for (Object o : list) {
          if (JsonUtils.getType(o) != null)
            jsonArray.addElement(createJsonValue(o));
          else
            jsonArray.addElement(createJsonObject(o));
        }

        return jsonArray;
      }
      case MAP:
        JsonValue jsonObject = new ObjectValue();
        Map<String, Object> map = new HashMap<String, Object>(
            (Map<String, Object>) object);
        Set<String> keys = map.keySet();
        for (String k : keys) {
          Object o = map.get(k);
          if (JsonUtils.getType(o) != null)
            jsonObject.addElement(k, createJsonValue(o));
          else
            jsonObject.addElement(k, createJsonObject(o));
        }

        return jsonObject;
      default:
        // Must not be here!
        return null;
    }
    
  }
  
  /**
   * Check fields in class which marked as 'transient'. Transient fields will
   * be not serialized in JSON representation. 
   * @param clazz the class.
   * @return list of fields which must be skiped.
   */
  private static List<String> getTransientFields(Class<?> clazz) {
    List<String> l = new ArrayList<String>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field f : fields) {
      if (Modifier.isTransient(f.getModifiers())) {
        l.add(f.getName());
      }
    }
    return l;
  }
  
}
