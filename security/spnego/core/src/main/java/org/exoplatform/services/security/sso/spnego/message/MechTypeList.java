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
package org.exoplatform.services.security.sso.spnego.message;

import java.util.LinkedList;

/**
 * @author Martin Algesten
 */
public class MechTypeList extends AbstractSequence<Oid> {

  private LinkedList<Oid> mechs = new LinkedList<Oid>();
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence#getParts()
   */
  @Override
  protected LinkedList<Oid> getParts() {
    return mechs;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence
   * #createInstance(org.exoplatform.services.organization.auth.sso.spnego.message.ParseState)
   */
  @Override
  protected Oid createInstance( ParseState state ) {
    return new Oid();
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message
   * .AbstractSequence#setParts(java.util.LinkedList)
   */
  @Override
  protected void setParts(LinkedList<Oid> parts) {
    this.mechs = parts;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message
   * .AbstractSequence#parse(org.exoplatform.services.organization.auth.sso.spnego.message.ParseState)
   */
  @Override
  public void parse(ParseState state) {
    state.setPhase("MECH_TYPE_LIST");
    super.parse(state);
  }
  
  /**
   * @return the mechs
   */
  public LinkedList<Oid> getMechs() {
    return mechs;
  }
  
  /**
   * @param mechs the mechs to set
   */
  public void setMechs(LinkedList<Oid> mechs) {
    this.mechs = mechs;
  }
  
}
