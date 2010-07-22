/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.common.util;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Jun
 * 25, 2010
 */
public class MediaType {

  /**
   * Main type f.e. 'text' in 'text/xml'. 
   */
  private String             type;

  /**
   * Main type f.e. 'xml' in 'text/xml'. 
   */
  private String             subtype;

  /**
   * Wildcard type. 
   */
  public static final String MEDIA_TYPE_WILDCARD = "*";
  
  public static final String WILDCARD = "*/*";

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }
  
  /**
   * Creates an instance of specified mediatype.
   * @param mediaType mediatype, f.e.: text/plain
   */
  public MediaType(String mediaType) {
    this.type = mediaType.split("/")[0];
    this.subtype = mediaType.split("/")[1];
  }

  /**
   * Creates an instance of mediatype with specified type and subtype.
   * @param type main type f.e. 'text' in 'text/xml'. 
   * @param subtype subtype f.e. 'xmk' in 'text/xml'.
   */
  public MediaType(String type, String subtype) {
    this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
    this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;
  }
  
  /**
   * Check if this media type is compatible with another media type. E.g.
   * image/* is compatible with image/jpeg, image/png, etc. Media type
   * parameters are ignored. The function is commutative.
   * @return true if the types are compatible, false otherwise.
   * @param other the media type to compare with
   */
  public boolean isCompatible(MediaType other) {
    if (other == null){
        return false;}
    if (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD)){
        return true;}
    else if (type.equalsIgnoreCase(other.type) && (subtype.equals(MEDIA_TYPE_WILDCARD) || other.subtype.equals(MEDIA_TYPE_WILDCARD))){
        return true;}
    else{
        return this.type.equalsIgnoreCase(other.type)
            && this.subtype.equalsIgnoreCase(other.subtype);}
}
  
  @Override
  public String toString() {
    return type + "/" + subtype;
  }

}
