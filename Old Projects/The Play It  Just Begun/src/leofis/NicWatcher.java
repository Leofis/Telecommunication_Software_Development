package leofis;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import server.LeofisWebServiceInterface;
import server.StatisticalReports;
import server.StatisticalReportsIpFreqEntry;
import server.StatisticalReportsLiveInterfacesEntry;
import server.StatisticalReportsPatternFreqEntry;

public class NicWatcher implements Runnable {
	public volatile boolean iAmDead = false;
	
	CountDownLatch fourthTask;
	Hashtable<String, Integer> ipFreq;
	Hashtable<String, Integer> patternFreq;
	ArrayList<String> maliciousPatterns;
	ArrayList<String> maliciousIPs;

	 ArrayList<PcapIf> oldNics = new ArrayList<PcapIf>();
	 ArrayList<PcapIf> addedNics = new ArrayList<PcapIf>();
	 ArrayList<PcapIf> removedNics = new ArrayList<PcapIf>();
	 ArrayList<PcapIf> newNics = new ArrayList<PcapIf>();

	 List<TheWatcher> allCurrentWatchers = new ArrayList<TheWatcher>();
	
	public NicWatcher(CountDownLatch fourthTask) {
		this.fourthTask = fourthTask;
	}

	public void run() {
		System.out.println("I am the Nic Watcher. Starting monitor Nics :");
		System.out.println();
		fourthTask.countDown();

		/*---------------------------------------------------------------------*/

		while (!iAmDead) {

			try {
				Thread.sleep((long)ThreadPlay.wtNicWatcher*1000);
			} catch (Exception x) {
			}

			StringBuilder errbuf = new StringBuilder(); // For any error msgs

			/***************************************************************************
			 * First get a list of devices on this system
			 **************************************************************************/
			int r = Pcap.findAllDevs(newNics, errbuf);

			//System.out.println("Detected " + newNics.size() + " devices...");

			if (r == Pcap.NOT_OK || newNics.isEmpty()) {
				System.err.printf("Can't read list of devices, error is %s",errbuf.toString());
				return;
			}
			
			findAddedNics();
			findRemovedNics();
			
			StatisticalReportsLiveInterfacesEntry[] liveDevices = new StatisticalReportsLiveInterfacesEntry[newNics.size()+removedNics.size()+addedNics.size()];
			StatisticalReportsIpFreqEntry[] ipFr = new StatisticalReportsIpFreqEntry[0];
			StatisticalReportsPatternFreqEntry[] paFr = new StatisticalReportsPatternFreqEntry[0];
			
			int j = 0;
			
			for (int i=0; i<newNics.size();i++)
			{
				if (!newNics.get(i).getName().equals("nflog")
						&& !newNics.get(i).getName().equals("any") && !newNics.get(i).getName().equals("bluetooth0") 
						&& !newNics.get(i).getName().equals("nfqueue") && !newNics.get(i).getName().equals("lo")) {
					liveDevices[j] = new StatisticalReportsLiveInterfacesEntry();
					liveDevices[j].setKey(SendGenericId.nodeID +" "+ newNics.get(i).getName());
					liveDevices[j].setValue("Online");
					j++;
				}
			}
			
			findAddedNics();
			findRemovedNics();
			
			
			if (addedNics.size() >= 1) {
				for (PcapIf device : addedNics) {
					TheWatcher watcher = new TheWatcher(ipFreq,patternFreq,maliciousIPs,maliciousPatterns, device);
					watcher.setName("TheWatcher");
					allCurrentWatchers.add(watcher);
					liveDevices[j] = new StatisticalReportsLiveInterfacesEntry();
					liveDevices[j].setKey(SendGenericId.nodeID +" "+ device.getName());
					liveDevices[j].setValue("Online");
					j++;
					System.out.println("I am creating a Watcher for "+device.getName()+". Total number of Watchers : "+ allCurrentWatchers.size());
					watcher.start();
				}
			}

			if (removedNics.size() >= 1) {
				
				List<TheWatcher> allcurrentWatchersTemp = new ArrayList<TheWatcher>();
				allcurrentWatchersTemp.addAll(allCurrentWatchers);
				
				//try {
					for(PcapIf device : removedNics){
						TheWatcher tobeDelWatcher = null;
						for (int i = 0; i < allCurrentWatchers.size(); i++) {
							tobeDelWatcher = allCurrentWatchers.get(i);
							//byte[] mac = tobeDelWatcher.getHwAddress();
							String mac = tobeDelWatcher.getHwName();
							//if ( asString(mac)==null ) System.out.println("Attenton!!!! MAC IS NULL ! Dangerous PATH!");
							if ( mac==null ) System.out.println("Attenton!!!! Device hasn't name! Dangerous PATH!");
							//if (asString(device.getHardwareAddress()).equals(asString(mac)))
							if (device.getName().equals(mac))
							{
								System.out.println("The operation of " + tobeDelWatcher.device.getName()+" stopped. Disconnected !");
								liveDevices[j] = new StatisticalReportsLiveInterfacesEntry();
								liveDevices[j].setKey(SendGenericId.nodeID +" "+ mac);
								liveDevices[j].setValue("Offline");
								tobeDelWatcher.killTheWatcher();
								allcurrentWatchersTemp.remove(tobeDelWatcher);
								j++;
							}
						}
						allCurrentWatchers.clear();
						allCurrentWatchers.addAll(allcurrentWatchersTemp);
					}
			//	} catch (IOException x) {
			//		x.getMessage();
			//	}
			}

			oldNics.clear();
			oldNics.addAll(newNics);
			newNics.clear();

			StatisticalReports reports = new StatisticalReports();
    		reports.setLiveInterfaces(liveDevices);
    		reports.setIpFreq(ipFr);
    		reports.setPatternFreq(paFr);
	    	LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
			try {
				iface.maliciousPatternsStatisticalReports(SendGenericId.nodeID, reports);
			} catch (RemoteException e) {
			}
		}
		
		StatisticalReportsLiveInterfacesEntry[] liveDevices = new StatisticalReportsLiveInterfacesEntry[allCurrentWatchers.size()];
		StatisticalReportsIpFreqEntry[] ipFr = new StatisticalReportsIpFreqEntry[0];
		StatisticalReportsPatternFreqEntry[] paFr = new StatisticalReportsPatternFreqEntry[0];
		int j = 0;		
		for (int i = 0; i < allCurrentWatchers.size(); i++) {
			TheWatcher tw = allCurrentWatchers.get(i);
			liveDevices[j] = new StatisticalReportsLiveInterfacesEntry();
			liveDevices[j].setKey(SendGenericId.nodeID +" "+ tw.getHwName());
			liveDevices[j].setValue("has been shutted down.");
			j++;
			System.out.println("The operation of " + tw.device.getName()+ " stopped with user's signal");
			tw.killTheWatcher();
			try {
				tw.join();
			} catch (InterruptedException e) {
			}
		}
		
		StatisticalReports reports = new StatisticalReports();
		reports.setLiveInterfaces(liveDevices);
		reports.setIpFreq(ipFr);
		reports.setPatternFreq(paFr);
    	LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
		try {
			iface.maliciousPatternsStatisticalReports(SendGenericId.nodeID, reports);
		} catch (RemoteException e) {
		}

		System.out.println("Nicwatcher finished successfully.");
		/*-----------------------------------------------------------------------*/
	}

