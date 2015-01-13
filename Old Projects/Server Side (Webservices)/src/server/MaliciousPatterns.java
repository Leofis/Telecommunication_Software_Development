package server;

import java.util.ArrayList;

public class MaliciousPatterns {

	private ArrayList<String> maliciousIPs = new ArrayList<>();
	private ArrayList<String> maliciousPatterns = new ArrayList<>();

	public ArrayList<String> getMaliciousIPs() {
		return maliciousIPs;
	}

	public void setMaliciousIPs(ArrayList<String> maliciousIPs) {
		this.maliciousIPs = maliciousIPs;
	}

	public ArrayList<String> getMaliciousPatterns() {
		return maliciousPatterns;
	}

	public void setMaliciousPatterns(ArrayList<String> maliciousPatterns) {
		this.maliciousPatterns = maliciousPatterns;
	}

	public  void addIP(String string)
	{
		maliciousIPs.add(string);
	}
	
	public  void addPa(String string)
	{
		maliciousPatterns.add(string);
	}
	
}
