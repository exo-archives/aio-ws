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

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.URIPattern;
import org.exoplatform.services.rest.transformer.InputEntityTransformer;
import org.exoplatform.services.rest.transformer.OutputEntityTransformer;

/**
 * Created by The eXo Platform SAS. <br/> Abstraction of resource description.<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public interface ResourceDescriptor {
  /**
   * HTTP method for serving by ResourceContainer.
   * @return HTTP method name.
   */
  String getAcceptableMethod();

  /**
   * Describe mimetype(s) which RessourceContainer can consume.
   * @return mimetype(s).
   */
  String getConsumedMimeTypes();

  /**
   * Describe mimetype(s) which RessourceContainer can produce.
   * @return mimetype(s).
   */
  String getProducedMimeTypes();

  /**
   * @return MultivaluedMetadata.
   */
  MultivaluedMetadata getQueryPattern();

  /**
   * Object of ResourecContainer.
   * @return ResourceContainer.
   */
  ResourceContainer getResourceContainer();

  /**
   * Method of ResourceContainer object. Then this method may be called by ResourceDispatcher.
   * @return Method.
   */
  Method getServer();

  /**
   * Return URIPattern object.
   * @return URIPattern.
   */
  URIPattern getURIPattern();

  /**
   * Return the array of annotation for method parameters.
   * @return method parameter annotations.
   */
  Annotation[] getMethodParameterAnnotations();

  /**
   * Return the array of types method parameters.
   * @return method parameters types.
   */
  Class<?>[] getMethodParameters();

  /**
   * Return Class of OutputEntityTransformer. ResourceDispatcher uses this class
   * for creation instance of OutputEntityTransformer.
   * @return Class of OutputEntityTransformer.
   */
  Class<? extends OutputEntityTransformer> getOutputTransformerType();

  /**
   * Return Class of InputEntityTransformer. ResourceDispatcher uses this class
   * for creation instance of InputEntityTransformer.
   * @return Class of InputEntityTransformer.
   */
  Class<? extends InputEntityTransformer> getInputTransformerType();

}
