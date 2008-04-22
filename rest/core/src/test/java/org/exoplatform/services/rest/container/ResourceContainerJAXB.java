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

package org.exoplatform.services.rest.container;

import java.math.BigDecimal;

import org.exoplatform.services.rest.transformer.JAXBInputTransformer;
import org.exoplatform.services.rest.transformer.JAXBOutputTransformer;
import org.exoplatform.services.rest.generated.*;

import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@URITemplate("/test/jaxb/")
public class ResourceContainerJAXB implements ResourceContainer {

  @HTTPMethod("GET")
  @InputTransformer(JAXBInputTransformer.class)
  @OutputTransformer(JAXBOutputTransformer.class)
  public Response method1(Book book) throws Exception {

    System.out.println(">>> JAXBTransformation");
    System.out.println("==Book Card==");
    System.out.println("===> tittle: " + book.getTitle());
    System.out.println("===> author: " + book.getAuthor());
    System.out.println("===> currency price: " + book.getPrice().getCurrency());
    System.out.println("===> currency member price: " +
        book.getMemberPrice().getCurrency());
    System.out.println("===> price: " + book.getPrice().getValue());
    System.out
        .println("===> member price: " + book.getMemberPrice().getValue());
    book.setTitle("Red Hat Enterprise Linux 5 Administration Unleashed");
    book.setAuthor("Tammy Fox");
    book.setSendByPost(false);
    book.setPrice(createPrice("EUR", 21.75f));
    book.setMemberPrice(createMemberPrice("EUR", 17.25f));

    return Response.Builder.ok(book, "text/xml").build();
  }

  private Price createPrice(String currency, Float value) {
    Price price = new Price();
    price.setCurrency(currency);
    price.setValue(new BigDecimal(value));
    return price;
  }

  private MemberPrice createMemberPrice(String currency, Float value) {
    MemberPrice mprice = new MemberPrice();
    mprice.setCurrency(currency);
    mprice.setValue(new BigDecimal(value));
    return mprice;
  }

}
