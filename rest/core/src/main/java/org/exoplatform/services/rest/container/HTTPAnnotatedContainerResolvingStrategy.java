/**
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
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
 * Created by The eXo Platform SARL .
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class HTTPAnnotatedContainerResolvingStrategy implements
    ResourceContainerResolvingStrategy {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.container.ResourceContainerResolvingStrategy#resolve
   *      (org.exoplatform.services.rest.container.ResourceContainer)
   */
  final public List<ResourceDescriptor> resolve(final ResourceContainer resourceContainer) {
    List<ResourceDescriptor> resources = new ArrayList<ResourceDescriptor>();
    for (Method method : resourceContainer.getClass().getMethods()) {
      HTTPResourceDescriptor descr = methodMapping(method, resourceContainer);
      if (descr != null) {
        resources.add(descr);
      }
    }
    return resources;
  }

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

  /*
   * Glue two Strings in one. Is used for creation uri string.
   */
  private String glueUri(String middleUri, URITemplate u) {
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

  /*
   * Get URI string from URITemplate annotation.
   */
  private String middleUri(Class<? extends ResourceContainer> clazz) {
    Annotation anno = clazz.getAnnotation(URITemplate.class);
    if (anno == null) {
      return "";
    }
    return ((URITemplate) anno).value();
  }

  /**
   * Consists information about ResourceContainer.
   */
  public class HTTPResourceDescriptor implements ResourceDescriptor {

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
    
    private ResourceContainer resourceContainer;

    /**
     * @param method the method of class ResourceContainer
     * @param httpMethodName HTTP method name
     * @param uri URI for serving by current method of ResourceContainer
     * @param resourceContainer ResourceContainer
     */
    public HTTPResourceDescriptor(final Method method, final String httpMethodName,
        final String uri, final ResourceContainer resourceContainer) {

      this.servingMethod = method;
      this.httpMethodName = httpMethodName;
      this.uriPattern = new URIPattern(uri);
      this.resourceContainer = resourceContainer;

      methodParameters = servingMethod.getParameterTypes();
      methodParameterAnnotations = resolveParametersAnnotations();

      ConsumedMimeTypes consumedMimeTypesAnnotation = method
          .getAnnotation(ConsumedMimeTypes.class);
      ProducedMimeTypes producedMimeTypesAnnotation = method
          .getAnnotation(ProducedMimeTypes.class);
      consumedMimeTypes = (consumedMimeTypesAnnotation != null) ? consumedMimeTypesAnnotation
          .value()
          : MimeTypes.ALL;
      producedMimeTypes = (producedMimeTypesAnnotation != null) ? producedMimeTypesAnnotation
          .value()
          : MimeTypes.ALL;

      QueryTemplate queryParamFilter = method
          .getAnnotation(QueryTemplate.class);
      queryPattern = (queryParamFilter != null) ? QueryUtils
          .parseQueryString(queryParamFilter.value())
          : new MultivaluedMetadata();

      InputTransformer containerInputTransformer = resourceContainer.getClass()
          .getAnnotation(InputTransformer.class);
      InputTransformer methodInputTransformer = method
          .getAnnotation(InputTransformer.class);
      if (containerInputTransformer != null && methodInputTransformer == null) {
        inputTransformerType = containerInputTransformer.value();
      } else if (methodInputTransformer != null) {
        inputTransformerType = methodInputTransformer.value();
      }

      OutputTransformer containerOutputTransformer = resourceContainer
          .getClass().getAnnotation(OutputTransformer.class);
      OutputTransformer methodOutputTransformer = method
          .getAnnotation(OutputTransformer.class);
      if (containerOutputTransformer != null && methodOutputTransformer == null) {
        outputTransformerType = containerOutputTransformer.value();
      } else if (methodOutputTransformer != null) {
        outputTransformerType = methodOutputTransformer.value();
      }
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getResourceContainer()
     */
    final public ResourceContainer getResourceContainer() {
      return resourceContainer;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getServer()
     */
    final public Method getServer() {
      return servingMethod;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getInputTransformerType()
     */
    final public Class<? extends InputEntityTransformer> getInputTransformerType() {
      return inputTransformerType;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getOutputTransformerType()
     */
    final public Class<? extends OutputEntityTransformer> getOutputTransformerType() {
      return outputTransformerType;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getMethodParameterAnnotations()
     */
    final public Annotation[] getMethodParameterAnnotations() {
      return methodParameterAnnotations;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getMethodParameters()
     */
    final public Class<?>[] getMethodParameters() {
      return methodParameters;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getURIPattern()
     */
    final public URIPattern getURIPattern() {
      return uriPattern;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getAcceptableMethod()
     */
    final public String getAcceptableMethod() {
      return httpMethodName;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getConsumedMimeTypes()
     */
    final public String getConsumedMimeTypes() {
      return consumedMimeTypes;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getProducedMimeTypes()
     */
    final public String getProducedMimeTypes() {
      return producedMimeTypes;
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.services.rest.container.ResourceDescriptor#getAnnotaitedQueryParams()
     */
    final public MultivaluedMetadata getQueryPattern() {
      return queryPattern;
    }

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
