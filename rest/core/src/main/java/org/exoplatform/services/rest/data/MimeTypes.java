/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.services.rest.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MimeTypes {

  public static final String ALL = "*/*";

  private String[] mimeTypes_;
  
  private static final Pattern SPACE_PATTERN = Pattern.compile("(\\s+)");
  
  private static final QualityComparator COMPARATOR = new QualityComparator();
  
  private static class QualityComparator implements Comparator<String> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(String mimeType1, String mimeType2) {
      float q1 = getQvalue(mimeType1);
      float q2 = getQvalue(mimeType2);
      if (q1 < q2)
        return 1;
      if (q1 > q2)
        return -1;
      return 0;
    }
    
    private Float getQvalue(String s) {
      float q = 1.0F;
      int t = s.indexOf(';');
      
      if (t > 0) {
        String qstring = s.substring(t + 1);
        if (qstring.startsWith("q=")) {
          // Quality string can starts form '.', for example q=.3
          // It is the same to q=0.3
          String t2 = qstring.substring(2);
          if (t2.charAt(0) == '.')
            t2 = '0' + t2;
          q = Float.valueOf(t2);
          if (q >= 0F && q <= 1.0F)
            return q;
          throw new IllegalArgumentException("Invalid quality value "
              + q + ", must be between 0 and 1");
        }
      }
      return q;
    }

  }

  /**
   * @param mimeTypeHeader the Content-Type HTTP header.
   */
  public MimeTypes(String mimeTypeHeader) {
    Matcher m = SPACE_PATTERN.matcher(mimeTypeHeader);
    mimeTypeHeader = m.replaceAll("");
    mimeTypes_ = mimeTypeHeader.split(",");
    Arrays.sort(mimeTypes_, COMPARATOR);
    for (int i = 0; i < mimeTypes_.length; i++) {
      String t = mimeTypes_[i];
      mimeTypes_[i] = removeQvalue(t);
    }
  }
  
  /**
   * @return sorted array of mimetype.
   * @see org.exoplatform.services.rest.data.HeaderUtils.
   */
  public String[] getAsArray() {
    return mimeTypes_;
  }

  /**
   * Check does array has requested mimetype.
   * @param s requested mimetype.
   * @return result.
   */
  public boolean hasMimeType(String s) {
    for (String m : mimeTypes_) {
      m = removeQvalue(m);
      if (m.equalsIgnoreCase(s)) {
        return true;
      }
    }
    return false;
  }
  
  /*
   * Remove q value from given string.
   * Example text/xml;q=0.9 will be change to text/xml.
   * @param m the source string.
   * @return result string.
   */
  private static String removeQvalue(String m) {
    int q = m.indexOf(";q=");
    if (q > 0)
      m = m.substring(0, q);
    return m;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (String m : mimeTypes_) {
      if (sb.length() > 0)
        sb.append(',').append(' ');
      sb.append(m);
    }
    return sb.toString();
  }

}
