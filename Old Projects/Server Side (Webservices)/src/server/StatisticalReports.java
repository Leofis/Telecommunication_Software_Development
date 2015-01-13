package server;

import java.util.Hashtable;

public class StatisticalReports {

	private Hashtable<String, Integer> ipFreq = new Hashtable<>() ;
	private Hashtable<String, Integer> patternFreq = new Hashtable<>() ;
	private Hashtable<String, String> liveInterfaces = new Hashtable<>();
	
	private String ipFreqS;
	private String patternFreqS;
	private String liveInterfacesS;
	
	public Hashtable<String, Integer> getIpFreq() {
		return ipFreq;
	}
	public void setIpFreq(Hashtable<String, Integer> ipFreq) {
		this.ipFreq = ipFreq;
	}
	public Hashtable<String, Integer> getPatternFreq() {
		return patternFreq;
	}
	public void setPatternFreq(Hashtable<String, Integer> ipPattern) {
		this.patternFreq = ipPattern;
	}
	public Hashtable<String, String> getLiveInterfaces() {
		return liveInterfaces;
	}
	public void setLiveInterfaces(Hashtable<String, String> liveInterfaces) {
		this.liveInterfaces = liveInterfaces;
	}
	public String getIpFreqS() {
		return ipFreqS;
	}
	public void setIpFreqS(String ipFreqS) {
		this.ipFreqS = ipFreqS;
	}
	public String getPatternFreqS() {
		return patternFreqS;
	}
	public void setPatternFreqS(String patternFreqS) {
		this.patternFreqS = patternFreqS;
	}
	public String getLiveInterfacesS() {
		return liveInterfacesS;
	}
	public void setLiveInterfacesS(String liveInterfacesS) {
		this.liveInterfacesS = liveInterfacesS;
	}
}