	/**private String asString(final byte[] mac) {
		final StringBuilder buf = new StringBuilder();
		for (byte b : mac) {
			if (buf.length() != 0) {
				buf.append(':');
			}
			if (b >= 0 && b < 16) {
				buf.append('0');
			}
			buf.append(Integer.toHexString((b < 0) ? b + 256 : b).toUpperCase());
		}

		return buf.toString();
	}**/

	private void findAddedNics() {
		//try {
			// System.out.println("Old state has " + oldNics.size() +
			// " devices");
			// System.out.println("New state has " + newNics.size() +
			// " devices");
			addedNics.clear();

			boolean foundInOld = false;

			for (PcapIf device : newNics) {
				foundInOld = false;
				String mac = device.getName();
				//byte[] mac = device.getHardwareAddress();
				if (mac == null) {
					continue; // Interface doesn't have a hardware address
				}
				// System.out.println("Checking for device " + asString(mac) +
				// " in old list");
				for (PcapIf deviceTwo : oldNics) {
					//byte[] macTwo = deviceTwo.getHardwareAddress();
					String macTwo = deviceTwo.getName();
					if (macTwo != null) {
						//if (asString(mac).equals(asString(macTwo))) {
						if (mac.equals(macTwo)) {	
							foundInOld = true;
							// System.out.println("Found in old!");
						}
					}
				}

				if (!foundInOld) {
					// System.out.println("Not found in old list the device " +
					// asString(device.getHardwareAddress()));
					if (!device.getName().equals("nflog")
							&& !device.getName().equals("any") && !device.getName().equals("bluetooth0") 
							&& !device.getName().equals("nfqueue") && !device.getName().equals("lo")) {
						addedNics.add(device);
						// System.out.println("I am about to add the Nic with name .."
						// + device.getName());
					}
				}
			}
		/*} catch (IOException x) {
			x.printStackTrace();
		}/*/
	}

	private void findRemovedNics() {
		//try {
			// System.out.println("Old state has " + oldNics.size() +
			// " devices");
			// System.out.println("New state has " + newNics.size() +
			// " devices");
			removedNics.clear();

			boolean foundInOld = false;

			for (PcapIf device : oldNics) {
				foundInOld = false;
				//byte[] mac = device.getHardwareAddress();
				String mac = device.getName();
				if (mac == null) {
					continue; // Interface doesn't have a hardware address
				}
				// System.out.println("Checking for device " + asString(mac) +
				// " in old list");
				for (PcapIf deviceTwo : newNics) {
					//byte[] macTwo = deviceTwo.getHardwareAddress(); /***********************Here we have problem **************************/
					String macTwo = deviceTwo.getName();
					if (macTwo != null) {
						//if (asString(mac).equals(asString(macTwo))) {
						if (mac.equals(macTwo)) {	
							foundInOld = true;
							// System.out.println("Found in old!");
						}
					}
				}

				if (!foundInOld) {
					//System.out.println("Not found in old list the device " +
					//asString(device.getHardwareAddress()));
					if (!device.getName().equals("nflog")
							&& !device.getName().equals("any") && !device.getName().equals("bluetooth0") 
							&& !device.getName().equals("nfqueue") && !device.getName().equals("lo")) {
						removedNics.add(device);
					}
				}
			}
		/*} catch (IOException x) {
			x.printStackTrace();
		}*/
	}
}