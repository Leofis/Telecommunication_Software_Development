package server;

public class LeofisWebServiceInterfaceProxy implements server.LeofisWebServiceInterface {
  private String _endpoint = null;
  private server.LeofisWebServiceInterface leofisWebServiceInterface = null;
  
  public LeofisWebServiceInterfaceProxy() {
    _initLeofisWebServiceInterfaceProxy();
  }
  
  public LeofisWebServiceInterfaceProxy(String endpoint) {
    _endpoint = endpoint;
    _initLeofisWebServiceInterfaceProxy();
  }
  
  private void _initLeofisWebServiceInterfaceProxy() {
    try {
      leofisWebServiceInterface = (new server.LeofisWebServiceImplementationServiceLocator()).getLeofisWebServiceImplementationPort();
      if (leofisWebServiceInterface != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)leofisWebServiceInterface)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)leofisWebServiceInterface)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (leofisWebServiceInterface != null)
      ((javax.xml.rpc.Stub)leofisWebServiceInterface)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public server.LeofisWebServiceInterface getLeofisWebServiceInterface() {
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface;
  }
  
  public void maliciousPatternsStatisticalReports(java.lang.String arg0, server.StatisticalReports arg1) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    leofisWebServiceInterface.maliciousPatternsStatisticalReports(arg0, arg1);
  }
  
  public boolean unregister(java.lang.String arg0) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.unregister(arg0);
  }
  
  public server.MaliciousPatterns maliciousPatternRequest(java.lang.String arg0) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.maliciousPatternRequest(arg0);
  }
  
  public boolean login(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.login(username, password);
  }
  
  public boolean logout(java.lang.String username) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.logout(username);
  }
  
  public boolean registerAndroid(java.lang.String username, java.lang.String password, java.lang.String[] nodes) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.registerAndroid(username, password, nodes);
  }
  
  public server.StatisticalReports[] retrieveStatistics(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.retrieveStatistics(username, password);
  }
  
  public java.lang.String retrieveMaliciousPatterns(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.retrieveMaliciousPatterns(username, password);
  }
  
  public void insertMaliciousPatterns(java.lang.String username, java.lang.String password, java.lang.String malliciousIP, java.lang.String malliciousPattern) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    leofisWebServiceInterface.insertMaliciousPatterns(username, password, malliciousIP, malliciousPattern);
  }
  
  public boolean remove(java.lang.String nodeID) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.remove(nodeID);
  }
  
  public boolean register(java.lang.String arg0) throws java.rmi.RemoteException{
    if (leofisWebServiceInterface == null)
      _initLeofisWebServiceInterfaceProxy();
    return leofisWebServiceInterface.register(arg0);
  }
  
  
}