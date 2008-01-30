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

package org.exoplatform.ws.framework.json.impl;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.ws.framework.json.JsonGenerator;
import org.exoplatform.ws.framework.json.impl.JsonUtils.Types;
import org.exoplatform.ws.framework.json.value.JsonValue;
import org.exoplatform.ws.framework.json.value.impl.ArrayValue;
import org.exoplatform.ws.framework.json.value.impl.BooleanValue;
import org.exoplatform.ws.framework.json.value.impl.DoubleValue;
import org.exoplatform.ws.framework.json.value.impl.LongValue;
import org.exoplatform.ws.framework.json.value.impl.NullValue;
import org.exoplatform.ws.framework.json.value.impl.ObjectValue;
import org.exoplatform.ws.framework.json.value.impl.StringValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonGeneratorImpl implements JsonGenerator {

  public JsonGeneratorImpl() throws JsonException {
  }

  public JsonValue createJsonObject(Object object) throws JsonException {
    Method[] methods = object.getClass().getMethods();

    JsonValue jsonRootValue = new ObjectValue();

    for (Method method : methods) {
      String methodName = method.getName();

      if (methodName.startsWith("get") &&
          methodName.length() > "get".length() &&
          !"getClass".equals(methodName)) {

        String key = methodName.substring("get".length());

        // first letter of key to lower case.
        key = (key.length() > 1) ? Character.toLowerCase(key.charAt(0)) + key.substring(1)
            : key.toLowerCase();

        try {
          // get result of invoke method get...
          Object invokeResult = method.invoke(object, new Object[0]);

          Types t = JsonUtils.getType(invokeResult);
          if (t != null) {
            switch (t) {
              case NULL:
                jsonRootValue.addElement(key, new NullValue());
                break;
              case BOOLEAN:
                jsonRootValue.addElement(key, new BooleanValue((Boolean) invokeResult));
                break;
              case BYTE:
                jsonRootValue.addElement(key, new LongValue((Byte) invokeResult));
                break;
              case SHORT:
                jsonRootValue.addElement(key, new LongValue((Short) invokeResult));
                break;
              case INT:
                jsonRootValue.addElement(key, new LongValue((Integer) invokeResult));
                break;
              case LONG:
                jsonRootValue.addElement(key, new LongValue((Long) invokeResult));
                break;
              case FLOAT:
                jsonRootValue.addElement(key, new DoubleValue((Float) invokeResult));
                break;
              case DOUBLE:
                jsonRootValue.addElement(key, new DoubleValue((Double) invokeResult));
                break;
              case CHAR:
                jsonRootValue.addElement(key, new StringValue(Character
                    .toString((Character) invokeResult)));
                break;
              case STRING:
                jsonRootValue.addElement(key, new StringValue((String) invokeResult));
                break;
              case ARRAY_BOOLEAN:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new BooleanValue(Array.getBoolean(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_BYTE:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new LongValue(Array.getByte(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_SHORT:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new LongValue(Array.getShort(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_INT:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new LongValue(Array.getInt(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_LONG:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new LongValue(Array.getLong(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_FLOAT:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new DoubleValue(Array.getFloat(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_DOUBLE:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new DoubleValue(Array.getDouble(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_CHAR:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new StringValue(
                      Character.toString(Array.getChar(invokeResult, i))));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_STRING:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(new StringValue((String) Array.get(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case ARRAY_OBJECT:
              {
                JsonValue jsonArray = new ArrayValue();
                int length = Array.getLength(invokeResult);
                for (int i = 0; i < length; i++)
                  jsonArray.addElement(createJsonObject(Array.get(invokeResult, i)));
                jsonRootValue.addElement(key, jsonArray);
              }
                break;
              case COLLECTION:
                JsonValue jsonArray = new ArrayValue();
                List<Object> list = new ArrayList<Object>((Collection<?>) invokeResult);
                for (Object o : list)
                  jsonArray.addElement(createJsonObject(o));

                jsonRootValue.addElement(key, jsonArray);
                break;
              default:
                break;
            }
          } else {
            jsonRootValue.addElement(key, createJsonObject(invokeResult));
          }
        } catch (InvocationTargetException e) {
          throw new JsonException(e);
        } catch (IllegalAccessException e) {
          throw new JsonException(e);
        }
      }
    }
    return jsonRootValue;
  }
}
