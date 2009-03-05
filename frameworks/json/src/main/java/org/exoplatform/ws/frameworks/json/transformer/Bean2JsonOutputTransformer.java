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

package org.exoplatform.ws.frameworks.json.transformer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.exoplatform.services.rest.transformer.OutputEntityTransformer;
import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonWriterImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * Created by The eXo Platform SARL.
 * Author : Volodymyr Krasnikov
 * volodymyr.krasnikov@exoplatform.com.ua
 */
public class Bean2JsonOutputTransformer extends OutputEntityTransformer {

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeTo(Object entity, OutputStream entityDataStream) throws IOException {
    try {
      JsonValue jv = new JsonGeneratorImpl().createJsonObject(entity);
      Writer writer = new OutputStreamWriter(entityDataStream,"UTF-8");
      JsonWriter jsonWriter = new JsonWriterImpl(writer);
      jv.writeTo(jsonWriter);
      jsonWriter.flush();
    } catch (JsonException e) {
      e.printStackTrace();
      throw new IOException("Error while converting POJO to JSON");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

  }

}
