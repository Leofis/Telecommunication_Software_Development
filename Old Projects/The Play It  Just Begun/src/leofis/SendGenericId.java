package leofis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import server.LeofisWebServiceInterface;

public class SendGenericId implements Runnable {
	public volatile boolean iAmDead = false;
	CountDownLatch firstTask;
	static String nodeID;

	public SendGenericId(CountDownLatch latch) {
		firstTask = latch;
		// new Thread(this).start();
	}

	public void run() {
		
		getUniqueID();
		
		while (!iAmDead)
		{
			try {
				Thread.sleep((long)ThreadPlay.wtSendGenericID*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
			try {
				iface.register(nodeID);
			} catch (RemoteException e) {
				
			}
		}
		System.out.println("SendGenericID finished successfully.");
	}

	void getUniqueID() {
		
		String string = null;
		Process process;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			process = Runtime.getRuntime().exec("sudo dmidecode -s system-uuid");
			
			isr = new InputStreamReader(process.getInputStream());
			br = new BufferedReader(isr);
			
			System.out.println("Calculating the Unique ID for this computer... :");
			while ((string = br.readLine()) != null) {
				//System.out.println("Calculating the Unique ID for this computer... :");
				System.out.println();
				System.out.println("---- > " + string + " <----");
				System.out.println();
				LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
				if (iface == null) {
					nodeID = null;
				} else {
					iface.register(string);
					nodeID = string;
				}
			}
			process.waitFor();
			if (process.exitValue() == 0) {
				process.destroy();
				firstTask.countDown();
			} else {
				System.out.println("-----------------------------------------------------------------");
				System.out.println("Error, ID wasn't successfully created");
				System.out.println("-----------------------------------------------------------------");
				System.exit(-1);
			}

		} catch (IOException | InterruptedException x) {

		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
