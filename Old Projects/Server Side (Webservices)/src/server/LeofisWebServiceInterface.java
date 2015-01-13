package server;
  
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.DOCUMENT)
public interface LeofisWebServiceInterface 
{      
	@WebMethod(operationName = "register")
    public boolean register(@WebParam(name = "arg0") String nodeId);
	
	@WebMethod(operationName = "maliciousPatternsStatisticalReports")
	public void maliciousPatternsStatisticalReports(@WebParam(name = "arg0") String nodeID, @WebParam(name = "arg1") StatisticalReports m);
	
	@WebMethod(operationName = "maliciousPatternRequest")
	public MaliciousPatterns maliciousPatternRequest(@WebParam(name = "arg0") String nodeID);
	 
	@WebMethod(operationName = "unregister")
    public boolean unregister(@WebParam(name = "arg0") String nodeId);
	
	@WebMethod(operationName = "remove")
    public boolean remove(@WebParam(name = "nodeID") String nodeId);
	
	@WebMethod(operationName = "login")
	public int login (@WebParam(name = "username") String username, @WebParam(name = "password") String password);
	
	@WebMethod(operationName = "logout")
	public boolean logout (@WebParam(name = "username") String username, @WebParam(name = "password") String password);
	
	@WebMethod(operationName = "registerAndroid")
	public boolean registerAndroid (@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "nodes") AvailableNodes nodes);
	
	@WebMethod(operationName = "retrieveStatistics")
	public List<StatisticalReports> retrieveStatistics(@WebParam(name = "username") String username, @WebParam(name = "password") String password);
	
	@WebMethod(operationName = "retrieveMaliciousPatterns")
	public String retrieveMaliciousPatterns(@WebParam(name = "username") String username, @WebParam(name = "password") String password);
	
	@WebMethod(operationName = "insertMaliciousPatterns")
	public boolean insertMaliciousPatterns(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "maliciousIP") String maliciousIP, @WebParam(name = "maliciousPattern")String maliciousPattern);
}