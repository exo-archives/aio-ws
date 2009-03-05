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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

import org.exoplatform.services.security.sso.spnego.message.ApplicationConstructedObject;
import org.exoplatform.services.security.sso.spnego.message.NegTokenTarg;
import org.exoplatform.services.security.sso.spnego.message.ParseState;

/**
 * Parser for convering <code>byte[]</code> spnego tokens into nice objects.
 * @author Martin Algesten
 */
public class SpnegoParser {

  /**
   * Logger.
   */
  static final Log LOG = ExoLogger.getLogger("ws.security.SpnegoParser");

  /**
   * Parses an incoming NegTokenInit.
   * @param token the byte array to parse.
   * @return the object if no errors were detected or null if there were errors.
   */
  public ApplicationConstructedObject parseInitToken(byte[] token) {

    ParseState state = new ParseState(token);
    ApplicationConstructedObject result = new ApplicationConstructedObject();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Token dump: " + dump(token));
    }

    try {
      result.parse(state);
    } catch (Exception e) {
      LOG.error("Failed to parse: " + e.getMessage());
      if (LOG.isDebugEnabled()) {
        LOG.debug("Failed to parse", e);
      }
    }

    for (String message : state.getMessages()) {
      LOG.info(message);
    }

    if (state.getMessages().size() == 0) {
      return result;
    } else {
      return null;
    }

  }

  /**
   * Parses a NegTokenTarg.
   * @param token the byte array token.
   * @return the parsed token if no error were encountered, or null if there
   *         were errors.
   */
  public NegTokenTarg parseTargToken(byte[] token) {

    ParseState state = new ParseState(token);

    NegTokenTarg result = new NegTokenTarg();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Token dump: " + dump(token));
    }

    try {
      result.parse(state);
    } catch (Exception e) {
      LOG.error("Failed to parse: " + e.getMessage());
      if (LOG.isDebugEnabled()) {
        LOG.debug("Failed to parse", e);
      }
    }

    for (String message : state.getMessages()) {
      LOG.info(message);
    }

    if (state.getMessages().size() == 0) {
      return result;
    } else {
      return null;
    }

  }

  /**
   * Dumps the given token in a nice string.
   * @param token the source bytes.
   * @return the result String.
   */
  protected String dump(byte[] token) {

    char[] dump = Hex.encodeHex(token);

    StringBuilder buf = new StringBuilder();

    buf.append("0 : ");

    for (int i = 0; i < dump.length; i += 2) {
      buf.append("0x");
      buf.append(dump[i]);
      buf.append(dump[i + 1]);
      buf.append(" ");
      if (i > 0 && i % 30 == 0) {
        buf.append("\n" + (i / 2 + 1) + ": ");
      }
    }

    return buf.toString();

  }

}
