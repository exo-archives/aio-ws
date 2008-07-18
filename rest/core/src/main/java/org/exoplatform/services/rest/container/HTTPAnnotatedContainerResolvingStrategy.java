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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.QueryTemplate;
import org.exoplatform.services.rest.URIPattern;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.ConsumedMimeTypes;
import org.exoplatform.services.rest.ProducedMimeTypes;
import org.exoplatform.services.rest.data.MimeTypes;
import org.exoplatform.services.rest.data.QueryUtils;
import org.exoplatform.services.rest.transformer.InputEntityTransformer;
import org.exoplatform.services.rest.transformer.OutputEntityTransformer;

/**
 * Created by The eXo Platform SAS .
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public final class HTTPAnnotatedContainerResolvingStrategy implements
    ResourceContainerResolvingStrategy {

  /**
   * {@inheritDoc}
   */
  public List<ResourceDescriptor> resolve(final ResourceContainer resourceContainer) {
    List<ResourceDescriptor> resources = new ArrayList<ResourceDescriptor>();
    for (Method method : resourceContainer.getClass().getMethods()) {
      HTTPResourceDescriptor descr = methodMapping(method, resourceContainer);
      if (descr != null) {
        resources.add(descr);
      }
    }
    return resources;
  }

  /**
   * Create HTTPResourceDescriptor.
   * @param method the method of class. This class must implement {@link ResourceContainer).
   * @param resourceCont a ResourceContainer.
   * @return HTTPResourceDescriptor or null if method has not reqired annotations.
   */
  private HTTPResourceDescriptor methodMapping(final Method method,
      final ResourceContainer resourceCont) {
    String middleUri = middleUri(resourceCont.getClass());
    HTTPMethod httpMethodAnnotation = method.getAnnotation(HTTPMethod.class);
    URITemplate uriTemplateAnnotation = method.getAnnotation(URITemplate.class);
    if (httpMethodAnnotation != null
        && (uriTemplateAnnotation != null || !"".equals(middleUri))) {

      String uri = (!"".equals(middleUri)) ? glueUri(middleUri,
          uriTemplateAnnotation) : uriTemplateAnnotation.value();
      String httpMethodName = httpMethodAnnotation.value();
      return new HTTPResourceDescriptor(method, httpMethodName, uri,
          resourceCont);
    }
    return null;
  }

  /**
   * Glue two Strings in one. Is used for creation URI string.
   */
  /**
   * @param middleUri the {@link URITemplate} from class.
   * @param u the {@link URITemplate} from method.
   * @return result String.
   */
  private static String glueUri(String middleUri, URITemplate u) {
    if (u == null) {
      return middleUri;
    }
    String uri = u.value();
    if (middleUri.endsWith("/") && uri.startsWith("/")) {
      uri = middleUri + uri.replaceFirst("/", "");
    } else if (!middleUri.endsWith("/") && !uri.startsWith("/")) {
      uri = middleUri + "/" + uri;
    } else {
      uri = middleUri + uri;
    }
    return uri;
  }

  /**
   * Get URI string from URITemplate annotation.
   * @param clazz the class.
   * @return {@link URITemplate} from class or null if not specified.
   */
  private static String middleUri(Class<? extends ResourceContainer> clazz) {
    Annotation anno = clazz.getAnnotation(URITemplate.class);
    if (anno == null) {
      return "";
    }
    return ((URITemplate) anno).value();
  }

  /**
   * Consists information about ResourceContainer.
   */
  public final class HTTPResourceDescriptor implements ResourceDescriptor {

    /**
     * HTTP method name.
     */
    private String httpMethodName;
    
    /**
     * @see org.exoplatform.services.rest.URIPattern.
     */
    private URIPattern uriPattern;
    
    /**
     * MimeTypes which can be consumed by resource.
     */
    private String consumedMimeTypes;
    
    /**
     * MimeTypes which can be produced by resource.
     */
    private String producedMimeTypes;
    
    /**
     * QueryPattern represented by MultivaluedMetadata object.
     */
    private MultivaluedMetadata queryPattern;
    
    /**
     * InputTransformer.
     */
    private Class<? extends InputEntityTransformer> inputTransformerType;
    
    /**
     * OutputTransformer.
     */
    private Class<? extends OutputEntityTransformer> outputTransformerType;
    
    /**
     * Method.
     */
    private Method servingMethod;
    
    /**
     * Annotation of Method's parameters.
     */
    private Annotation[] methodParameterAnnotations;
    
    /**
     * Method parameters.
     */
    private Class<?>[] methodParameters;
    
    /**
     * ResourceCOntainer.
     */
    private ResourceContainer resourceContainer;

    /**
     * @param method the method of class ResourceContainer.
     * @param httpMethodName HTTP method name.
     * @param uri URI for serving by current method of ResourceContainer.
     * @param resourceContainer ResourceContainer.
     */
    public HTTPResourceDescriptor(final Method method, final String httpMethodName,
        final String uri, final ResourceContainer resourceContainer) {

      this.servingMethod = method;
      this.httpMethodName = httpMethodName;
      this.uriPattern = new URIPattern(uri);
      this.resourceContainer = resourceContainer;

      this.methodParameters = method.getParameterTypes();
      this.methodParameterAnnotations = resolveParametersAnnotations();

      ConsumedMimeTypes consumedMimeTypesAnnotation = method.getAnnotation(ConsumedMimeTypes.class);
      ProducedMimeTypes producedMimeTypesAnnotation = method.getAnnotation(ProducedMimeTypes.class);
      this.consumedMimeTypes = consumedMimeTypesAnnotation != null
          ? consumedMimeTypesAnnotation.value() : MimeTypes.ALL;
      this.producedMimeTypes = producedMimeTypesAnnotation != null
          ? producedMimeTypesAnnotation.value() : MimeTypes.ALL;

      QueryTemplate queryParamFilter = method.getAnnotation(QueryTemplate.class);
      queryPattern = queryParamFilter != null
          ? QueryUtils.parseQueryString(queryParamFilter.value()) : new MultivaluedMetadata();

      InputTransformer containerInputTransformer = resourceContainer.getClass().getAnnotation(InputTransformer.class);
      InputTransformer methodInputTransformer = method.getAnnotation(InputTransformer.class);
      
      if (containerInputTransformer != null && methodInputTransformer == null) {
        inputTransformerType = containerInputTransformer.value();
      } else if (methodInputTransformer != null) {
        inputTransformerType = methodInputTransformer.value();
      }

      OutputTransformer containerOutputTransformer =
          resourceContainer.getClass().getAnnotation(OutputTransformer.class);
      OutputTransformer methodOutputTransformer = method.getAnnotation(OutputTransformer.class);
      if (containerOutputTransformer != null && methodOutputTransformer == null) {
        outputTransformerType = containerOutputTransformer.value();
      } else if (methodOutputTransformer != null) {
        outputTransformerType = methodOutputTransformer.value();
      }
    }

    /**
     * {@inheritDoc}
     */
    public ResourceContainer getResourceContainer() {
      return resourceContainer;
    }

    /**
     * {@inheritDoc}
     */
    public Method getServer() {
      return servingMethod;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends InputEntityTransformer> getInputTransformerType() {
      return inputTransformerType;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends OutputEntityTransformer> getOutputTransformerType() {
      return outputTransformerType;
    }
    
    /**
     * {@inheritDoc}
     */
    public Annotation[] getMethodParameterAnnotations() {
      return methodParameterAnnotations;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?>[] getMethodParameters() {
      return methodParameters;
    }

    /**
     * {@inheritDoc}
     */
    public URIPattern getURIPattern() {
      return uriPattern;
    }

    /**
     * {@inheritDoc}
     */
    public String getAcceptableMethod() {
      return httpMethodName;
    }

    /**
     * {@inheritDoc}
     */
    public String getConsumedMimeTypes() {
      return consumedMimeTypes;
    }

    /**
     * {@inheritDoc}
     */
    public String getProducedMimeTypes() {
      return producedMimeTypes;
    }

    /**
     * {@inheritDoc}
     */
    public MultivaluedMetadata getQueryPattern() {
      return queryPattern;
    }

    /**
     * Get annotations from method parameters.
     * @return array of annotations.
     */
    private Annotation[] resolveParametersAnnotations() {
      Annotation[][] a = servingMethod.getParameterAnnotations();
      Annotation[] anno = new Annotation[a.length];
      for (int i = 0; i < a.length; i++) {
        if (a[i].length > 0) {
          anno[i] = a[i][0];
        }
      }
      return anno;
    }

  }

}
