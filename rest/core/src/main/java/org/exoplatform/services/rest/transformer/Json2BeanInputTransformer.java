/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.rest.transformer;

import java.io.IOException;
import java.io.InputStream;

import org.exoplatform.ws.framework.json.JsonParser;
import org.exoplatform.ws.framework.json.impl.BeanBuilder;
import org.exoplatform.ws.framework.json.impl.JsonException;
import org.exoplatform.ws.framework.json.impl.JsonParserImpl;
import org.exoplatform.ws.framework.json.value.JsonValue;

/**
 * Created by The eXo Platform SARL
 * Author : Volodymyr Krasnikov
 *          volodymyr.krasnikov@exoplatform.com.ua
 */

public class Json2BeanInputTransformer extends InputEntityTransformer {

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.InputEntityTransformer#readFrom(java.io.InputStream)
   */
  @Override
  public Object readFrom(InputStream entityDataStream) throws IOException {

    Object targetObject = null;

    JsonParser jsonParser = new JsonParserImpl();
    try {
      jsonParser.parse(entityDataStream);
      JsonValue jsonValue = jsonParser.getJsonHandler().getJsonObject();
      targetObject = new BeanBuilder().createObject(this.getType(),jsonValue);
    } catch (JsonException e) {
      e.printStackTrace();
      throw new IOException("JSON parsing exception");
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("JSON to JavaBean conversion exception");
    }

    return targetObject;

  }

}
