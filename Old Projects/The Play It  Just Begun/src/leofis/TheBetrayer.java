package leofis;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import server.LeofisWebServiceInterface;
import server.StatisticalReports;
import server.StatisticalReportsIpFreqEntry;
import server.StatisticalReportsLiveInterfacesEntry;
import server.StatisticalReportsPatternFreqEntry;

public class TheBetrayer implements Runnable {
	public volatile boolean iAmDead = false;
	CountDownLatch thirdTask;
	
	int period = 0;

	ArrayList<String> maliciousIPs;
	ArrayList<String> maliciousPatterns;
	Hashtable<String, Integer> ipFreq;
	Hashtable<String, Integer> patternFreq;

	public TheBetrayer(CountDownLatch thirdTask) {
		this.thirdTask = thirdTask;
	}

	public void run() {
		maliciousIPs = new ArrayList<String>();
		maliciousPatterns = new ArrayList<String>();
		patternFreq = new Hashtable<String, Integer>();
		ipFreq = new Hashtable<String, Integer>();

		System.out.println("I am the Betrayer. Sending Statistics :");

		thirdTask.countDown();
		
		while (!iAmDead)
		{
			try {
				Thread.sleep((long)ThreadPlay.wtBetrayer*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (iAmDead) {
				System.out.println();
				System.out.println("Betrayer : Sending last reports ! Won't be long ...");
				System.out.println();
			}
			
			StatisticalReportsLiveInterfacesEntry[] liveInterfaces = new StatisticalReportsLiveInterfacesEntry[0];
			
			StatisticalReportsIpFreqEntry[] ipFr = new StatisticalReportsIpFreqEntry[ipFreq.size()];
			if (ipFreq.entrySet().size() >= 1)
			{
		
				int i = 0;
				for (Map.Entry<String, Integer> entry : ipFreq.entrySet()) {
					ipFr[i] = new StatisticalReportsIpFreqEntry();
					ipFr[i].setKey(entry.getKey());
					ipFr[i].setValue(entry.getValue());
					i++;
				}
			}
			
			StatisticalReportsPatternFreqEntry[] paFr = new StatisticalReportsPatternFreqEntry[patternFreq.size()];
			if (patternFreq.entrySet().size()>=1)
			{
		
				int i = 0;
				for (Map.Entry<String, Integer> entry : patternFreq.entrySet()) {
					paFr[i] = new StatisticalReportsPatternFreqEntry();
					paFr[i].setKey(entry.getKey());
					paFr[i].setValue(entry.getValue());
					i++;
				}
			}

	    	try {
	    		
	    		StatisticalReports reports = new StatisticalReports();
	    		reports.setIpFreq(ipFr);
		    	reports.setPatternFreq(paFr);
		    	reports.setLiveInterfaces(liveInterfaces);
		    	LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
				iface.maliciousPatternsStatisticalReports(SendGenericId.nodeID, reports);
				
			} catch (RemoteException x) {
				
				try {
					Thread.sleep(15*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("-----------------------------------------------------------------");
				System.out.println("ERROR ! Server Communication Stopped. Reconnecting...");
				System.out.println("-----------------------------------------------------------------");
			}

			period++;
			if (period==ThreadPlay.loop) {clearReports();period = 0;}
		}
		System.out.println("The Betrayer finished successfully.");
	}
	
	private void clearReports(){
		synchronized (ipFreq) {
			ipFreq.clear();
		}
		synchronized (patternFreq) {
			patternFreq.clear();
		}
	  }
}
