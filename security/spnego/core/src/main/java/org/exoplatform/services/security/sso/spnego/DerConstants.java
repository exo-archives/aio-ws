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
package org.exoplatform.services.security.sso.spnego;

/**
 * Constants needed for unwrapping/wrapping the incoming/outgoing Spnego message.
 * @author Martin Algesten
 */
public interface DerConstants {

  /**
   * Type identifier for NegTokenInit messages (0xa0).
   */
  static final byte[] TYPE_SPNEGO_NEGTOKENINIT = new byte[] {(byte) 0xa0};
  
  /**
   * Type identifier for NegTokenTarg messages (0xa1).
   */
  static final byte[] TYPE_SPNEGO_NEGTOKENTARG = new byte[] {(byte) 0xa1};
  
  /**
   * Type identifier for Application Constructed Objects (0x60).
   */
  static final byte[] TYPE_APP_CONSTRUCTED_OBJECT = new byte[] {0x60};

  /**
   * Type identifier for octet strings (0x04).
   */
  static final byte[] TYPE_OCTET_STRING = new byte[] {0x04};

  /**
   * Type identifier for sequences (0x30).
   */
  static final byte[] TYPE_SEQUENCE = new byte[] {0x30};

  
  /**
   * Position 0 in TYPE_SEQUENCE (0xa0).
   */
  static final byte[] SEQ_POS_0 = new byte[] {(byte) 0xa0};

  /**
   * Position 1 in TYPE_SEQUENCE (0xa1).
   */
  static final byte[] SEQ_POS_1 = new byte[] {(byte) 0xa1};

  /**
   * Position 2 in TYPE_SEQUENCE (0xa2).
   */
  static final byte[] SEQ_POS_2 = new byte[] {(byte) 0xa2};
  
  
  
  /**
   * Oid for SPNEGO 1.3.6.1.5.5.2 .
   */
  static final byte[] OID_SPNEGO = new byte[] {0x06, 0x06, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x02};
  
  /**
   * Oid for Kerberos with one bit wrong (needed for compatibility reasons) 1.2.840.48018.1.2.2 .
   */
  static final byte[] OID_KERBEROS_V5_COMPAT = new byte[] {0x06, 0x09, 0x2a, (byte) 0x86, 0x48,
    (byte) 0x82, (byte) 0xf7, 0x12, 0x01, 0x02, 0x02};

  /**
   * Oid for Kerberos proper 1.2.840.113554.1.2.2 .
   */
  static final byte[] OID_KERBEROS_V5 = new byte[] {0x06, 0x09, 0x2a, (byte) 0x86, 0x48, (byte) 0x86,
    (byte) 0xf7, 0x12, 0x01, 0x02, 0x02};

  
  static final byte[] NEGOTIATION_RESULT = new byte[] {(byte) 0xa0, 0x03, 0x0a, 0x01, 0x0A, 0x01, 0x01};

}
