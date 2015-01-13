/**
 * LeofisWebServiceInterface.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package server;

public interface LeofisWebServiceInterface extends java.rmi.Remote {
    public void maliciousPatternsStatisticalReports(java.lang.String arg0, server.StatisticalReports arg1) throws java.rmi.RemoteException;
    public boolean unregister(java.lang.String arg0) throws java.rmi.RemoteException;
    public server.MaliciousPatterns maliciousPatternRequest(java.lang.String arg0) throws java.rmi.RemoteException;
    public boolean login(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public boolean logout(java.lang.String username) throws java.rmi.RemoteException;
    public boolean registerAndroid(java.lang.String username, java.lang.String password, java.lang.String[] nodes) throws java.rmi.RemoteException;
    public server.StatisticalReports[] retrieveStatistics(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public java.lang.String retrieveMaliciousPatterns(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public void insertMaliciousPatterns(java.lang.String username, java.lang.String password, java.lang.String malliciousIP, java.lang.String malliciousPattern) throws java.rmi.RemoteException;
    public boolean remove(java.lang.String nodeID) throws java.rmi.RemoteException;
    public boolean register(java.lang.String arg0) throws java.rmi.RemoteException;
}
