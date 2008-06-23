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
public abstract class AbstractMessagePart implements MessagePart {

  /**
   * Calculates the ASN.1 DER represenation of the length given.
   * @return if length &lt; 0x80, the length straight off. If more than 0x80,
   *         the first byte is 0x80 | [number of bytes required to represent
   *         length] and then bytes high byte first.
   */
  protected int[] calculateDerLength(int length) {

    int[] tmp = new int[16]; // arbitrary buffer length;
    int index = tmp.length; // start from back.

    while (length != 0) {
      tmp[--index] = length & 0xff;
      length = length >> 8;
    }

    tmp[--index] = 0x80 | (tmp.length - index);
    int[] result = new int[tmp.length - index];
    System.arraycopy(tmp, index, result, 0, result.length);

    return result;

  }

  /**
   * Creates a der part of a message with the type first, then der length and
   * then the actual data.
   * @param derType the byte identifier of the data type.
   * @param wrappedData the data.
   * @return [derType][derLength][data]
   */
  protected int[] wrap(int derType, int[] wrappedData) {

    int[] derLength = calculateDerLength(wrappedData.length);
    int[] tmp = new int[1 + derLength.length + wrappedData.length];
    tmp[0] = derType;
    System.arraycopy(derLength, 0, tmp, 1, derLength.length);
    System.arraycopy(wrappedData, 0, tmp, derLength.length + 1,
        wrappedData.length);
    return tmp;
  }

  public static void arraycopy(byte[] src, int srcPos, int[] dest, int destPos,
      int length) {
    for (int i = 0; i < length; i++) {
      dest[destPos + i] = 0xff & src[srcPos + i];
    }
  }

  public static void arraycopy(int[] src, int srcPos, byte[] dest, int destPos,
      int length) {
    for (int i = 0; i < length; i++) {
      dest[destPos + i] = (byte) src[srcPos + i];
    }
  }

}
