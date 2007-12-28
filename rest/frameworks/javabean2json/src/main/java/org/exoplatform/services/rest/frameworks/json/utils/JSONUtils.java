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

package org.exoplatform.services.rest.frameworks.json.utils;

import org.exoplatform.services.rest.frameworks.json.JSONConstants;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONUtils {
  
  public static String getJSONString(String string) {
    if (string == null || string.length() == 0)
      return "\"\"";
    StringBuffer sb = new StringBuffer();
    sb.append("\"");
    char[] charArray = string.toCharArray();
    for (char c : charArray) {
      switch (c) {
      case JSONConstants.NEW_LINE:
        sb.append("\\n");
        break;
      case JSONConstants.CARRIAGE_RETURN:
        sb.append("\\r");
        break;
      case JSONConstants.HORIZONTAL_TAB:
        sb.append("\\t");
        break;
      case JSONConstants.BACKSPACE:
        sb.append("\\b");
        break;
      case JSONConstants.FORM_FEED:
        sb.append("\\f");
        break;
      case JSONConstants.ESCAPE:
        sb.append("\\\\");
        break;
      case JSONConstants.QUOTE:
        sb.append("\\\"");
        break;
      default:
        if (c < '\u0010')
          sb.append("\\u000" + Integer.toHexString(c));
        else if ((c < '\u0020' && c > '\u0009') || (c >= '\u0080' && c < '\u00a0'))
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
  
  public static boolean isObjectRepresentPrimitiveType(Object o) {
    if (o instanceof Boolean)
      return true;
    if (o instanceof Character)
      return true;
    if (o instanceof Byte)
      return true;
    if (o instanceof Short)
      return true;
    if (o instanceof Integer)
      return true;
    if (o instanceof Long)
      return true;
    if (o instanceof Float)
      return true;
    if (o instanceof Double)
      return true;
    if (o instanceof String)
      return true;
    return false;
  }  

}

