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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import org.picocontainer.Startable;
import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.container.InvalidResourceDescriptorException;
import org.exoplatform.services.rest.container.ResourceDescriptor;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.container.ResourceContainerResolvingStrategy;

/**
 * Created by The eXo Platform SAS.<br/> For binding and unbinding
 * ResourceContainers.<br/>
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class ResourceBinder implements Startable {

  private List<ResourceDescriptor> resourceDescriptors_;
  private List<ResourceContainerResolvingStrategy> bindStrategies_;
  private ExoContainerContext containerContext_;
  private ExoContainer container_;
  private static final Log log = ExoLogger.getLogger("ws.rest.core.ResourceBinder");

  private static final ResourceDescriptorComparator COMPARATOR = new ResourceDescriptorComparator();
  
  /** 
   * Comparator for sorting List of ResourceDescriptors. 
   */
  private static class ResourceDescriptorComparator implements Comparator<ResourceDescriptor> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(ResourceDescriptor resourceDescriptor1,
        ResourceDescriptor resourceDescriptor2) {
      int u1 = resourceDescriptor1.getURIPattern().getParamNames().size();
      int u2 = resourceDescriptor2.getURIPattern().getParamNames().size();
      if (u1 < u2)
        return 1;
      if (u1 > u2)
        return -1;
      // For greed behavior QueryTemplate mechanism.
      int q1 = resourceDescriptor1.getQueryPattern().getAll().size();
      int q2 = resourceDescriptor2.getQueryPattern().getAll().size();
      if (q1 < q2)
        return 1;
      if (q1 > q2)
        return -1;
      //
      /* Few query parameters can have the same name, for us this means 
       * we can get parameters as list: key=value1, value2,... .
       * We don't care about this when sorting, we are waiting for next:
       * key=value. 
       * 
       * EXPLANATION.
       * 1. param1=1, 2 and param2=3
       * 2. param1=1    and param2=2, 3  
       * What resource must be first ? 
       * So if request has parameters: param1=1, 2 and param2=2, 3
       * we can't guaranty which resource will be first in the list
       * and can't guaranty which resource will be called.  
       */
      
      return 0;
    }
    
  }
  
  /**
   * Constructor sets the resolving strategy. Currently
   * HTTPAnnotatedContainerResolvingStrategy (annotations used for description
   * ResourceContainers)
   * @param params class name for ResourceContainerResolvingStrategy
   * @param containerContext ExoContainer context
   * @throws Exception any exception
   */
  public ResourceBinder(InitParams params, ExoContainerContext containerContext)
      throws Exception {

    this.containerContext_ = containerContext;
    this.resourceDescriptors_ = new ArrayList<ResourceDescriptor>();
    this.bindStrategies_ = new ArrayList<ResourceContainerResolvingStrategy>();

    Iterator<ValueParam> i = params.getValueParamIterator();
    while (i.hasNext()) {
      ValueParam v = i.next();
      ResourceContainerResolvingStrategy rs = (ResourceContainerResolvingStrategy) Class
          .forName(v.getValue()).newInstance();
      bindStrategies_.add(rs);
    }
  }

  /**
   * Bind ResourceContainer resourceCont if validation for this container is ok.
   * After binding new component(s) to the ResourceDescriptor list it must be
   * sorted by number of parameter in URITemplate. The resources which have more
   * parameters must be at the begin of list, the resources which have less
   * parameter must be at the and of it. <br/> See example: <br/> <code>
   * @HTTPMethod("GET")
   * @URITemplate("/level1/{id1}/"
   * public void method1() {
   * ....
   * }
   * @HTTPMethod("GET")
   * @URITemplate("/level1/{id1}/{id2}/"
   * public void method1() {
   * ....
   * }
   * For URI "/level1/test1/test2/" both URITemplate are valid.
   * First method: id1 = "test1/test2"
   * Second method: id1 = "test1", id2 = "test2"
   * </code>
   * @param resourceCont the Resource Container
   * @throws InvalidResourceDescriptorException if validation failed.
   */
  final public void bind(final ResourceContainer resourceCont)
      throws InvalidResourceDescriptorException {
    for (ResourceContainerResolvingStrategy strategy : bindStrategies_) {
      List<ResourceDescriptor> resList = strategy.resolve(resourceCont);
      validate(resList);
      resourceDescriptors_.addAll(resList);
      log.info("Bind new ResourceContainer: " + resourceCont);
      Collections.sort(resourceDescriptors_, COMPARATOR);
    }
//    int i = 1;
//    for (ResourceDescriptor r : resourceDescriptors_) {
//      log.info(i++ + ". " + r.getURIPattern().getString() + " : "
//          + r.getQueryPattern().getAll());
//    }
  }

  /**
   * Unbind single ResourceContainer.
   * @param resourceCont the ResourceContainer which should be unbinded.
   */
  final public void unbind(final ResourceContainer resourceCont) {
    int i = 0;
    List<ResourceDescriptor> temp = new ArrayList<ResourceDescriptor>(resourceDescriptors_);
    for (ResourceDescriptor resource : temp) {
      if (resource.getResourceContainer().equals(resourceCont))
        resourceDescriptors_.remove(i);
      else
        i++;
    }
  }

  /**
   * Clear the list of ResourceContainer description.
   */
  final public void clear() {
    this.resourceDescriptors_.clear();
  }

  /**
   * @return all resources descriptors.
   */
  final public List<ResourceDescriptor> getAllDescriptors() {
    return this.resourceDescriptors_;
  }

  /**
   * Validation for ResourceContainer. Not allowed have two ResourceContainers
   * with the same URIPatterns. And ALL ResourceContainers must have the
   * required annotation .
   * @param newDescriptors descriptors of ResourceContainer for binding.
   * @throws InvalidResourceDescriptorException if ResourceContainer is not valid.
   */
  private void validate(final List<ResourceDescriptor> newDescriptors)
      throws InvalidResourceDescriptorException {

    for (ResourceDescriptor newDescriptor : newDescriptors) {
      URIPattern newPattern = newDescriptor.getURIPattern();
      String newHTTPMethod = newDescriptor.getAcceptableMethod();
      MultivaluedMetadata newQueryPattern = newDescriptor.getQueryPattern();

      for (ResourceDescriptor storedDescriptor : resourceDescriptors_) {
        URIPattern srotedPattern = storedDescriptor.getURIPattern();
        String storedHTTPMethod = storedDescriptor.getAcceptableMethod();
        MultivaluedMetadata storedQueryPattern = storedDescriptor
            .getQueryPattern();
        // check URI pattern
        if (srotedPattern.matches(newPattern) ||
            newPattern.matches(srotedPattern)) {
          // check HTTP method.
          if (storedHTTPMethod.equalsIgnoreCase(newHTTPMethod) &&
              isQueryPatternMatches(storedQueryPattern, newQueryPattern)) {
            throw new InvalidResourceDescriptorException(
                "The ResourceContainer " +
                "\nmethod : " + newDescriptor.getServer().toGenericString() +
                "\nURITemplate : " + newDescriptor.getURIPattern().getString() +
                "\nHTTPMethod : " + newDescriptor.getAcceptableMethod() +
                "\nQueryTemplate : " + newDescriptor.getServer().getAnnotation(QueryTemplate.class) +
                "\ncan not be defined because of existed ResourceContainer " +
                "\nmethod : " + storedDescriptor.getServer().toGenericString() +
                "\nURITemplate : " + storedDescriptor.getURIPattern().getString() +
                "\nHTTPMethod : " + storedDescriptor.getAcceptableMethod() +
                "\nQueryTemplate : " + storedDescriptor.getServer().getAnnotation(QueryTemplate.class));
          }
        }
      }

      Method method = newDescriptor.getServer();
      
      Class<? extends ResourceContainer> newContainer =
        newDescriptor.getResourceContainer().getClass();

      Class<?> [] requestedParams = newDescriptor.getMethodParameters();
      
      Annotation[] paramAnno = newDescriptor.getMethodParameterAnnotations(); 
      
      boolean hasRequestEntity = false;
      // check method parameters
      for (int i = 0; i < paramAnno.length; i++) {
        if (paramAnno[i] == null) {

          /* We have at least one not annotated object.
           * Check is input transformer specified.  
           */
          if (method.getAnnotation(InputTransformer.class) == null &&
              newContainer.getAnnotation(InputTransformer.class) == null) {
            throw new InvalidResourceDescriptorException(
                "One not annotated object found, but transformer in method : \n" +
                    method.toGenericString() +
                    "\nInputTransformer annotation is not specified. This is not allowed!");
            
          }
          
          if (!hasRequestEntity) {
            hasRequestEntity = true;
          } else {
            // We already found one not annotated object. This is entity.
            throw new InvalidResourceDescriptorException(
                "Only one not annotated object with must represent HTTP Request.\n" +
                    "In method : " + method.toGenericString());
          }
          
        } else {
          /* If parameter annotated, then it SHOULD be String or MUST has
           * constructor with String as parameter or it MUST be a
           * org.exoplatform.services.rest.Cookie object.
           * The other types is NOT allowed.
           */
          if (AnnotationUtils.Anno.COOKIE_PARAM == AnnotationUtils.getType(
              paramAnno[i].annotationType())) {
            if (requestedParams[i].isAssignableFrom(Cookie.class))
              continue;
          }
          //   
          try {
            requestedParams[i].getConstructor(String.class);
          } catch(NoSuchMethodException e) {
            throw new InvalidResourceDescriptorException(
                "All annotated parameters must have constructor with String or be the String.\n" +
                    "In method : " + method.toGenericString());
          }
        }
      }
    }
  }

  /**
   * If one of Resource has not any QueryPatter this resource should accept all
   * request. And binding other components with the same URITemplate, HTTPMethod
   * and some QueryTemplate should be filed. Not allowed to have two resources
   * with the same URI, HTTP method and QueryPattern. If first set of query
   * parameters name does not consist <b>all</b> query parameters name from
   * second set <b>and</b> second one does consist <b>all</b> query parameters
   * name from the first one then return false. <br/> Otherwise query parameters
   * value must be checked. <br/> This example explain that: <br/> <code>
   * First query string :  param1=param1&param2=param2.
   * Second query string : param1=param1&param2=param2&param3=param3.
   * First set of query parameters name  : param1, param2.
   * Second set of query parameters name : param1, param2, param3.
   * First set does not consist all keys from second one, but second one consist all of keys
   * from first one. Then value of query parameters must be checked. 
   * Another situation:
   * First query string :  param1=param1&param2=param2.
   * Second query string : param1=param1&param3=param3.
   * First set of query parameters name  : param1, param2.
   * Second set of query parameters name : param1, param3.
   * Sets of query parameters name are different then stop checking.
   * </code>
   * @param storedQueryPattern the binded QueryParameters (QueryTemplate in this
   *            case).
   * @param newQueryPattern the candidate QueryParameters.
   * @return true if QeuryPattern matches, false otherwise.
   */
