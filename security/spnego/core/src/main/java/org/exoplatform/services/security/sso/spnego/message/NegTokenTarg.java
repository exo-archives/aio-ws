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
public class NegTokenTarg extends AbstractSequence<NegTokenSequencePart> {

  public final static int INDEX_NEG_RESULT = 0;
  public final static int INDEX_SUPPORTED_MECH = 1;
  public final static int INDEX_RESPONSE_TOKEN = 2;
  public final static int INDEX_MECH_LIST_MIC = 3;
  
  private NegResult negResult;
  private Oid supportedMech;
  private OctetString responseToken;
  private OctetString mechListMIC;
 
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence#getParts()
   */
  @Override
  protected LinkedList<NegTokenSequencePart> getParts() {
    LinkedList<NegTokenSequencePart> tmp = new LinkedList<NegTokenSequencePart>();
    if ( negResult != null ) 
      tmp.add( new NegTokenSequencePart( INDEX_NEG_RESULT, negResult ) );
    if ( supportedMech != null ) 
      tmp.add( new NegTokenSequencePart( INDEX_SUPPORTED_MECH, supportedMech ) );
    if ( responseToken != null )
      tmp.add( new NegTokenSequencePart( INDEX_RESPONSE_TOKEN, responseToken ) );
    if ( mechListMIC != null )
      tmp.add( new NegTokenSequencePart( INDEX_MECH_LIST_MIC, mechListMIC ) );
    return tmp;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence
   * #createInstance(org.exoplatform.services.organization.auth.sso.spnego.message.ParseState)
   */
  @Override
  protected NegTokenSequencePart createInstance(ParseState state) {
    NegTokenSequencePart part = new NegTokenSequencePart();
    if ( state.expect(0xa0, false, null) ) {
      negResult = new NegResult();
      part.setMessagePart(negResult);
    } else if ( state.expect(0xa1, false, null) ) {
      supportedMech = new Oid();
      part.setMessagePart(supportedMech);
    } else if ( state.expect(0xa2, false, null) ) {
      responseToken = new OctetString();
      part.setMessagePart(responseToken);
    } else if ( state.expect(0xa3, false, null) ) {
      mechListMIC = new OctetString();
      part.setMessagePart(mechListMIC);
    }
    return part;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message
   * .AbstractSequence#setParts(java.util.LinkedList)
   */
  @Override
  protected void setParts(LinkedList<NegTokenSequencePart> parts) {
    // TODO Auto-generated method stub
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence#getDerType()
   */
  @Override
  public int getDerType() {
    return TYPE_NEG_TOKEN_TARG;
  }
 
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence#toDer()
   */
  @Override
  public int[] toDer() {
    int[] tmp = super.toDer();
    return wrap( TYPE_NEG_TOKEN_TARG, tmp );
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message
   * .AbstractSequence#parse(org.exoplatform.services.organization.auth.sso.spnego.message.ParseState)
   */
  @Override
  public void parse(ParseState state) {
    state.setPhase("NEG_TOKEN_TARG");
    state.expect(TYPE_NEG_TOKEN_TARG, true, "Expected NegTokenTarg identifier");
    state.parseDerLength(); // I can't really verify it.
    super.parse(state);
  }

  public OctetString getMechListMIC() {
    return mechListMIC;
  }

  public void setMechListMIC(OctetString mechListMIC) {
    this.mechListMIC = mechListMIC;
  }

  public NegResult getNegResult() {
    return negResult;
  }

  public void setNegResult(NegResult negResult) {
    this.negResult = negResult;
  }

  public OctetString getResponseToken() {
    return responseToken;
  }

  public void setResponseToken(OctetString responseToken) {
    this.responseToken = responseToken;
  }

  public Oid getSupportedMech() {
    return supportedMech;
  }

  public void setSupportedMech(Oid supportedMech) {
    this.supportedMech = supportedMech;
  }

}
