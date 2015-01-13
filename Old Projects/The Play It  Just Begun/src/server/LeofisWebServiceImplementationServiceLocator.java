/**
 * LeofisWebServiceImplementationServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package server;

public class LeofisWebServiceImplementationServiceLocator extends org.apache.axis.client.Service implements server.LeofisWebServiceImplementationService {

    public LeofisWebServiceImplementationServiceLocator() {
    }


    public LeofisWebServiceImplementationServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LeofisWebServiceImplementationServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LeofisWebServiceImplementationPort
    private java.lang.String LeofisWebServiceImplementationPort_address = "http://127.0.0.1:9999/LeofisService/";

    public java.lang.String getLeofisWebServiceImplementationPortAddress() {
        return LeofisWebServiceImplementationPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LeofisWebServiceImplementationPortWSDDServiceName = "LeofisWebServiceImplementationPort";

    public java.lang.String getLeofisWebServiceImplementationPortWSDDServiceName() {
        return LeofisWebServiceImplementationPortWSDDServiceName;
    }

    public void setLeofisWebServiceImplementationPortWSDDServiceName(java.lang.String name) {
        LeofisWebServiceImplementationPortWSDDServiceName = name;
    }

    public server.LeofisWebServiceInterface getLeofisWebServiceImplementationPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LeofisWebServiceImplementationPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLeofisWebServiceImplementationPort(endpoint);
    }

    public server.LeofisWebServiceInterface getLeofisWebServiceImplementationPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            server.LeofisWebServiceImplementationPortBindingStub _stub = new server.LeofisWebServiceImplementationPortBindingStub(portAddress, this);
            _stub.setPortName(getLeofisWebServiceImplementationPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLeofisWebServiceImplementationPortEndpointAddress(java.lang.String address) {
        LeofisWebServiceImplementationPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (server.LeofisWebServiceInterface.class.isAssignableFrom(serviceEndpointInterface)) {
                server.LeofisWebServiceImplementationPortBindingStub _stub = new server.LeofisWebServiceImplementationPortBindingStub(new java.net.URL(LeofisWebServiceImplementationPort_address), this);
                _stub.setPortName(getLeofisWebServiceImplementationPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("LeofisWebServiceImplementationPort".equals(inputPortName)) {
            return getLeofisWebServiceImplementationPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://server/", "LeofisWebServiceImplementationService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://server/", "LeofisWebServiceImplementationPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LeofisWebServiceImplementationPort".equals(portName)) {
            setLeofisWebServiceImplementationPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
