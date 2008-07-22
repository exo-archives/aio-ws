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

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.XMLOutputTransformer;
import org.exoplatform.services.rest.transformer.XSLT4DOMOutputTransformer;
import org.exoplatform.services.rest.transformer.XSLTConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Simple service for REST demo.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class XMLBookService implements ResourceContainer {
  
  /**
   * Books storage.
   */
  private BookStorage bookStorage;
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.XMLBookService");
  
  /**
   * Constructs new instance of XMLBookService. 
   * @param books BookStorage.
   */
  public XMLBookService(BookStorage books) {
    bookStorage = books;
  }
  
  /**
   * Return request with XML body which represent Book object.
   * @param key the key.
   * @return @see {@link Response} .
   */
  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/xml/{key}/")                                                                                                                                                                                   
  @OutputTransformer(XMLOutputTransformer.class)                                                                                                                                                         
  public Response get(@URIParam("key") String key) {    
    Book book = bookStorage.getBook(key);
    if (book == null)
      return Response.Builder.notFound().build();
    LOG.info("GET: " + book);
    Document doc = null;
    try {
      doc = createDocument(book);
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(500).errorMessage(e.getMessage()).build();
    }
    return Response.Builder.ok(doc).mediaType("text/xml").build();  
  }
                                                                                                                                                                                                               
  /**
   * Return request with HTML body which represent Book object.
   * @param key the key.
   * @return @see {@link Response} .
   */
  @HTTPMethod("GET")                                                                                                                                                                                           
  @URITemplate("/html/{key}/")                                                                                                                                                                                   
  @OutputTransformer(XSLT4DOMOutputTransformer.class)                                                                                                                                                         
  public Response get2(@URIParam("key") String key) {    
    Book book = bookStorage.getBook(key);
    if (book == null)
      return Response.Builder.notFound().build();
    LOG.info("GET: " + book);
    Document doc = null;
    try {
      doc = createDocument(book);
    } catch (Exception e) {
      e.printStackTrace();
      return Response.Builder.withStatus(500).errorMessage(e.getMessage()).build();
    }
    Map<String, String> p = new HashMap<String, String>();
    p.put(XSLTConstants.XSLT_TEMPLATE, "book");
    return Response.Builder.ok(doc).setTransformerParameters(p)
      .mediaType("text/html").build();  
  }
  
  /**
   * Create XML representation of Book object.
   * @param book Book.
   * @return Document.
   * @throws Exception if any errors occurs.
   */
  private Document createDocument(Book book) throws Exception {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = doc.createElement("book");
    Element isdn = doc.createElement("isdn");
    isdn.setTextContent(book.getIsdn());
    Element author = doc.createElement("author");
    author.setTextContent(book.getAuthor());
    Element title = doc.createElement("title");
    title.setTextContent(book.getTitle());
    Element pages = doc.createElement("pages");
    pages.setTextContent(book.getPages() + "");
    Element price = doc.createElement("price");
    price.setTextContent(book.getPrice() + "");
      
    root.appendChild(isdn);
    root.appendChild(title);
    root.appendChild(author);
    root.appendChild(pages);
    root.appendChild(price);

    doc.appendChild(root);
    return doc;
  }

}
