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

/**
 * @author Martin Algesten
 */
public class ApplicationConstructedObject extends AbstractMessagePart {

  private Oid oid;
  private NegTokenInit negTokenInit;

  public int getDerType() {
    return TYPE_APPLICATION_CONSTRUCTED_OBJECT;
  }

  public int[] toDer() {

    int[] oidDer = oid.toDer();
    int[] negTokenInitDer = negTokenInit.toDer();

    int[] tmp = new int[oidDer.length + negTokenInitDer.length];

    System.arraycopy(oidDer, 0, tmp, 0, oidDer.length);
    System.arraycopy(negTokenInitDer, 0, tmp, oidDer.length,
        negTokenInitDer.length);
    return wrap(TYPE_APPLICATION_CONSTRUCTED_OBJECT, tmp);
  }

  public void parse(ParseState state) {
    state.setPhase("APPLICATION_CONSTRUCTED_OBJECT");
    state.expect(TYPE_APPLICATION_CONSTRUCTED_OBJECT, true,
        "Expected type identifier");
    int length = state.parseDerLength();
    int actualLength = state.getToken().length - state.getIndex();
    if (length != actualLength)
      state.getMessages().add(
          "Expected length " + length + " mismatch against actual "
          + actualLength);
    oid = new Oid();
    oid.parse(state);
    negTokenInit = new NegTokenInit();
    negTokenInit.parse(state);
  }

  public NegTokenInit getNegTokenInit() {
    return negTokenInit;
  }

  public void setNegTokenInit(NegTokenInit negTokenInit) {
    this.negTokenInit = negTokenInit;
  }

  public Oid getOid() {
    return oid;
  }

  public void setOid(Oid oid) {
    this.oid = oid;
  }

}
