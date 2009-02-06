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

package org.exoplatform.services.rest.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.GenericContainerResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ContainerResponse implements GenericContainerResponse {

  /**
   * Logger.
   */
  private static final Log        LOG = ExoLogger.getLogger(ContainerResponse.class.getName());

  /**
   * See {@link ContainerResponseWriter}.
   */
  private ContainerResponseWriter responseWriter;

  /**
   * @param responseWriter See {@link ContainerResponseWriter}
   */
  public ContainerResponse(ContainerResponseWriter responseWriter) {
    this.responseWriter = responseWriter;
  }

  // GenericContainerResponse

  /**
   * HTTP status.
   */
  private int                            status;

  /**
   * Entity type.
   */
  private Type                           entityType;

  /**
   * Entity.
   */
  private Object                         entity;

  /**
   * HTTP response headers.
   */
  private MultivaluedMap<String, Object> headers;

  /**
   * Response entity content-type.
   */
  private MediaType                      contentType;

  /**
   * See {@link Response}, {@link ResponseBuilder}.
   */
  private Response                       response;

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void setResponse(Response response) {
    this.response = response;
    if (response == null)
      reset();

    status = response.getStatus();
    headers = response.getMetadata();
    entity = response.getEntity();
    if (entity instanceof GenericEntity) {
      GenericEntity ge = (GenericEntity) entity;
      entity = ge.getEntity();
      entityType = ge.getType();
    } else {
      if (entity != null)
        entityType = entity.getClass();
    }

    Object contentTypeHeader = getHttpHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
    if (contentTypeHeader instanceof MediaType)
      contentType = (MediaType) contentTypeHeader;
    else if (contentTypeHeader != null)
      contentType = MediaType.valueOf(contentTypeHeader.toString());
    else
      contentType = null;
  }

  /**
   * {@inheritDoc}
   */
  public Response getResponse() {
    return response;
  }

  /**
   * Reset to default status.
   */
  private void reset() {
    this.status = Response.Status.NO_CONTENT.getStatusCode();
    this.entity = null;
    this.entityType = null;
    this.contentType = null;
    this.headers = null;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void writeResponse() throws IOException {
    if (entity == null) {
      responseWriter.writeHeaders(this);
      return;
    }
    // NOTE currently not support annotation at entity.
    // Pass null instead array of annotations in methods:
    // RequestHandler#getAcceptableWriterMediaTypes(Class<?>, Type, null)
    // RequestHandler#getMessageBodyWriter(Class<?>, Type, null, MediaType);

    ApplicationContext context = ApplicationContext.getCurrent();
    MediaType contentType = getContentType();

    // if content-type is still not preset try determine it
    if (contentType == null) {
      List<MediaType> l = context.getRequestHandler()
                                 .getAcceptableWriterMediaTypes(entity.getClass(), entityType, null);
      contentType = context.getContainerRequest().getAcceptableMediaType(l);
      if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype())
        contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE;

      this.contentType = contentType;
      getHttpHeaders().putSingle(HttpHeaders.CONTENT_TYPE, contentType);
    }
    MessageBodyWriter entityWriter = context.getRequestHandler()
                                            .getMessageBodyWriter(entity.getClass(),
                                                                  entityType,
                                                                  null,
                                                                  contentType);
    if (entityWriter == null) {
      String message = "Not found writer for " + entity.getClass() + " and MIME type "
          + contentType;
      if (context.getContainerRequest().getMethod().equals(HttpMethod.HEAD)) {
        // just warning here, HEAD method we do not need write entity
        LOG.warn(message);
        getHttpHeaders().putSingle(HttpHeaders.CONTENT_LENGTH, Long.toString(-1));
      } else {
        LOG.error(message);
        throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).build());
      }
    } else {
      if (getHttpHeaders().getFirst(HttpHeaders.CONTENT_LENGTH) == null) {
        long contentLength = entityWriter.getSize(entity,
                                                  entity.getClass(),
                                                  entityType,
                                                  null,
                                                  contentType);
        if (contentLength >= 0)
          getHttpHeaders().putSingle(HttpHeaders.CONTENT_LENGTH, Long.toString(contentLength));
      }
    }
    if (context.getContainerRequest().getMethod().equals(HttpMethod.HEAD))
      entity = null;

    responseWriter.writeHeaders(this);
    responseWriter.writeBody(this, entityWriter);
  }

  /**
   * {@inheritDoc}
   */
  public MediaType getContentType() {
    return contentType;
  }

  /**
   * {@inheritDoc}
   */
  public Type getEntityType() {
    return entityType;
  }

  /**
   * {@inheritDoc}
   */
  public Object getEntity() {
    return entity;
  }

  /**
   * {@inheritDoc}
   */
  public MultivaluedMap<String, Object> getHttpHeaders() {
    return headers;
  }

  /**
   * {@inheritDoc}
   */
  public int getStatus() {
    return status;
  }

}
