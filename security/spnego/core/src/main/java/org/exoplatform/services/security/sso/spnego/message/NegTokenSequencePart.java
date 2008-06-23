/*
 * Copyright 2006 Taglab Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License
 */
package org.exoplatform.services.security.sso.spnego.message;

/**
 * @author Martin Algesten
 */
public class NegTokenSequencePart extends AbstractMessagePart {

  private int seqNo = 0; // 0, 1, 2, 3, 4...

  private MessagePart messagePart;

  public NegTokenSequencePart() {
  }

  public NegTokenSequencePart(int seqNo, MessagePart messagePart) {
    this.seqNo = seqNo;
    this.messagePart = messagePart;
  }

  // 0xa0, 0xa1, 0xa2, 0xa3...
  public int getDerType() {
    return 0xa0 + seqNo;
  }

  public int[] toDer() {
    return wrap(getDerType(), messagePart.toDer());
  }

  public void parse(ParseState state) {
    seqNo = 0xff & state.getToken()[state.getIndex()];
    state.setIndex(state.getIndex() + 1);
    if (seqNo < 0xa0)
      state.addMessage("Expected sequence number > 0xa0: " + seqNo);
    seqNo = seqNo - 0xa0;
    state.parseDerLength(); // Can't verify.
    messagePart.parse(state);
  }

  public void setSeqNo(int seqNo) {
    this.seqNo = seqNo;
  }

  public int getSeqNo() {
    return seqNo;
  }

  public MessagePart getMessagePart() {
    return messagePart;
  }

  public void setMessagePart(MessagePart messagePart) {
    this.messagePart = messagePart;
  }

}
