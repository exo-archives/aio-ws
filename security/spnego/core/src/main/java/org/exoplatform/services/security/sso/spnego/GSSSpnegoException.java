/* Copyright 2006 Taglab Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package org.exoplatform.services.security.sso.spnego;

import org.ietf.jgss.GSSException;

/**
 * Exactly the same as {@link GSSException} except that it is a
 * {@link RuntimeException} via {@link SpnegoException}.
 * @author Martin Algesten
 */
public class GSSSpnegoException extends SpnegoException {

  private static final long serialVersionUID = -6914929877480458931L;

  public GSSSpnegoException(GSSException cause) {
    super(cause);
  }

  /**
   * @see org.ietf.jgss.GSSException#getMajor()
   */
  public int getMajor() {
    return ((GSSException) getCause()).getMajor();
  }

  /**
   * @see org.ietf.jgss.GSSException#getMajorString()
   */
  public String getMajorString() {
    return ((GSSException) getCause()).getMajorString();
  }

  /**
   * @see org.ietf.jgss.GSSException#getMinor()
   */
  public int getMinor() {
    return ((GSSException) getCause()).getMinor();
  }

  /**
   * @see org.ietf.jgss.GSSException#getMinorString()
   */
  public String getMinorString() {
    return ((GSSException) getCause()).getMinorString();
  }

}
