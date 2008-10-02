/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ws.frameworks.cometd.loadbalancer;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class Node {
 
  /**
   *The id of node.  
   */
  private String   id;
  
  /**
   * Base URL of this node.
   */
  private String    url;

  /**
   * The max connection allowed on this node.
   */
  private int       maxConenction;

  /**
   * How much already connected.
   */
  private int       connected;
  
  /**
   * True if node currently alive.
   */
  private boolean   alive;

  /**
   * @param url the base URL of node.
   * @param maxConenction the max count of allowed connection.
   */
  public Node(String id, String url, int maxConenction) {
    this.id = id;
    this.url = url;
    this.maxConenction = maxConenction;
    this.alive = true;
    this.connected = 0;
  }
  
  /**
   * 
   */
  public Node(String id, String url) {
    this.id = id;
    this.url = url;
    this.maxConenction = 0;
    this.alive = true;
    this.connected = 0;
  }

  /**
   * @return the URL of node in cluster.
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the URL to set.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the max allowed connection on this node.  
   */
  public int getMaxConenction() {
    return maxConenction;
  }

  /**
   * @param maxConenction set max allowed connection on this node.
   */
  public void setMaxConenction(int maxConenction) {
    this.maxConenction = maxConenction;
  }

  /**
   * @param connected set how much already connected.   
   */
  public void setConnected(int connected) {
    this.connected = connected;
  }

  /**
   * @return how much already connected to this node. 
   */
  public int getConnected() {
    return connected;
  }

  /**
   * @return the alive
   */
  public boolean isAlive() {
    return alive;
  }

  /**
   * @param alive the alive to set
   */
  public void setAlive(boolean alive) {
    this.alive = alive;
  }
  
  /**
   * Add new connection.
   */
  public void addConnection() {
    this.connected++;
  }
  
  /**
   * Remove connection.
   */
  public void delConnection() {
    this.connected--;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  
  

}
