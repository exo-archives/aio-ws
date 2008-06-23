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
public class NegResult extends AbstractMessagePart {

  public final static int ACCEPT_COMPLETED = 1;
  public final static int ACCEPT_INCOMPLETE = 2;
  public final static int REJECTED = 3;

  private int result = 0;

  public int getDerType() {
    return TYPE_ENUMERATED;
  }

  public NegResult() {
  }

  public NegResult(int result) {
    this.result = result;
  }

  public int[] toDer() {
    int[] tmp = new int[3];
    tmp[0] = TYPE_ENUMERATED;
    tmp[1] = 1;
    tmp[2] = result;
    return tmp;
  }

  public void parse(ParseState state) {
    state.setPhase("NEG_RESULT");
    state.expect(TYPE_ENUMERATED, true, "Expected enumerated identifier");
    int length = state.parseDerLength();
    if (length != 1)
      state.addMessage("Expected length 1 but was " + length);
    this.result = state.getToken()[state.getIndex()];
  }

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

}
