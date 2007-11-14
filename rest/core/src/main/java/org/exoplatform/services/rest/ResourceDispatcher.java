/**
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 */
package org.exoplatform.services.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.logging.Log;
//import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceDescriptor;
import org.exoplatform.services.rest.data.MimeTypes;
import org.exoplatform.services.rest.transformer.InputEntityTransformer;
import org.exoplatform.services.rest.transformer.OutputEntityTransformer;
import org.exoplatform.services.rest.transformer.EntityTransformerFactory;

/**
 * Created by The eXo Platform SARL.<br/> ResourceDispatcher finds
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

//  private static final Log LOGGER = ExoLogger.getLogger("ResourceDispatcher");

  /**
   * Constructor gets all binded ResourceContainers from ResourceBinder.
   * @param containerContext ExoContainerContext
   * @throws Exception any Exception
   */
  public ResourceDispatcher(InitParams params, ResourceBinder binder)
      throws Exception {
    if (params != null) {
      PropertiesParam contextParam = params.getPropertiesParam("contex-params");
      if (contextParam != null) {
        Iterator<Property> iterator = contextParam.getPropertyIterator();
        while (iterator.hasNext()) {
          Property property = iterator.next();
          contextParams_.put(property.getName(), property.getValue());
        }
      }
    }
    this.resourceDescriptors_ = binder.getAllDescriptors();
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
        if (r.getURIPattern().getTokens().length < resource.getURIPattern().getTokens().length) {
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
          EntityTransformerFactory factory = new EntityTransformerFactory(
              resource.getInputTransformerType());
          InputEntityTransformer transformer = (InputEntityTransformer) factory.newTransformer();
          transformer.setType(methodParameters[i]);
          params[i] = transformer.readFrom(request.getEntityStream());
        } else {
          Constructor<?> constructor = methodParameters[i].getConstructor(String.class);
          String constructorParam = null;
          Annotation a = methodParametersAnnotations[i];
          if (a.annotationType().isAssignableFrom(URIParam.class)) {
            URIParam u = (URIParam) a;
            constructorParam = request.getResourceIdentifier().getParameters().get(u.value());
//            params[i] = request.getResourceIdentifier().getParameters().get(u.value());
          } else if (a.annotationType().isAssignableFrom(HeaderParam.class)) {
            HeaderParam h = (HeaderParam) a;
            constructorParam = request.getHeaderParams().get(h.value());
//            params[i] = request.getHeaderParams().get(h.value());
          } else if (a.annotationType().isAssignableFrom(QueryParam.class)) {
            QueryParam q = (QueryParam) a;
            constructorParam = request.getQueryParams().get(q.value());
//            params[i] = request.getQueryParams().get(q.value());
          } else if (a.annotationType().isAssignableFrom(ContextParam.class)) {
            ContextParam c = (ContextParam) a;
            constructorParam = contextHolder_.get().get(c.value());
//            params[i] = contextHolder_.get().get(c.value());
          }
          if (methodParameters[i].isAssignableFrom(String.class)) {
            params[i] = constructorParam;
          } else {
            params[i] = (constructorParam != null) ? constructor.newInstance(constructorParam)
                : null;
          }
        }
      }
      Response resp = (Response) resource.getServer().invoke(
          resource.getResourceContainer(), params);
      if (!resp.isTransformerInitialized() && resp.isEntityInitialized()) {
        resp.setTransformer(getTransformer(resource));
      }
      return resp;
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

  private OutputEntityTransformer getTransformer(ResourceDescriptor resource)
      throws InvalidResourceDescriptorException {
    try {
      EntityTransformerFactory factory = new EntityTransformerFactory(resource
          .getOutputTransformerType());
      return (OutputEntityTransformer) factory.newTransformer();
    } catch (Exception e) {
      throw new InvalidResourceDescriptorException(
          "Could not get EntityTransformer from Response" +
              " or annotation to ResourceDescriptor. Exception: " + e);
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
    if (queryPattern.keys().size() == 0) {
      return true;
    }
    if (!fromRequest.keys().containsAll(queryPattern.keys())) {
      return false;
    }
    Set<String> keys = queryPattern.keys();
    Iterator<String> iterator = keys.iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      List<String> r = fromRequest.getList(key);
      List<String> p = queryPattern.getList(key);
      if (!r.containsAll(p)) {
        return false;
      }
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
        if ("*/*".equals(p)) {
          return true;
        }
        if (p.equals(r)) {
          return true;
        }
        String[] rsubtype = r.split("/");
        String[] psubtype = p.split("/");
        if (psubtype[0].equals(rsubtype[0]) && "*".equals(psubtype[1])) {
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
  }

}
