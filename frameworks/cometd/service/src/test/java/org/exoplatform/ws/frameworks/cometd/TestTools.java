/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.ws.frameworks.cometd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.frameworks.cometd.transport.DlegateMessage;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.BeanBuilder;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TestTools {
  /**
   * Class logger.
   */
  private final Log log = ExoLogger.getLogger("ws.TestTools");

  public static String getBaseURLCometdServer(String u) {
    try {
      URL url = new URL(u);
      HTTPConnection connection = new HTTPConnection(url);
      HTTPResponse response = connection.Get(url.getFile());
      if (response.getStatusCode() == HTTPStatus.OK) {
       String baseCometdURL = new String(response.getData());
       return baseCometdURL;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getUserToken(String u) {
    try {
      URL url = new URL(u);// + "/rest/ext/gettoken/" + id + "/");
      HTTPConnection connection = new HTTPConnection(url);
      HTTPResponse response = connection.Get(url.getFile());
      String token = new String(response.getData());
      // System.out.println("RemoteCometdTest.getUserToken()"+ token);
      return token;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void sendMessage(String exoid,
                                 String channel,
                                 String message,
                                 String msgId,
                                 String baseURI) {
    try {
      DlegateMessage data = new DlegateMessage(channel, exoid, message, msgId);
      URL url = new URL(baseURI);// + "ext/sendprivatemessage/");
      JsonGeneratorImpl generatorImpl = new JsonGeneratorImpl();
      JsonValue json = generatorImpl.createJsonObject(data);
      HTTPConnection connection = new HTTPConnection(url);
      connection.Post(url.getFile(), json.toString().getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void sendBroadcastMessage(String channel,
                                          String message,
                                          String msgId,
                                          String baseURI) {
    try {
      DlegateMessage data = new DlegateMessage(channel, message, msgId);
      JsonGeneratorImpl generatorImpl = new JsonGeneratorImpl();
      JsonValue json = generatorImpl.createJsonObject(data);
      URL url = new URL(baseURI);// + "ext/sendbroadcastmessage/");
      HTTPConnection connection = new HTTPConnection(url);
      connection.Post(url.getFile(), json.toString().getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static CMessage stringToCMessage(String incomString) {
    try {
      // System.out.println("RemoteCometdTest.stringToCMessage()" +
      // incomString);
      String tmpJ = incomString.trim();
      String jsonString = tmpJ.substring(1, tmpJ.length() - 1);
      JsonHandler jsonHandler = new JsonDefaultHandler();
      JsonParser jsonParser = new JsonParserImpl();
      InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes());
      jsonParser.parse(inputStream, jsonHandler);
      JsonValue jsonValue = jsonHandler.getJsonObject();
      // System.out.println("RemoteeCometdTest.stringToCMessage()" +
      // jsonValue.toString());
      return (CMessage) new BeanBuilder().createObject(CMessage.class, jsonValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static CMessages stringToCMessages(String incomString) {
    try {
      String tmpJ = incomString.trim();
      String jsonString = "{\"cometdMessages\":" + tmpJ + "}";//.substring(1,tmpJ
                                                              // .length()-1);
      JsonHandler jsonHandler = new JsonDefaultHandler();
      JsonParser jsonParser = new JsonParserImpl();
      InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes());
      jsonParser.parse(inputStream, jsonHandler);
      JsonValue jsonValue = jsonHandler.getJsonObject();
      return (CMessages) new BeanBuilder().createObject(CMessages.class, jsonValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static Boolean release(String u){
    try {
      URL url = new URL(u);
      HTTPConnection connection = new HTTPConnection(url);
      HTTPResponse response = connection.Get(url.getFile());
      if (response.getStatusCode() == HTTPStatus.OK) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}
