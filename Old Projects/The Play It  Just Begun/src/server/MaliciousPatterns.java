/**
 * MaliciousPatterns.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package server;

public class MaliciousPatterns  implements java.io.Serializable {
    private java.lang.String[] maliciousIPs;

    private java.lang.String[] maliciousPatterns;

    public MaliciousPatterns() {
    }

    public MaliciousPatterns(
           java.lang.String[] maliciousIPs,
           java.lang.String[] maliciousPatterns) {
           this.maliciousIPs = maliciousIPs;
           this.maliciousPatterns = maliciousPatterns;
    }


    /**
     * Gets the maliciousIPs value for this MaliciousPatterns.
     * 
     * @return maliciousIPs
     */
    public java.lang.String[] getMaliciousIPs() {
        return maliciousIPs;
    }


    /**
     * Sets the maliciousIPs value for this MaliciousPatterns.
     * 
     * @param maliciousIPs
     */
    public void setMaliciousIPs(java.lang.String[] maliciousIPs) {
        this.maliciousIPs = maliciousIPs;
    }

    public java.lang.String getMaliciousIPs(int i) {
        return this.maliciousIPs[i];
    }

    public void setMaliciousIPs(int i, java.lang.String _value) {
        this.maliciousIPs[i] = _value;
    }


    /**
     * Gets the maliciousPatterns value for this MaliciousPatterns.
     * 
     * @return maliciousPatterns
     */
    public java.lang.String[] getMaliciousPatterns() {
        return maliciousPatterns;
    }


    /**
     * Sets the maliciousPatterns value for this MaliciousPatterns.
     * 
     * @param maliciousPatterns
     */
    public void setMaliciousPatterns(java.lang.String[] maliciousPatterns) {
        this.maliciousPatterns = maliciousPatterns;
    }

    public java.lang.String getMaliciousPatterns(int i) {
        return this.maliciousPatterns[i];
    }

    public void setMaliciousPatterns(int i, java.lang.String _value) {
        this.maliciousPatterns[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MaliciousPatterns)) return false;
        MaliciousPatterns other = (MaliciousPatterns) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.maliciousIPs==null && other.getMaliciousIPs()==null) || 
             (this.maliciousIPs!=null &&
              java.util.Arrays.equals(this.maliciousIPs, other.getMaliciousIPs()))) &&
            ((this.maliciousPatterns==null && other.getMaliciousPatterns()==null) || 
             (this.maliciousPatterns!=null &&
              java.util.Arrays.equals(this.maliciousPatterns, other.getMaliciousPatterns())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMaliciousIPs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMaliciousIPs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMaliciousIPs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMaliciousPatterns() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMaliciousPatterns());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMaliciousPatterns(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MaliciousPatterns.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://server/", "maliciousPatterns"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maliciousIPs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maliciousIPs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maliciousPatterns");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maliciousPatterns"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