//  private boolean isQueryPatternMatches(MultivaluedMetadata storedQueryPattern,
//      MultivaluedMetadata newQueryPattern) {
//    Set<String> storedKeys = storedQueryPattern.keys();
//    Set<String> newKeys = newQueryPattern.keys();
//    if (newKeys.size() == 0 || storedKeys.size() == 0) {
//      return true;
//    }
//    if (!storedKeys.containsAll(newKeys) && !newKeys.containsAll(storedKeys)) {
//      return false;
//    }
//    /* Get iterator from stored keys, it is already binded,
//     * so it will be user like pattern for candidate for binding.
//     */
//    Iterator<String> iterator = storedKeys.iterator();
//    while (iterator.hasNext()) {
//      String key = iterator.next();
//      // get list of query parameters value
//      List<String> s = storedQueryPattern.getList(key);
//      List<String> n = newQueryPattern.getList(key);
//      /* If candidate has no one of query parameter continue check other
//       * parameter. It is not enough to stop checking.
//       */
//      if (n == null) {
//        continue;
//      }
//      if (!s.containsAll(n) || !n.containsAll(s)) {
//        return false;
//      }
//    }
//    return true;
//  } 

  /*
   * For greed behavior QueryTemplate mechanism.
   */
  /**
   * If two Resources has the same URIPattern then they must be sorted by
   * QueryPatter, Resource which has more query parameters in the pattern should 
   * be firsts. And binding other components with the same URITemplate, HTTPMethod
   * and the same QueryTemplate should be filed. Not allowed to have two resources
   * with the same URI, HTTP method and QueryPattern. If first set of query
   * parameters name does not consist <b>all</b> query parameters name from
   * second set <b>or</b> second one does consist <b>all</b> query parameters
   * name from the first one then return false. <br/> Otherwise query parameters
   * value must be checked. <br/> This example explain that: <br/> <code>
   * First query string :  param1=param1&param2=param2.
   * Second query string : param1=param1&param2=param2&param3=param3.
   * First set of query parameters name  : param1, param2.
   * Second set of query parameters name : param1, param2, param3.
   * First set does not consist all keys from second one.
   * Then value of query parameters must not be checked.
   * If Resources has the same URITemplate second Resource in example will be
   * higher in the list. ResourceDispatcher will get it first. 
   * </code>
   * @param storedQueryPattern the binded QueryParameters (QueryTemplate in this
   *            case).
   * @param newQueryPattern the candidate QueryParameters.
   * @return true if QeuryPattern matches, false otherwise.
   */
  private boolean isQueryPatternMatches(MultivaluedMetadata storedQueryPattern,
      MultivaluedMetadata newQueryPattern) {
    Set<String> storedKeys = storedQueryPattern.keys();
    Set<String> newKeys = newQueryPattern.keys();
    if (newKeys.size() != storedKeys.size()) {
      return false;
    }
    if (!storedKeys.containsAll(newKeys) || !newKeys.containsAll(storedKeys)) {
      return false;
    }
    /* Get iterator from stored keys, it is already binded,
     * so it will be user like pattern for candidate for binding.
     */
    Iterator<String> iterator = storedKeys.iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      // get list of query parameters value
      List<String> s = storedQueryPattern.getList(key);
      List<String> n = newQueryPattern.getList(key);
      /* If candidate has no one of query parameter
       * then stop checking. Template are not matched.
       */
      if (n == null) {
        return false;
      }
      if (!s.containsAll(n) || !n.containsAll(s)) {
        return false;
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.picocontainer.Startable#start()
   */
  public void start() {
    container_ = containerContext_.getContainer();
    List<ResourceContainer> list = container_
        .getComponentInstancesOfType(ResourceContainer.class);
    for (ResourceContainer c : list) {
      try {
        bind(c);
      } catch (InvalidResourceDescriptorException irde) {
        log.error("Can't add ResourceContainer Component: " +
            c.getClass().getName() + ".\nException : " + irde);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.picocontainer.Startable#stop()
   */
  public void stop() {
    clear();
  }
}
