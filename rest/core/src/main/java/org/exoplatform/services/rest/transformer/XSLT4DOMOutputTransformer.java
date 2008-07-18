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

package org.exoplatform.services.rest.transformer;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.exoplatform.services.xml.transform.NotSupportedIOTypeException;
import org.exoplatform.services.xml.transform.trax.TRAXTemplatesService;
import org.exoplatform.services.xml.transform.trax.TRAXTransformer;
import org.w3c.dom.Document;

/**
 * This type of transformer can transform given XML to other mime-type 
 * and write it to output stream.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class XSLT4DOMOutputTransformer extends OutputEntityTransformer {
  
  /**
   * TRAXTemplates service. Contains compiled schemas. 
   */
  private TRAXTemplatesService templatesService;
  
  /**
   * Construct transformer.
   * @param templatesService TRAXTemplates service.
   */
  public XSLT4DOMOutputTransformer(TRAXTemplatesService templatesService) {
    this.templatesService = templatesService;
  }

  /**
   * {@inheritDoc}}
   */
  @Override
  public void writeTo(Object entity, OutputStream entityDataStream)
      throws IOException {
    Document e = (Document) entity;
    String xsltTemplate = transformerParameters.get(XSLTConstants.XSLT_TEMPLATE);
    try {
      TRAXTransformer transformer = null;
      if (xsltTemplate != null) {
        transformer = templatesService.getTemplates(xsltTemplate).newTransformer();
      } else {
        throw new TransformerConfigurationException("Can't get parameter '"
            + XSLTConstants.XSLT_TEMPLATE + "' from OutputTransformer parameters.\n"
            + "It can be set from ResourceContainer, see : "
            + "org.exoplatform.services.rest.Response.Builder#setTransformerParameters(trfParams)\n"
            + "and org.exoplatform.services.rest.transformer.XSLTConctants#XSLT_Tplates");
      }
      transformer.initResult(new StreamResult(entityDataStream));
      transformer.transform(new DOMSource(e));
    } catch (TransformerConfigurationException tce) {
      throw new IOException("Can't write to output stream " + tce);
    } catch (NotSupportedIOTypeException nse) {
      throw new IOException("Can't write to output stream " + nse);
    } catch (TransformerException tre) {
      throw new IOException("Can't write to output stream " + tre);
    }
  }

}
