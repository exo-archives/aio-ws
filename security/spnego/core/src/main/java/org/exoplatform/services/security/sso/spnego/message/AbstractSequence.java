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
public abstract class AbstractSequence<T extends MessagePart> extends
    AbstractMessagePart {

  public int getDerType() {
    return TYPE_SEQUENCE;
  }

  protected abstract LinkedList<T> getParts();

  protected abstract T createInstance(ParseState state);

  protected abstract void setParts(LinkedList<T> parts);

  public int[] toDer() {
    LinkedList<int[]> derParts = new LinkedList<int[]>();
    int totalLength = 0;
    for (MessagePart part : getParts()) {
      int[] derPart = part.toDer();
      totalLength += derPart.length;
      derParts.add(derPart);
    }

    int[] tmp = new int[totalLength];

    int index = 0;
    for (int[] derPart : derParts) {
      System.arraycopy(derPart, 0, tmp, index, derPart.length);
      index += derPart.length;
    }
    return wrap(TYPE_SEQUENCE, tmp);
  }

  public void parse(ParseState state) {

    state.expect(TYPE_SEQUENCE, true, "Expected type identifier");
    int length = state.parseDerLength();
    int startIndex = state.getIndex();
    LinkedList<T> parts = new LinkedList<T>();

    while (state.getIndex() < (startIndex + length)) {
      T part = createInstance(state);
      part.parse(state);
      parts.add(part);
    }

    if (state.getIndex() != startIndex + length)
      state.getMessages().add("Incorrect sequence length");

    setParts(parts);
  }

}
