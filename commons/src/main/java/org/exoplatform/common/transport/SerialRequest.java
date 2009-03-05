/**
 * Copyright (C) 2003-2008 eXo Platform SAS.
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

package org.exoplatform.common.transport;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SerialRequest implements Serializable {

  private static final long serialVersionUID = -1285660043757622945L;

  private String method;
  private String url;
  private HashMap<String, String> queries;
  private HashMap<String, String> headers;
  private SerialInputData data;
  
  public String getMethod() {
    return method;
  }
  
  public void setMethod(String method) {
    this.method = method;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public HashMap<String, String> getQueries() {
    return queries;
  }
  
  public void setQueries(HashMap<String, String> queries) {
    this.queries = queries;
  }
  
  public HashMap<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }

  public SerialInputData getData() {
    return data;
  }
  
  public void setData(SerialInputData data) {
    this.data = data;
  }
  
}
