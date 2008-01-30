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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonUtils {

  public enum Types {
    BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR, STRING, NULL,

    ARRAY_BYTE, ARRAY_SHORT, ARRAY_INT, ARRAY_LONG, ARRAY_FLOAT, ARRAY_DOUBLE,
    ARRAY_BOOLEAN, ARRAY_CHAR, ARRAY_STRING, ARRAY_OBJECT, COLLECTION, MAP
  }

  public enum JsonToken {
    object, array, key, value
  }

  private static final Map<String, Types> knownTypes = new HashMap<String, Types>();

  static {
    // wrappers for primitive types
    knownTypes.put(Boolean.class.getName(), Types.BOOLEAN);

    knownTypes.put(Byte.class.getName(), Types.BYTE);
    knownTypes.put(Short.class.getName(), Types.SHORT);
    knownTypes.put(Integer.class.getName(), Types.INT);
    knownTypes.put(Long.class.getName(), Types.LONG);
    knownTypes.put(Float.class.getName(), Types.FLOAT);
    knownTypes.put(Double.class.getName(), Types.DOUBLE);

    knownTypes.put(Character.class.getName(), Types.CHAR);
    knownTypes.put(String.class.getName(), Types.STRING);

    // primitive types
    knownTypes.put("boolean", Types.BOOLEAN);

    knownTypes.put("byte", Types.BYTE);
    knownTypes.put("short", Types.SHORT);
    knownTypes.put("int", Types.INT);
    knownTypes.put("long", Types.LONG);
    knownTypes.put("float", Types.FLOAT);
    knownTypes.put("double", Types.DOUBLE);

    knownTypes.put("char", Types.CHAR);

    knownTypes.put("null", Types.NULL);

    // arrays
    knownTypes.put(new boolean[0].getClass().getName(), Types.ARRAY_BOOLEAN);

    knownTypes.put(new byte[0].getClass().getName(), Types.ARRAY_BYTE);
    knownTypes.put(new short[0].getClass().getName(), Types.ARRAY_SHORT);
    knownTypes.put(new int[0].getClass().getName(), Types.ARRAY_INT);
    knownTypes.put(new long[0].getClass().getName(), Types.ARRAY_LONG);
    knownTypes.put(new double[0].getClass().getName(), Types.ARRAY_DOUBLE);
    knownTypes.put(new float[0].getClass().getName(), Types.ARRAY_FLOAT);

    knownTypes.put(new char[0].getClass().getName(), Types.ARRAY_CHAR);
    knownTypes.put(new String[0].getClass().getName(), Types.ARRAY_STRING);

  }

  public static String getJsonString(String string) {
    if (string == null || string.length() == 0)
      return "\"\"";
    StringBuffer sb = new StringBuffer();
    sb.append("\"");
    char[] charArray = string.toCharArray();
    for (char c : charArray) {
      switch (c) {
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '"':
          sb.append("\\\"");
          break;
        default:
          if (c < '\u0010')
            sb.append("\\u000" + Integer.toHexString(c));
          else if ((c < '\u0020' && c > '\u0009') ||
              (c >= '\u0080' && c < '\u00a0'))
            sb.append("\\u00" + Integer.toHexString(c));
          else if (c >= '\u2000' && c < '\u2100')
            sb.append("\\u" + Integer.toHexString(c));
          else
            sb.append(c);
          break;
      }
    }
    sb.append("\"");
    return sb.toString();
  }

  public static boolean isKnownType(Object o) {
    if (o == null)
      return true;
    return isKnownType(o.getClass());
  }

  public static boolean isKnownType(Class<?> clazz) {
    if (knownTypes.get(clazz.getName()) != null)
      return true;
    return false;
  }

  public static Types getType(Object o) {
    if (o == null)
      return Types.NULL;
    if (knownTypes.get(o.getClass().getName()) != null)
      return knownTypes.get(o.getClass().getName());
    if (o instanceof Object[])
      return Types.ARRAY_OBJECT;
    if (o instanceof Collection)
      return Types.COLLECTION;
    if (o instanceof Map)
      return Types.MAP;
    return null;
  }

  public static Types getType(Class<?> clazz) {
    if (knownTypes.get(clazz.getName()) != null)
      return knownTypes.get(clazz.getName());
    if (clazz.isArray())
      return Types.ARRAY_OBJECT;
    if (Collection.class.isAssignableFrom(clazz))
      return Types.COLLECTION;
    if (Map.class.isAssignableFrom(clazz))
      return Types.MAP;
    return null;
  }
}
