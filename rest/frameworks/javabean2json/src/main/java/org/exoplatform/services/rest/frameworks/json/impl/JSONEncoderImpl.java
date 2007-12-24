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

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.services.rest.frameworks.json.JSONEncoder;
import org.exoplatform.services.rest.frameworks.json.JSONWriter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONEncoderImpl implements JSONEncoder {

  private JSONWriter jsonWriter_;
  
  public JSONEncoderImpl(Writer writer) {
    jsonWriter_ = new JSONWriterImpl(writer);
  }
  
  public JSONEncoderImpl(OutputStream out) {
    jsonWriter_ = new JSONWriterImpl(out);
  }

  public void writeObject(Object bean) throws IllegalArgumentException {
    try {
      Map<String, Object> jsonMap = new JSONObjectFactoryImpl().createJSONObject(bean);
      writeJSONObject(jsonMap);
      jsonWriter_.flush();
      jsonWriter_.close();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Object '"
          + bean + "' can't be encoded in JSON representation.", e);
    }

  }

  private void writeJSONObject(Map<String, Object> jsonObject) throws JSONException {
    jsonWriter_.writeStartObject();
    Set<String> keys = jsonObject.keySet();
    for (String key : keys) {
      jsonWriter_.writeKey(key);
      Object o = jsonObject.get(key);
      if (o instanceof Collection) {
        List<Object> jsonArray = new ArrayList<Object>((Collection)o); 
        writeJSONArray(jsonArray);
      } else if (o instanceof Map) {
        Map<String, Object> m = new HashMap<String, Object>((Map)o);
        writeJSONObject(m);
      } else {
        writeSimpleObject(o);
      }
    }    
    jsonWriter_.writeEndObject();
  }
  
  private void writeJSONArray(List<Object> array) throws JSONException {
    jsonWriter_.writeStartArray();
    for (Object o : array) {
      if (o instanceof Collection) {
        List<Object> jsonArray = new ArrayList<Object>((Collection)o); 
        writeJSONArray(jsonArray);
      } else if (o instanceof Map) {
        Map<String, Object> m = new HashMap<String, Object>((Map)o);
        writeJSONObject(m);
      } else {
        writeSimpleObject(o);
      }
    }
    jsonWriter_.writeEndArray();
  }
  
  private void writeSimpleObject(Object o) throws JSONException {
    if (o instanceof NullObject)
      jsonWriter_.writeNull();
    else if (o instanceof Boolean)
      jsonWriter_.writeValue((Boolean)o);
    else if (o instanceof Character)
      jsonWriter_.writeString((Character.toString((Character)o)));
    else if (o instanceof Byte)
      jsonWriter_.writeValue((Byte)o);
    else if (o instanceof Short)
      jsonWriter_.writeValue((Short)o);
    else if (o  instanceof Integer)
      jsonWriter_.writeValue((Integer)o);
    else if (o instanceof Long)
      jsonWriter_.writeValue((Long)o);
    else if (o instanceof Float)
      jsonWriter_.writeValue((Float)o);
    else if (o instanceof Double)
      jsonWriter_.writeValue((Double)o);
    else if (o instanceof String)
      jsonWriter_.writeString((String)o);
  }

}

