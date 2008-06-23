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
public class NegTokenInit extends AbstractSequence<NegTokenSequencePart> {

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence#getDerType()
   */
  @Override
  public int getDerType() {
    return TYPE_NEG_TOKEN_INIT;
  }

  public final static int INDEX_MECH_TYPES = 0;
  public final static int INDEX_REQ_FLAGS = 1;
  public final static int INDEX_MECH_TOKEN = 2;
  public final static int INDEX_MECH_LIST_MIC = 3;

  private MechTypeList mechTypes;
  private ContextFlags contextFlags;
  private OctetString mechToken;
  private OctetString mechListMIC;

  @Override
  protected LinkedList<NegTokenSequencePart> getParts() {

    LinkedList<NegTokenSequencePart> tmp = new LinkedList<NegTokenSequencePart>();

    if (mechTypes != null)
      tmp.add(new NegTokenSequencePart(INDEX_MECH_TYPES, mechTypes));

    if (contextFlags != null)
      tmp.add(new NegTokenSequencePart(INDEX_REQ_FLAGS, contextFlags));

    if (mechToken != null)
      tmp.add(new NegTokenSequencePart(INDEX_MECH_TOKEN, mechToken));

    if (mechListMIC != null)
      tmp.add(new NegTokenSequencePart(INDEX_MECH_LIST_MIC, mechListMIC));

    return tmp;

  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence#toDer()
   */
  @Override
  public int[] toDer() {
    int tmp[] = super.toDer();
    return wrap(TYPE_NEG_TOKEN_INIT, tmp);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence
   * #parse(org.exoplatform.services.organization.auth.sso.spnego.message.ParseState)
   */
  @Override
  public void parse(ParseState state) {
    state.setPhase("NEG_TOKEN_INIT");
    state.expect(TYPE_NEG_TOKEN_INIT, true, "Expected NegTokenInit identifier");
    state.parseDerLength(); // I can't really verify it.
    super.parse(state);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.spnego.message.AbstractSequence
   * #createInstance(org.exoplatform.services.organization.auth.sso.spnego.message.ParseState)
   */
  @Override
  protected NegTokenSequencePart createInstance(ParseState state) {
    NegTokenSequencePart part = new NegTokenSequencePart();
    if (state.expect(0xa0, false, null)) {
      mechTypes = new MechTypeList();
      part.setMessagePart(mechTypes);
    } else if (state.expect(0xa1, false, null)) {
      contextFlags = new ContextFlags();
      part.setMessagePart(contextFlags);
    } else if (state.expect(0xa2, false, null)) {
      mechToken = new OctetString();
      part.setMessagePart(mechToken);
    } else if (state.expect(0xa3, false, null)) {
      mechListMIC = new OctetString();
      part.setMessagePart(mechListMIC);
    } else {
      state.addMessage("Unexpected message part sequence no: " +
          state.getToken()[state.getIndex()]);
    }
    return part;
  }

  @Override
  protected void setParts(LinkedList<NegTokenSequencePart> parts) {
    for (NegTokenSequencePart part : parts) {
      switch (part.getSeqNo()) {
        case INDEX_MECH_TYPES:
          mechTypes = (MechTypeList) part.getMessagePart();
          break;
        case INDEX_REQ_FLAGS:
          contextFlags = (ContextFlags) part.getMessagePart();
          break;
        case INDEX_MECH_TOKEN:
          mechToken = (OctetString) part.getMessagePart();
          break;
        case INDEX_MECH_LIST_MIC:
          mechListMIC = (OctetString) part.getMessagePart();
          break;
        default:
          throw new RuntimeException("Unexpected sequence number: " +
              part.getSeqNo());
      }
    }
  }

  public ContextFlags getContextFlags() {
    return contextFlags;
  }

  public void setContextFlags(ContextFlags contextFlags) {
    this.contextFlags = contextFlags;
  }

  public OctetString getMechListMIC() {
    return mechListMIC;
  }

  public void setMechListMIC(OctetString mechListMIC) {
    this.mechListMIC = mechListMIC;
  }

  public OctetString getMechToken() {
    return mechToken;
  }

  public void setMechToken(OctetString mechToken) {
    this.mechToken = mechToken;
  }

  public MechTypeList getMechTypes() {
    return mechTypes;
  }

  public void setMechTypes(MechTypeList mechTypes) {
    this.mechTypes = mechTypes;
  }

}
