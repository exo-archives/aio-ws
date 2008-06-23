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
public interface MessagePart {
  
  public final static int TYPE_APPLICATION_CONSTRUCTED_OBJECT = 0x60;
  public final static int TYPE_NEG_TOKEN_INIT = 0xa0;
  public final static int TYPE_NEG_TOKEN_TARG = 0xa1;
  public final static int TYPE_SEQUENCE = 0x30;
  public final static int TYPE_OID = 0x06;
  public final static int TYPE_OCTET_STRING = 0x04;
  public final static int TYPE_ENUMERATED = 0x0a;
  public final static int TYPE_BIT_STRING = 0x03;

  public int getDerType();
  
  public int[] toDer();
  
  public void parse( ParseState state );
  
}
