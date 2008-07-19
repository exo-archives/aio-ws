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

import org.apache.commons.codec.binary.Hex;

/**
 * @author Martin Algesten
 */
public class ParseState {

  private String phase;
  private int index = 0;
  private byte[] token;

  private LinkedList<String> messages = new LinkedList<String>();

  public ParseState(byte[] token) {
    this.token = token;
  }

  /**
   * Expects the current position to hold value <code>b</code>.
   * @param b The boolean to expect
   * @param moveIndex Tells if we are to move the index counter.
   * @param message Message to add to messages if the position does not hold the
   *            expected byte. Doesn't get added if null.
   * @return true if the current index holds the value given.
   */
  public boolean expect(int b, boolean moveIndex, String message) {
    boolean result = token[index] == (byte) b;
    if (!result && message != null)
      addMessage(message);
    if (moveIndex)
      index++;
    return result;
  }

  /**
   * Expects the current position to hold the given array of values <code>b</code>.
   * @param b the array of values expected.
   * @param moveIndex Tells if we are to move the index counter.
   * @param message Message to add to messages if the position does not hold the
   *            expected bytes. Doesn't get added if null.
   * @return true if there's a complete match between current position and the
   *         value given.
   */
  public boolean expect(int[] b, boolean moveIndex, String message) {
    boolean result = true;
    for (int i = 0; i < b.length; i++) {
      if (token[index + i] != (byte) b[i]) {
        result = false;
        break;
      }
    }
    if (!result && message != null)
      addMessage(message);
    if (moveIndex)
      index += b.length;
    return result;
  }

  /**
   * Calculates the der encoded length at the current position and moves the
   * index counter forward.
   */
  /*
   * 8 April 2008. Changed by <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
   * Was error as gives negative results of DerLenght.
   * Tested under Ubuntu 7.10 (jdk 1.5.0_11) as web server.
   */
  public int parseDerLength() {
    // fix
//    int b = token[index];
    int b = token[index] & 0xFF;
    
    if ((b & 0x80) == 0x00) {
      index++;
      return b;
    } else {
      int length = b & 0x7f;
      int result = 0;
      index++;
      for (int i = 0; i < length; i++) {
        result = result << 8;
        // fix
//        result = result | (int)token[index++];
        result = result | token[index++] & 0xFF;
      }
      return result;
    }
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public LinkedList<String> getMessages() {
    return messages;
  }

  public void setMessages(LinkedList<String> messages) {
    this.messages = messages;
  }

  public void addMessage(String message) {
    byte[] b = new byte[1];
    b[0] = token[index];
    char[] c = Hex.encodeHex(b);
    messages.add("[" + index + "," + phase + ",0x" + c[0] + c[1] + "] " + message);
  }

  public byte[] getToken() {
    return token;
  }

  public void setToken(byte[] token) {
    this.token = token;
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase;
  }

}
