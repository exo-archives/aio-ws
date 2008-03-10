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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceDescriptor;
import org.exoplatform.services.rest.data.MimeTypes;
import org.exoplatform.services.rest.transformer.EntityTransformerFactory;
import org.exoplatform.services.rest.transformer.InputEntityTransformer;
import org.exoplatform.services.rest.transformer.OutputEntityTransformer;

/**
 * Created by The eXo Platform SAS.<br/> ResourceDispatcher finds
 * ResourceContainer with can serve the Request and calls method it.
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class ResourceDispatcher implements Connector {

  public static final String CONTEXT_PARAM_HOST = "host";
  public static final String CONTEXT_PARAM_BASE_URI = "baseURI";
  public static final String CONTEXT_PARAM_REL_URI = "relURI";
  public static final String CONTEXT_PARAM_ABSLOCATION = "absLocation";

  private List<ResourceDescriptor> resourceDescriptors_;
  private ThreadLocal<Context> contextHolder_ = new ThreadLocal<Context>();
  private final Map<String, String> contextParams_ = new HashMap<String, String>();
          
//  private ExoContainer container_;
  private EntityTransformerFactory factory_;

//  private static final Log LOGGER = ExoLogger.getLogger("ResourceDispatcher");
  
  /**
   * Constructor gets all binded ResourceContainers from ResourceBinder.
   * @param containerContext ExoContainerContext
   * @throws Exception any Exception
   */
  public ResourceDispatcher(InitParams params, ResourceBinder binder,
      EntityTransformerFactory factory) throws Exception {
    
    if (params != null) {
      PropertiesParam contextParam = params.getPropertiesParam("context-params");
      if (contextParam != null) {
        Iterator<Property> iterator = contextParam.getPropertyIterator();
        while (iterator.hasNext()) {
          Property property = iterator.next();
          contextParams_.put(property.getName(), property.getValue());
        }
      }
    }
    this.resourceDescriptors_ = binder.getAllDescriptors();
    this.factory_ = factory;
  }

  /**
   * Dispatches Request to method of ResourceContainer.
   * @param request REST request.
   * @return REST response from ResourceContainer.
   * @throws Exception any Exception.
   */
  public Response dispatch(Request request) throws Exception {
    String requestedURI = request.getResourceIdentifier().getURI();
    String methodName = request.getMethodName();
    String acceptedMimeTypes = (request.getHeaderParams().get("accept") != null)
        ? request.getHeaderParams().get("accept")
        : MimeTypes.ALL;
    MimeTypes requestedMimeTypes = new MimeTypes(acceptedMimeTypes);
    ResourceDescriptor resource = null;
    for (ResourceDescriptor r : resourceDescriptors_) {

      MimeTypes producedMimeTypes = new MimeTypes(r.getProducedMimeTypes());
      MultivaluedMetadata annotatedQueryParams = r.getQueryPattern();

      // Check is this ResourceContainer have appropriated parameters,
      // such URIPattern, HTTP method, QueryParamFilter and mimetype.
      if (r.getAcceptableMethod().equalsIgnoreCase(methodName)
          && r.getURIPattern().matches(requestedURI)
          && (compareMimeTypes(requestedMimeTypes.getMimeTypes(), producedMimeTypes.getMimeTypes()))
          && (isQueryParamsMatches(request.getQueryParams(), annotatedQueryParams))) {
        
        if (resource == null) {
          // if no one resource found yet, remember this resource.
          resource = r;
          continue;
        }
        if (r.getURIPattern().matches(resource.getURIPattern())) {
          // if URITemplate of candidate resource matches to remembered one, it can't be used.
          // Example: URI /test1/test2/a/b/test3/test4/ and two templates 
          // 1. /test1/test2/{id1}/{id2}/test3/test4/
          // 2. test1/test2/{id1}/test3/test4/
          // Template 1 will be first in the list and it is matched to requested URI,
          // and template 2 also matched to URI (and has less parameters!), but
          // template 2 matches to template 1, so it is more common. 
          // It will not be used! 
          continue;
        }
        if (r.getURIPattern().getTotalTokensLength() > resource.getURIPattern().getTotalTokensLength()) {
          resource = r;
        }
      }
    }
    if (resource != null) {
      ResourceIdentifier identifier = request.getResourceIdentifier();
      identifier.initParameters(resource.getURIPattern());
    
      // set initialized context to thread local
      contextHolder_.set(new Context(contextParams_, identifier));

      Annotation[] methodParametersAnnotations = resource.getMethodParameterAnnotations();
      Class<?>[] methodParameters = resource.getMethodParameters();
      Object[] params = new Object[methodParameters.length];
      // building array of parameters
      for (int i = 0; i < methodParametersAnnotations.length; i++) {
        if (methodParametersAnnotations[i] == null) {
          InputEntityTransformer transformer = (InputEntityTransformer) factory_
              .newTransformer(resource.getInputTransformerType());
          transformer.setType(methodParameters[i]);
          params[i] = transformer.readFrom(request.getEntityStream());
        } else {
          Constructor<?> constructor = methodParameters[i].getConstructor(String.class);
          String constructorParam = null;
          Annotation a = methodParametersAnnotations[i];
          if (a.annotationType().isAssignableFrom(URIParam.class)) {
            URIParam u = (URIParam) a;
            constructorParam = request.getResourceIdentifier().getParameters().get(u.value());
          } else if (a.annotationType().isAssignableFrom(HeaderParam.class)) {
            HeaderParam h = (HeaderParam) a;
            constructorParam = request.getHeaderParams().get(h.value());
          } else if (a.annotationType().isAssignableFrom(QueryParam.class)) {
            QueryParam q = (QueryParam) a;
            constructorParam = request.getQueryParams().get(q.value());
          } else if (a.annotationType().isAssignableFrom(ContextParam.class)) {
            ContextParam c = (ContextParam) a;
            constructorParam = contextHolder_.get().get(c.value());
          }
          if (methodParameters[i].isAssignableFrom(String.class)) {
            params[i] = constructorParam;
          } else {
            params[i] = (constructorParam != null) ? constructor.newInstance(constructorParam)
                : null;
          }
        }
      }
      Response response = (Response) resource.getServer().invoke(
          resource.getResourceContainer(), params);
      if (!response.isTransformerInitialized() && response.isEntityInitialized()) {
        response.setTransformer(getTransformer(resource, response.getTransformerParameters()));
      }
      else{
        OutputEntityTransformer transformer = response.getTransformer();
        if(transformer != null)
          transformer.addTransformerParameters(response.getTransformerParameters());
      }
      return response;
    }
    // if no one ResourceContainer found
    throw new NoSuchMethodException("No method found for " + methodName + " " +
        requestedURI + " " + acceptedMimeTypes);
  }

  /**
   * Get runtime context.
   * @return the runtimeContext.
   * @deprecated Instead of directly use <code>Context</code> should be user <code>@ContextParam</code>.
   */
  @Deprecated
  public Context getRuntimeContext() {
    return contextHolder_.get();
  }
  
  /**
   * Add new Context parameter. This method can be used to set some context
   * parameter. Any Resources can get access to ResourceDispatcher through
   * ExoContainer in runtime (Filter for example). Then this parameter can be
   * used in end point service.
   * @param key the key.
   * @param value the value.
   */
  public void addContextParameter(String key, String value) {
    contextHolder_.get().set(key, value);
  }

  /**
   * Get OutputEntitytransformer if it was not set before.
   */
  private OutputEntityTransformer getTransformer(ResourceDescriptor resource,
      Map<String, String> transformerParameters) throws InvalidResourceDescriptorException {
    try {
      OutputEntityTransformer transformer = (OutputEntityTransformer) factory_.newTransformer(
          resource.getOutputTransformerType());
      transformer.addTransformerParameters(transformerParameters);
      return transformer;
    } catch (Exception e) {
      throw new InvalidResourceDescriptorException(
          "Could not get EntityTransformer from Response"
          + " or annotation to ResourceDescriptor. Exception: " + e);
    }
  }

  /**
   * Compared query parameters.
   * @param fromRequest - query from request.
   * @param queryPattern - annotated query string.
   * @return true if query parameters matches to pattern false otherwise.
   */
  private boolean isQueryParamsMatches(MultivaluedMetadata fromRequest,
      MultivaluedMetadata queryPattern) {
    // if resource has not any QueryPattern it accept all request
    if (queryPattern.keys().size() == 0)
      return true;
    if (!fromRequest.keys().containsAll(queryPattern.keys()))
      return false;
    Set<String> keys = queryPattern.keys();
    Iterator<String> iterator = keys.iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      List<String> r = fromRequest.getList(key);
      List<String> p = queryPattern.getList(key);
      if (!r.containsAll(p))
        return false;
    }
    return true;
  }

  /**
   * Compared requested and produced mimetypes.
   * @param requested mimetypes from request.
   * @param produced mimetypes which ResourceContainer can produce.
   * @return true if mimetypes compatible false otherwise.
   */
  private boolean compareMimeTypes(String[] requested, String[] produced) {
    for (String r : requested) {
      for (String p : produced) {
        if ("*/*".equals(p))
          return true;
        if (p.equals(r))
          return true;
        String[] rsubtype = r.split("/");
        String[] psubtype = p.split("/");
        if (psubtype[0].equals(rsubtype[0])
            && "*".equals(psubtype[1])) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Keep runtime context and later it can be used for getting some runtime
   * information in ResourceContainer. For example full URL to the requested
   * resource.
   */
  public class Context {

    private final Map<String, String> params_;

    private Context(Map<String, String> params, ResourceIdentifier identifier) {
      params_ = params;
      params_.put(CONTEXT_PARAM_HOST, identifier.getHost());
      params_.put(CONTEXT_PARAM_BASE_URI, identifier.getBaseURI());
      params_.put(CONTEXT_PARAM_REL_URI, identifier.getURI());
      params_.put(CONTEXT_PARAM_ABSLOCATION,
          identifier.getBaseURI() + identifier.getURI());
    }

    /**
     * @return the host name.
     * @deprecated Instead this method use
     *             <code>@ContextParam(ResourceDispatcher.CONTEXT_PARAM_HOST)</code>.
     */
    @Deprecated
    public String getServerName() {
      return params_.get(CONTEXT_PARAM_HOST);
    }

    /**
     * @return the baseURI.
     * @deprecated Instead this method use
     *             <code>@ContextParam(ResourceDispatcher.CONTEXT_PARAM_BASE_URI)</code>.
     */
    @Deprecated
    public String getContextHref() {
      return params_.get(CONTEXT_PARAM_BASE_URI);
    }

    /**
     * Return absolute location to the requested resource.
     * @return the absolute location.
     */
    @Deprecated
    public String getAbsLocation() {
      return params_.get(CONTEXT_PARAM_BASE_URI) + params_.get(CONTEXT_PARAM_REL_URI);
    }

    /**
     * Add additional path to absolute location and return result.
     * @param additionalPath the additional path.
     * @return absolute location.
     */
    @Deprecated
    public String createAbsLocation(String additionalPath) {
      return getAbsLocation() + additionalPath;
    }

    private String get(String key) {
      return params_.get(key);
    }
    
    /**
     * Add new parameter. Can be used in runtime to set any context parameter
     * by if some resource can get access to ResourceDispatcher.
     * @param key the key.
     * @param value the value.
     */
    private void set(String key, String value) {
      params_.put(key, value);
    }
  }

}
