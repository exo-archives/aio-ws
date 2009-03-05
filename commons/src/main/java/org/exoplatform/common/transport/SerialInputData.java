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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SerialInputData implements Serializable {

  private static final long serialVersionUID = -8333597717110874434L;
  
  private static final int MAX_BUFFER_SIZE = 204800;
  
  private InputStream stream;
  
  public SerialInputData(InputStream stream) {
    if (stream == null) 
      throw new IllegalArgumentException("Stream can't be null!");
      
    this.stream = stream;
  }
  
  public SerialInputData(byte[] bytes) {
    this(new ByteArrayInputStream(bytes));
  }
  
  public InputStream getStream() {
    return stream;
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    int bytes = 0;
    while ((bytes = stream.read(buffer)) >= 0) {
      if (bytes > 0) {
        out.writeInt(bytes);
        out.write(buffer, 0, bytes);
      }
    }
    out.writeInt(0);
    stream.close();
  }

  
  private void readObject(ObjectInputStream in) throws IOException {
    
    boolean overflow = false; 
    byte[] buffer = new byte[8192];

    ByteArrayOutputStream bout = new ByteArrayOutputStream(MAX_BUFFER_SIZE);
    
    for (int bytes = in.readInt(); bytes > 0; bytes = in.readInt()) {
      in.readFully(buffer, 0, bytes);
      bout.write(buffer, 0, bytes);
      if (bout.size() > MAX_BUFFER_SIZE) {
        overflow = true;
        break;
      }
    }
    
    if (!overflow) {
      // small data , use bytes
      stream = new ByteArrayInputStream(bout.toByteArray());
      return;
    }
    
    // large data, use file
    final File file = File.createTempFile("restejb-", null);
    OutputStream out = new FileOutputStream(file);
    
    // copy data from byte array in file
    bout.writeTo(out);

    for (int bytes = in.readInt(); bytes > 0; bytes = in.readInt()) {
      in.readFully(buffer, 0, bytes);
      out.write(buffer, 0, bytes);
    }
    
    out.close();

    stream = new FileInputStream(file) {

      private boolean removed = false;

      /* (non-Javadoc)
       * @see java.io.FileInputStream#close()
       */
      @Override
      public void close() throws IOException {
        try {
          super.close();
        } finally {
          // file must be removed after using 
          removed = file.delete();
        }
      }

      /* (non-Javadoc)
       * @see java.io.FileInputStream#finalize()
       */
      @Override
      protected void finalize() throws IOException {
        try {
          // if file was not removed
          if (!removed)
            file.delete();
          
        } finally {
          super.finalize();
        }
      }
    };
  }
  
}
