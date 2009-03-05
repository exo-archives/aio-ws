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
public class SerialResponse implements Serializable {

  private static final long serialVersionUID = 7250729921392627533L;
  
  private int status;
  private HashMap<String, String> headers;
  private SerialInputData data;
  
  public int getStatus() {
    return status;
  }
  
  public void setStatus(int status) {
    this.status = status;
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
