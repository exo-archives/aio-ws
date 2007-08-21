
package org.exoplatform.soap.xfire;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="calculateResult" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "calculateResult"
})
@XmlRootElement(name = "calculateResponse")
public class CalculateResponse {

    protected float calculateResult;

    /**
     * Gets the value of the calculateResult property.
     * 
     */
    public float getCalculateResult() {
        return calculateResult;
    }

    /**
     * Sets the value of the calculateResult property.
     * 
     */
    public void setCalculateResult(float value) {
        this.calculateResult = value;
    }

}
