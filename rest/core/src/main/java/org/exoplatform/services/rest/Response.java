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

package org.exoplatform.services.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.rest.transformer.OutputEntityTransformer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

/**
 * REST Response.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Response {
  /**
   * Response status.
   */
  private int status;
  
  /**
   * Response entity. 
   */
  private Object entity;
  
  /**
   * Entity metadata.
   */
  private EntityMetadata metadata;
  
  /**
   * Response headers.
   */
  private MultivaluedMetadata responseHeaders;
  
  /**
   * Cookies.
   */
  private List<Cookie> cookies;
  
  /**
   * Output transformer, can be null if response has not entity.
   */
  private OutputEntityTransformer transformer;
  
  /**
   * Extra parameters for transformer. 
   */
  private Map<String, String> transformerParameters;

  /**
   * @param status the HTTP status.
   * @param responseHeaders the HTTP headers.
   * @param entity the representation requested object.
   * @param cookies the cookies.
   * @param transformer the entity serializator.
   * @param transformerParameters the additional parameters for transformer. 
   */
  protected Response(int status, MultivaluedMetadata responseHeaders,
      Object entity, List<Cookie> cookies, OutputEntityTransformer transformer,
      Map<String, String> transformerParameters) {
    this.status = status;
    this.responseHeaders = responseHeaders;
    this.entity = entity;
    this.cookies = cookies;
    this.transformer = transformer;
    this.transformerParameters = transformerParameters;
    this.metadata = new EntityMetadata(responseHeaders);
  }

  /**
   * HTTP status of response.
   * @return the HTTP status.
   */
  public int getStatus() {
    return status;
  }

  /**
   * HTTP headers.
   * @return the HTTP headers for response
   */
  public MultivaluedMetadata getResponseHeaders() {
    return responseHeaders;
  }

  /**
   * Body of HTTP response. Entity is a representation of requested resource.
   * @return the entity
   */
  public Object getEntity() {
    return entity;
  }
  
  /**
   * @return the cookies that will be added to response. 
   */
  public List<Cookie> getCookies() {
    return cookies;
  }

  /**
   * EntityMetadata gives possibility to view some response headers by use
   * method get.<br/> For example:<br/>
   * <pre>
   * metadata.getMediaType()
   * </pre> - Content-Type header.
   * @return the entity meta data.
   * @see org.exoplatform.services.rest.EntityMetadata.
   */
  public EntityMetadata getEntityMetadata() {
    return metadata;
  }

  /**
   * Is transformer initialized.
   * @return the state of OutputEntityTransformer.
   */
  public boolean isTransformerInitialized() {
    return transformer != null;
  }

  /**
   * Is entity initialized. For HTTP it means has request entity body or not.
   * @return the entity state.
   */
  public boolean isEntityInitialized() {
    return entity != null;
  }
  
  /**
   * Get parameters for OutputEntityTransformer.
   * This parameters can be set by ResourceContainer.
   * @return OutputEntityTransformer parameters;
   */
  public Map<String, String> getTransformerParameters() {
    return transformerParameters;
  }

  /**
   * Set entity transformer.
   * @param transformer the transformer which can serialize entity.
   */
  public void setTransformer(OutputEntityTransformer transformer) {
    this.transformer = transformer;
  }

  /**
   * Write entity to output stream.
   * @param outputEntityStream the output stream for writing entity.
   * @throws IOException Input/Output Exception.
   */
  public void writeEntity(OutputStream outputEntityStream) throws IOException {
    if (isTransformerInitialized() && isEntityInitialized()) {
      transformer.writeTo(entity, outputEntityStream);
    }
  }

  /**
   * Get an entity transformer.
   * @return transformer the transformer which can serialize entity
   */
  public OutputEntityTransformer getTransformer() {
    return transformer;
  }

  /**
   * REST Response builder.
   */
  public static class Builder {

    /**
     * HTTP status.
     */
    private int status;
    
    /**
     * Entity data. 
     */
    private Object entity;
    
    /**
     * HTTP headers.
     */
    private MultivaluedMetadata responseHeaders = new MultivaluedMetadata();
    
    /**
     * Cookies.
     */
    private List<Cookie> cookies;
    
    /**
     * Entity output transformer.
     */
    private OutputEntityTransformer transformer;
    
    /**
     * Additional parameters for transformer. 
     */
    private Map<String, String> transformerParameters;

    /**
     * Must not be visible for external class.
     */
    protected Builder() {
    }

    /**
     * Create a new instance of builder.
     * @return the new instance of Builder.
     */
    protected static synchronized Builder newInstance() {
      return new Builder();
    }

    /**
     * create REST Response with parameters collected by Builder.
     * @return the REST Response.
     */
    public Response build() {
      return new Response(status, responseHeaders, entity, cookies,
          transformer, transformerParameters);
    }

    /**
     * create Builder with selected status.
     * @param st the HTTP status
     * @return the new instance of Builder with given status.
     */
    public static Builder withStatus(int st) {
      Builder b = newInstance();
      b.status(st);
      return b;
    }

    /**
     * create Builder with HTTP status 200 (OK) and entity.
     * @param e the entity.
     * @return the new Builder instance with status 200 (OK) and entity.
     */
    public static Builder representation(Object e) {
      Builder b = newInstance();
      b.status(HTTPStatus.OK);
      b.entity(e);
      return b;
    }

    /**
     * create Builder with HTTP status 200 (OK), entity and set entity type.
     * @param e the entity.
     * @param type the entity type.
     * @return the new instance of Builder with status 200 (OK), entity and
     *         media type.
     */
    public static Builder representation(Object e, String type) {
      Builder b = representation(e);
      b.mediaType(type);
      return b;
    }

    /**
     * create Builder with HTTP status 200 (OK).
     * @return new instance of Builder with status 200 (OK).
     */
    public static Builder ok() {
      Builder b = newInstance();
      b.status(HTTPStatus.OK);
      return b;
    }

    /**
     * create Builder with HTTP status 200 (OK) and entity.
     * @param e the entity.
     * @return the new Builder instance with status 200 (OK) and entity.
     */
    public static Builder ok(Object e) {
      Builder b = ok();
      b.entity(e);
      return b;
    }

    /**
     * create Builder with HTTP status 200 (OK),entity and set entity type.
     * @param e the entity.
     * @param mediaType the entity type.
     * @return the new instance of Builder with status 200 (OK), entity and
     *         media type.
     */
    public static Builder ok(Object e, String mediaType) {
      Builder b = ok(e).mediaType(mediaType);
      return b;
    }

    /**
     * create Builder with status 201 (CREATED).
     * @param location the location created resources.
     * @return the new instance of Builder with status 201 (CREATED) and header
     *         Location.
     */
    public static Builder created(String location) {
      Builder b = newInstance();
      b.status(HTTPStatus.CREATED);
      b.responseHeaders.putSingle("Location", location);
      return b;
    }

    /**
     * create Builder with status 201 (CREATED) and entity.
     * @param e the entity.
     * @param location the location created resources.
     * @return the new instance of Builder with status 201 (CREATED), header
     *         Location and Location in entity body.
     */
    public static Builder created(Object e, String location) {
      Builder b = created(location);
      b.entity(e);
      return b;
    }

    /**
     * create Builder with HTTP status 202 (ACCEPTED).
     * @return the new instance of Builder with status 202 (ACCEPTED).
     */
    public static Builder accepted() {
      Builder b = newInstance();
      b.status(HTTPStatus.ACCEPTED);
      return b;
    }

    /**
     * create Builder with HTTP status 204 (NO CONTENT).
     * @return the new instance of Builder with status 204 (NO CONTENT).
     */
    public static Builder noContent() {
      Builder b = newInstance();
      b.status(HTTPStatus.NO_CONTENT);
      return b;
    }

    /**
     * created Builder with HTTP status 307 (TEMPORARY REDIRECT).
     * @param location the new resource location.
     * @return the new instance of Builder with status 307 (TEMPORARY REDIRECT).
     */
    public static Builder temporaryRedirect(String location) {
      Builder b = newInstance();
      b.status(HTTPStatus.TEMP_REDIRECT);
      b.locations(location);
      return b;
    }

    /**
     * create Builder with HTTP status 304 (NOT MODIFIED).
     * @return the new instance of Builder with status 304 (NOT MODIFIED).
     */
    public static Builder notModified() {
      Builder b = newInstance();
      b.status(HTTPStatus.NOT_MODIFIED);
      return b;
    }

    /**
     * create Builder with HTTP status 304 (NOT MODIFIED) and HTTP EntityTag.
     * @param tag the HTTP EntityTag.
     * @return the new instance of Builder with status 304 (NOT MODIFIED) and
     *         HTTP EntityTag.
     */
    public static Builder notModified(String tag) {
      Builder b = notModified();
      b.tag(tag);
      return b;
    }

    /**
     * create Builder with HTTP status 403 (FORBIDDEN).
     * @return the new instance of Builder with status 403 (FORBIDDEN).
     */
    public static Builder forbidden() {
      Builder b = newInstance();
      b.status(HTTPStatus.FORBIDDEN);
      return b;
    }

    /**
     * create Builder with HTTP status 404 (NOT FOUND).
     * @return the new instance of Builder with status 404 (NOT FOUND).
     */
    public static Builder notFound() {
      Builder b = newInstance();
      b.status(HTTPStatus.NOT_FOUND);
      return b;
    }

    /**
     * create Builder with HTTP status 400 (BAD REQUEST).
     * @return the new instance of Builder with status 400 (BAD REQUEST).
     */
    public static Builder badRequest() {
      Builder b = newInstance();
      b.status(HTTPStatus.BAD_REQUEST);
      return b;
    }

    /**
     * create Builder with HTTP status 500 (INTERNAL SERVER ERROR).
     * @return the new instance of Builder with status 500 (INTERNAL SERVER
     *         ERROR).
     */
    public static Builder serverError() {
      Builder b = newInstance();
      b.status(HTTPStatus.INTERNAL_ERROR);
      return b;
    }

    /**
     * return Buider with changed status.
     * @param st the HTTP status.
     * @return the Builder with changed status.
     */
    public Builder status(int st) {
      status = st;
      return this;
    }

    /**
     * add entity to the Builder.
     * @param e the entity (object with represents requested resource).
     * @return the Builder with added entity.
     */
    public Builder entity(Object e) {
      entity = e;
      return this;
    }

    /**
     * add entity to the Builder and set entity Content-Type.
     * @param e the entity (object with represents requested resource).
     * @param mediaType the entity media type.
     * @return the Builder with added entity and media type.
     */
    public Builder entity(Object e, String mediaType) {
      entity = e;
      this.responseHeaders.putSingle("Content-Type", mediaType);
      return this;
    }

    /**
     * set entity Content-Type.
     * @param mediaType the entity media type.
     * @return the Builder with added media type.
     */
    public Builder mediaType(String mediaType) {
      this.responseHeaders.putSingle("Content-Type", mediaType);
      return this;
    }

    /**
     * set Content-Language.
     * @param languages the List &lt; String &gt; of language.
     * @return the Builder with added header "Content-Language".
     */
    public Builder languages(List<String> languages) {
      this.responseHeaders.put("Content-Language", languages);
      return this;
    }

    /**
     * set Content-Encoding.
     * @param encodings the List &lt; String &gt; of content encoding.
     * @return the Builder with added header "Content-Encoding".
     */
    public Builder encodings(List<String> encodings) {
      this.responseHeaders.put("Content-Encoding", encodings);
      return this;
    }

    /**
     * set Location header.
     * @param location the Location string.
     * @return the Builder with added "Location" header.
     */
    public Builder locations(String location) {
      this.responseHeaders.putSingle("Location", location);
      return this;
    }

    /**
     * set Last-Modified header.
     * @param lastModified the date of last change in resource.
     * @return the Builder with added "Last-Modified" header.
     */
    public Builder lastModified(Date lastModified) {
      this.responseHeaders.putSingle("Last-Modified", DateFormat.getInstance()
          .format(lastModified));
      return this;
    }

    /**
     * set Content-Length header.
     * @param length the size of data in bytes.
     * @return the Builder with added "Content-Length" header.
     */
    public Builder contentLenght(long length) {
      this.responseHeaders.putSingle("Content-Length", length + "");
      return this;
    }

    /**
     * set HTTP EntityTag header.
     * @param tag the HTTP entity tag.
     * @return the Builder with added "ETag" header.
     */
    public Builder tag(String tag) {
      this.responseHeaders.putSingle("ETag", tag);
      return this;
    }

    /**
     * set OutputEntityTransformer.
     * @param trf the output transformer.
     * @see org.exoplatform.services.rest.transformer.OutputEntityTransformer.
     * @return the Builder with added entity transformer.
     */
    public Builder transformer(OutputEntityTransformer trf) {
      this.transformer = trf;
      return this;
    }
    
    /**
     * set parameters for OutputEntityTransformer.
     * @param trfParams parameters.
     * @return the Builder with added transformer parameters.
     */
    public Builder setTransformerParameters(Map<String, String> trfParams) {
      this.transformerParameters = trfParams;
      return this;
    }

    /**
     * add response single header.
     * @param key the key.
     * @param value the value.
     * @return the Builder with added single header.
     */
    public Builder header(String key, String value) {
      this.responseHeaders.putSingle(key, value);
      return this;
    }

    /**
     * set response headers.
     * @param headers key-values pair of HTTP response header.
     * @return the Builder with new sets of HTTP headers.
     */
    public Builder headers(MultivaluedMetadata headers) {
      this.responseHeaders = headers;
      return this;
    }

    /**
     * @param c the CacheControl.
     * @see org.exoplatform.services.rest.CacheControl.
     * @return the Builder with added Cache-Control header.
     */
    public Builder cacheControl(CacheControl c) {
      this.responseHeaders.putSingle("Cache-Control", c.getAsString());
      return this;
    }
    
    /**
     * Add new cookies.
     * @param cc the new cookies that will be added to response.
     * @return the Builder instance.
     */
    public Builder cookies(Cookie... cc) {
      if (this.cookies == null)
        this.cookies = new ArrayList<Cookie>(cc.length);
      for (Cookie c : cc)
        this.cookies.add(c);
      return this;
    }

    /**
     * Prepared error response and set transformer.
     * @param message - error message.
     * @return the instance of Builder with entity represented by String and
     *         StringOutputTransformer.
     */
    public Builder errorMessage(String message) {
      this.entity = message;
      this.transformer = new StringOutputTransformer();
      return this;
    }
    
  }

}
