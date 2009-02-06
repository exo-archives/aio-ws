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

package org.exoplatform.services.rest.impl.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * Processing multipart data based on apache fileupload.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Consumes( { "multipart/*" })
public class MultipartFormDataEntityProvider implements EntityProvider<Iterator<FileItem>> {

  /**
   * Logger.
   */
  private static final Log    LOG            = ExoLogger.getLogger(MultipartFormDataEntityProvider.class.getName());

  /**
   * Default folder for temporary files.
   */
  private static final String DEFAULT_FOLDER = "ws.rs.upload";

  /**
   * Folder for temporary files.
   */
  private static String       uploadFolder;

  private final File          repo;

  /**
   * @see #setUploadFolder(String).
   */
  private static Object       lock           = new Object();

  /**
   * @param folder temporary storage for uploaded files
   * @exception IllegalStateException if folder already set
   */
  public static void setUploadFolder(String folder) {
    synchronized (lock) {
      if (uploadFolder != null)
        throw new IllegalStateException("Upload folder already defined");
      uploadFolder = folder;
    }
  }

  public MultipartFormDataEntityProvider() {
    if (uploadFolder == null)
      uploadFolder = System.getProperty("java.io.tmpdir") + File.separator + DEFAULT_FOLDER;
    repo = new File(uploadFolder);
    if (!repo.exists())
      repo.mkdir();

    registerShutdownHook();
  }

  /**
   * Register Shutdown Hook for cleaning temporary files.
   */
  private void registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        File[] files = repo.listFiles();
        for (File file : files) {
          if (file.exists())
            file.delete();
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public boolean isReadable(Class<?> type,
                            Type genericType,
                            Annotation[] annotations,
                            MediaType mediaType) {
    if (type == Iterator.class) {
      try {
        ParameterizedType t = (ParameterizedType) genericType;
        Type[] ta = t.getActualTypeArguments();
        if (ta.length == 1 && ta[0] == FileItem.class) {
          return true;
        }
        return false;
      } catch (ClassCastException e) {
        return false;
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Iterator<FileItem> readFrom(Class<Iterator<FileItem>> type,
                                     Type genericType,
                                     Annotation[] annotations,
                                     MediaType mediaType,
                                     MultivaluedMap<String, String> httpHeaders,
                                     InputStream entityStream) throws IOException {
    /*
     * Try to get HttpServletRequest from environment context. Don't read data
     * from supplied input stream but pass full HttpServletRequest to file
     * uploder.
     */
    try {
      EnvironmentContext envctx = EnvironmentContext.getCurrent();
      if (envctx == null) {
        LOG.warn("EnvironmentContext is not set.");
        return null;
      }
      if (envctx.get(HttpServletRequest.class) == null) {
        LOG.warn("HttpServletRequest is not set in current environment context.");
        return null;
      }

      DefaultFileItemFactory factory = new DefaultFileItemFactory(IOHelper.getMaxBufferSize(), repo);
      FileUpload upload = new FileUpload(factory);
      return upload.parseRequest((HttpServletRequest) envctx.get(HttpServletRequest.class))
                   .iterator();
    } catch (FileUploadException e) {
      throw new IOException("Can't process multipart data item " + e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public long getSize(Iterator<FileItem> t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isWriteable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType) {
    // output is not supported
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public void writeTo(Iterator<FileItem> t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException {
    throw new UnsupportedOperationException();
  }

}
