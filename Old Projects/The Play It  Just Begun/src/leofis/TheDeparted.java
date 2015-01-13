package leofis;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import server.LeofisWebServiceInterface;
import server.MaliciousPatterns;

public class TheDeparted implements Runnable {
	public volatile boolean iAmDead = false;
	public volatile boolean filefound = true;
	long wait_time = 3000;
	CountDownLatch secondTask;

	ArrayList<String> maliciousIPs;
	ArrayList<String> maliciousPatterns;

	public TheDeparted(CountDownLatch secondTask) {
		this.secondTask = secondTask;
	}

	public void run() {

		System.out.println("I am the Departed. Fetcing Hazardous IPs and Patterns :");
		secondTask.countDown();

		while (!iAmDead) {

			try {
				Thread.sleep((long) ThreadPlay.wtDeparted * 1000);
			} catch (InterruptedException x) {
				System.out.println("Error during thread sleep. Departed");
			}
									
			try {
				LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
				/**/
				MaliciousPatterns pattern = iface.maliciousPatternRequest(SendGenericId.nodeID);
				if (pattern.getMaliciousIPs() != null) {
					for (int i = 0; i < pattern.getMaliciousIPs().length; i++) {
						synchronized (maliciousIPs) {
							if (!maliciousIPs.contains(pattern.getMaliciousIPs(i)))
								maliciousIPs.add(pattern.getMaliciousIPs(i));
						}
					}
				}
				
				if (pattern.getMaliciousPatterns() != null) {
					for (int i = 0; i < pattern.getMaliciousPatterns().length; i++) {
						synchronized (maliciousPatterns) {
							if(!maliciousPatterns.contains(pattern.getMaliciousPatterns(i)))
								maliciousPatterns.add(pattern.getMaliciousPatterns(i));
						}
					}
				}
			} catch (RemoteException e) {
			}
		}
		System.out.println("The Departed finished successfully.");
	}
}
