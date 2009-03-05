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

package org.exoplatform.services.ws.rest.samples;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ContextParam;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.ws.frameworks.json.transformer.Bean2JsonOutputTransformer;
import org.exoplatform.ws.frameworks.json.transformer.Json2BeanInputTransformer;

/**
 * Simple service for REST demo.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonBookService implements ResourceContainer {

  /**
   * BookStorage. 
   */
  private BookStorage bookStorage;
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.JsonBookService");
  
  /**
   * Constructs new instance of JsonBookService. 
   * @param books BookStorage.
   */
  public JsonBookService(BookStorage books) {
    bookStorage = books;
  }
  
  /**
   * Return request with JSON body which represent Book object.
   * @param key the key.
   * @return @see {@link Response} .
   */
  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/json/{key}/")                                                                                                                                                                                   
  @OutputTransformer(Bean2JsonOutputTransformer.class)                                                                                                                                                         
  public Response get(@URIParam("key") String key) {    
    Book book = bookStorage.getBook(key);
    if (book == null)
      return Response.Builder.notFound().build();
    LOG.info("GET: " + book);
    LOG.info("In storage now: " + bookStorage.numberOfBooks());
    return Response.Builder.ok(bookStorage.getBook(key)).mediaType("application/json").build();  
  }
                                                                                                                                                                                                               
  /**
   * Create new Book in BookStorage with specified key.
   * @param key the key.
   * @param baseURI the base URL.
   * @param book Book.
   * @return response with status 201, @see {@link Response} .
   */
  @HTTPMethod("POST")                                                                                                                                                                                           
  @URITemplate("/json/{key}/")                                                                                                                                                                                   
  @InputTransformer(Json2BeanInputTransformer.class)                                                                                                                                                         
  public Response post(@URIParam("key") String key,
          @ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI) String baseURI,
          Book book) {    
    LOG.info("POST: " + book);
    bookStorage.addBook(key, book);
    LOG.info("In storage now: " + bookStorage.numberOfBooks());
    return Response.Builder.created(baseURI + "json" + "/" + key).build();  
  }
    
}

