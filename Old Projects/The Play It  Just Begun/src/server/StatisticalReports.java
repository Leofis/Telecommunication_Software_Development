/**
 * StatisticalReports.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package server;

public class StatisticalReports  implements java.io.Serializable {
    private server.StatisticalReportsIpFreqEntry[] ipFreq;

    private server.StatisticalReportsLiveInterfacesEntry[] liveInterfaces;

    private server.StatisticalReportsPatternFreqEntry[] patternFreq;

    public StatisticalReports() {
    }

    public StatisticalReports(
           server.StatisticalReportsIpFreqEntry[] ipFreq,
           server.StatisticalReportsLiveInterfacesEntry[] liveInterfaces,
           server.StatisticalReportsPatternFreqEntry[] patternFreq) {
           this.ipFreq = ipFreq;
           this.liveInterfaces = liveInterfaces;
           this.patternFreq = patternFreq;
    }


    /**
     * Gets the ipFreq value for this StatisticalReports.
     * 
     * @return ipFreq
     */
    public server.StatisticalReportsIpFreqEntry[] getIpFreq() {
        return ipFreq;
    }


    /**
     * Sets the ipFreq value for this StatisticalReports.
     * 
     * @param ipFreq
     */
    public void setIpFreq(server.StatisticalReportsIpFreqEntry[] ipFreq) {
        this.ipFreq = ipFreq;
    }


    /**
     * Gets the liveInterfaces value for this StatisticalReports.
     * 
     * @return liveInterfaces
     */
    public server.StatisticalReportsLiveInterfacesEntry[] getLiveInterfaces() {
        return liveInterfaces;
    }


    /**
     * Sets the liveInterfaces value for this StatisticalReports.
     * 
     * @param liveInterfaces
     */
    public void setLiveInterfaces(server.StatisticalReportsLiveInterfacesEntry[] liveInterfaces) {
        this.liveInterfaces = liveInterfaces;
    }


    /**
     * Gets the patternFreq value for this StatisticalReports.
     * 
     * @return patternFreq
     */
    public server.StatisticalReportsPatternFreqEntry[] getPatternFreq() {
        return patternFreq;
    }


    /**
     * Sets the patternFreq value for this StatisticalReports.
     * 
     * @param patternFreq
     */
    public void setPatternFreq(server.StatisticalReportsPatternFreqEntry[] patternFreq) {
        this.patternFreq = patternFreq;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StatisticalReports)) return false;
        StatisticalReports other = (StatisticalReports) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ipFreq==null && other.getIpFreq()==null) || 
             (this.ipFreq!=null &&
              java.util.Arrays.equals(this.ipFreq, other.getIpFreq()))) &&
            ((this.liveInterfaces==null && other.getLiveInterfaces()==null) || 
             (this.liveInterfaces!=null &&
              java.util.Arrays.equals(this.liveInterfaces, other.getLiveInterfaces()))) &&
            ((this.patternFreq==null && other.getPatternFreq()==null) || 
             (this.patternFreq!=null &&
              java.util.Arrays.equals(this.patternFreq, other.getPatternFreq())));
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
        if (getIpFreq() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getIpFreq());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getIpFreq(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLiveInterfaces() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLiveInterfaces());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLiveInterfaces(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPatternFreq() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPatternFreq());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPatternFreq(), i);
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
        new org.apache.axis.description.TypeDesc(StatisticalReports.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://server/", "statisticalReports"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipFreq");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipFreq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://server/", ">>statisticalReports>ipFreq>entry"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "entry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("liveInterfaces");
        elemField.setXmlName(new javax.xml.namespace.QName("", "liveInterfaces"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://server/", ">>statisticalReports>liveInterfaces>entry"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "entry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patternFreq");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patternFreq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://server/", ">>statisticalReports>patternFreq>entry"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "entry"));
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
