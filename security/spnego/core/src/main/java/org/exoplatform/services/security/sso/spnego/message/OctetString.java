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
public class OctetString extends AbstractMessagePart {

  private int[] data;

  private int sourceStart;
  private int sourceLength;

  public int getDerType() {
    return TYPE_OCTET_STRING;
  }

  public int[] toDer() {
    return wrap(TYPE_OCTET_STRING, data);
  }

  public void parse(ParseState state) {
    state.setPhase("OCTET_STRING");
    state.expect(TYPE_OCTET_STRING, true, "Expected octet string identifier");
    int length = state.parseDerLength();
    data = new int[length];
    sourceStart = state.getIndex();
    sourceLength = length;
    arraycopy(state.getToken(), state.getIndex(), data, 0, length);
    state.setIndex(state.getIndex() + length);
  }

  /**
   * @param data the data to set
   */
  public void setData(int[] data) {
    this.data = data;
  }

  /**
   * @return the data
   */
  public int[] getData() {
    return data;
  }

  /**
   * @return the sourceLength
   */
  public int getSourceLength() {
    return sourceLength;
  }

  /**
   * @return the sourceStart
   */
  public int getSourceStart() {
    return sourceStart;
  }

}
