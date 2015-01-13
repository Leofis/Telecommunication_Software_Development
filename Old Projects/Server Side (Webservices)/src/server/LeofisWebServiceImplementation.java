package server;

import java.util.List;

import javax.jws.WebService;

import database.Jdbc;

@WebService(endpointInterface="server.LeofisWebServiceInterface")
public class LeofisWebServiceImplementation implements LeofisWebServiceInterface {
	private final Jdbc database = new Jdbc();
	
	@Override
	public boolean register(String nodeId) {
		return database.checkGenericID(nodeId);
	}

	@Override
	public void maliciousPatternsStatisticalReports(String nodeID,
			StatisticalReports m) {
		database.sendStatistical(nodeID, m);
	}

	@Override
	public MaliciousPatterns maliciousPatternRequest(String nodeID) {
		MaliciousPatterns maliciousPatterns = database.checkMaliciousList(nodeID);
		return maliciousPatterns;
	}

	@Override
	public boolean unregister(String nodeId) {
		return database.removeGenericID(nodeId);
	}
	
	@Override
	public boolean remove(String nodeId) {
		return database.deleteGenericID(nodeId);
	}
	
	@Override
	public int login(String username, String password) {
		return database.checkLogin(username, password);
	}

	@Override
	public boolean logout(String username , String password) {
		return database.checkLogout(username,password);
	}

	@Override
	public boolean registerAndroid(String username, String password,
			AvailableNodes nodes) {
		return database.checkAndroidDevice(username, password, nodes);
	}

	@Override
	public List<StatisticalReports> retrieveStatistics(String username,
			String password) {
		return database.getStatisticalMobile(username, password);
	}

	@Override
	public String retrieveMaliciousPatterns(String username, String password) {
		return database.getMaliciousMobile(username, password);
	}

	@Override
	public boolean insertMaliciousPatterns(String username, String password,
			String malliciousIP, String maliciousPattern) {
		return database.sendMaliciousMobile(username, password, malliciousIP, maliciousPattern);
	}

	public Jdbc getDatabase() {
		return database;
	}
}