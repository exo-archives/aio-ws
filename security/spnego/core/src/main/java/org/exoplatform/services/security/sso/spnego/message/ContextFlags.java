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
public class ContextFlags extends AbstractMessagePart {

  public final static int BIT_delegFlag = 0;
  public final static int BIT_mutualFlag = 1;
  public final static int BIT_replayFlag = 2;
  public final static int BIT_sequenceFlag = 3;
  public final static int BIT_anonFlag = 4;
  public final static int BIT_confFlag = 5;
  public final static int BIT_integFlag = 6;

  private int flags = 0;

  public int getDerType() {
    return TYPE_BIT_STRING;
  }

  public int[] toDer() {
    int[] tmp = new int[3];
    tmp[0] = TYPE_BIT_STRING;
    tmp[1] = 1;
    tmp[2] = flags;

    return tmp;
  }

  public void parse(ParseState state) {
    state.setPhase("CONTEXT_FLAGS");
    state.expect(TYPE_BIT_STRING, true, "Expected bit string identifier");
    state.expect(1, true, "Expected length 1");
    flags = state.getToken()[state.getIndex()];
  }

  public boolean isSet(int position) {
    return (flags & 2 ^ position) != 0;
  }

  public void set(int position, boolean set) {
    flags = set ? flags | 2 ^ position : flags & (~(2 ^ position));
  }

  public boolean isDelegFlag() {
    return isSet(BIT_delegFlag);
  }

  public void setDelegFlag(boolean delegFlag) {
    set(BIT_delegFlag, delegFlag);
  }

  public boolean isMutualFlag() {
    return isSet(BIT_mutualFlag);
  }

  public void setMutualFlag(boolean mutualFlag) {
    set(BIT_mutualFlag, mutualFlag);
  }

  public boolean isReplayFlag() {
    return isSet(BIT_replayFlag);
  }

  public void setReplayFlag(boolean replayFlag) {
    set(BIT_replayFlag, replayFlag);
  }

  public boolean isSequenceFlag() {
    return isSet(BIT_sequenceFlag);
  }

  public void setSequenceFlag(boolean sequenceFlag) {
    set(BIT_sequenceFlag, sequenceFlag);
  }

  public boolean isAnonFlag() {
    return isSet(BIT_anonFlag);
  }

  public void setAnonFlag(boolean anonFlag) {
    set(BIT_anonFlag, anonFlag);
  }

  public boolean isConfigFlag() {
    return isSet(BIT_confFlag);
  }

  public void setConfFlag(boolean confFlag) {
    set(BIT_confFlag, confFlag);
  }

  public boolean isIntegFlag() {
    return isSet(BIT_integFlag);
  }

  public void setInteFlag(boolean integFlag) {
    set(BIT_integFlag, integFlag);
  }

  /**
   * @return the flags
   */
  public int getFlags() {
    return flags;
  }

  /**
   * @param flags the flags to set
   */
  public void setFlags(int flags) {
    this.flags = flags;
  }

}
