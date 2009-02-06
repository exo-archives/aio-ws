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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * Simple service for REST demo.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("/samples/json")
public class JsonBookService implements ResourceContainer {

  /**
   * BookStorage. 
   */
  private BookStorage bookStorage;
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(JsonBookService.class.getName());
  
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
  @GET
  @Path("{key}")
  @Produces({MediaType.APPLICATION_JSON})
  public Book get(@PathParam("key") String key) {    
    Book book = bookStorage.getBook(key);
    if (book == null)
      throw new IllegalArgumentException("Book with supplied key " + key + " not found") ;
    LOG.info("GET: " + book);
    LOG.info("In storage now: " + bookStorage.numberOfBooks());
    return book;  
  }
                                                                                                                                                                                                               
  /**
   * Create new Book in BookStorage with specified key.
   * @param key the key.
   * @param baseURI the base URL.
   * @param book Book.
   * @return response with status 201, @see {@link Response} .
   */
  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  public Response post(@Context UriInfo uriInfo, Book book) {    
    LOG.info("POST: " + book);
    bookStorage.addBook(book.getIsdn(), book);
    LOG.info("In storage now: " + bookStorage.numberOfBooks());
    URI location = UriBuilder.fromUri(uriInfo.getBaseUri()).path(this.getClass()).path(book.getIsdn()).build();
    return Response.created(location).build();  
  }
    
}

