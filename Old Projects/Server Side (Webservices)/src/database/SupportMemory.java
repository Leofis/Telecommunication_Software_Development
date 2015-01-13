package database;

import java.util.ArrayList;
import java.util.Hashtable;

public class SupportMemory {
	
	protected ArrayList<String> genericIDs = new ArrayList<>();
	protected ArrayList<String> listIP = new ArrayList<>();
	protected ArrayList<String> listPattern = new ArrayList<>();
	
	protected ArrayList<String> usersAndroid = new ArrayList<>(); /* Probably it 'll change */
	
	protected ArrayList<String> liveIDs = new ArrayList<>();
	protected Hashtable<String, Long> liveIDsPartTwo = new Hashtable<>();
	protected Hashtable<String, String> liveInterfaces = new Hashtable<>();
	protected Hashtable<String, Integer> memoryIP = new Hashtable<>();
	protected Hashtable<String, Integer> memoryPattern = new Hashtable<>();
	
	public ArrayList<String> getGenericIDs() {
		return genericIDs;
	}
	
	public Hashtable<String, Integer> getMemoryIP() {
		return memoryIP;
	}
	
	public Hashtable<String, Integer> getMemoryPattern() {
		return memoryPattern;
	}
	
	public ArrayList<String> getListIP() {
		return listIP;
	}
	
	public ArrayList<String> getListPattern() {
		return listPattern;
	}

	public Hashtable<String, String> getLiveInterfaces() {
		return liveInterfaces;
	}

	protected void setLiveInterfaces(Hashtable<String, String> liveInterfaces) {
		this.liveInterfaces = liveInterfaces;
	}

	public ArrayList<String> getLiveIDs() {
		return liveIDs;
	}

	public Hashtable<String, Long> getLiveIDsPartTwo() {
		return liveIDsPartTwo;
	}

	public void setLiveIDsPartTwo(Hashtable<String, Long> liveIDsPartTwo) {
		this.liveIDsPartTwo = liveIDsPartTwo;
	}
}
