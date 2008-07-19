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

import java.util.Arrays;

/**
 * @author Martin Algesten
 */
public class Oid extends AbstractMessagePart {

  public static final int[] OID_SPNEGO = new int[] {0x2b, 0x06, 0x01, 0x05, 0x05, 0x02};

  public static final int[] OID_KERBEROS_MICROSOFT = new int[] {0x2a, 0x86, 0x48, 0x82, 0xf7, 0x12, 0x01, 0x02, 0x02};

  public static final int[] OID_KERBEROS = new int[] {0x2a, 0x86, 0x48, 0x86, 0xf7, 0x12, 0x01, 0x02, 0x02};

  private int[] oid = null;

  /**
   * {@inheritDoc} 
   */
  public int getDerType() {
    return TYPE_OID;
  }

  /**
   * {@inheritDoc} 
   */
  public int[] toDer() {
    return wrap(TYPE_OID, oid);
  }

  /**
   * {@inheritDoc} 
   */
  public void parse(ParseState state) {
    state.setPhase("OID");
    state.expect(TYPE_OID, true, "Expected oid identifier");
    int length = state.parseDerLength();
    oid = new int[length];
    arraycopy(state.getToken(), state.getIndex(), oid, 0, length);
    state.setIndex(state.getIndex() + length);
  }

  /**
   * @return the oid
   */
  public int[] getOid() {
    return oid;
  }

  /**
   * @param oid the oid to set
   */
  public void setOid(int[] oid) {
    this.oid = oid;
  }

  public boolean isSpnego() {
    return Arrays.equals(oid, OID_SPNEGO);
  }

  public boolean isKerberosMicrosoft() {
    return Arrays.equals(oid, OID_KERBEROS_MICROSOFT);
  }

  public boolean isKerberos() {
    return Arrays.equals(oid, OID_KERBEROS);
  }

}
