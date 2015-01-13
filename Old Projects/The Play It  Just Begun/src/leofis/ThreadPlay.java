package leofis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import server.LeofisWebServiceImplementationServiceLocator;
import server.LeofisWebServiceInterface;

public class ThreadPlay {
	
	static int wtSendGenericID=0, wtBetrayer = 0,wtDeparted = 0,wtWatcher = 0,wtNicWatcher = 0,loop = 0;
	private static String destination , port , servicename , wsdl ;
	
    public static void main(String[] args) {
   
    	getPropValues();
    	
        CountDownLatch t1 = new CountDownLatch(1);
        SendGenericId sendGenericId = new SendGenericId(t1);
        Thread TaskOne = new Thread (sendGenericId);
        TaskOne.start();
        
        try {
                t1.await();
            }
        catch (Exception exc) {
            System.out.println(exc);
        }
        
        if (SendGenericId.nodeID == null) {
        	System.out.println("Server communication Error. Computer couldn't be registered.");
        	System.exit(-1);
        }
        
        CountDownLatch t2 = new CountDownLatch(1);
        final TheBetrayer theBetrayer =new TheBetrayer(t2);
        
        Thread TaskTwo = new Thread (theBetrayer);
        TaskTwo.setName("theBetrayer");
        TaskTwo.start();
        
        try {
                t2.await();
            }
        catch (Exception exc) {
            System.out.println(exc);
        }
        
        CountDownLatch t3 = new CountDownLatch(1);
        TheDeparted theDeparted = new TheDeparted(t3);
        
        theDeparted.maliciousIPs = theBetrayer.maliciousIPs;
        theDeparted.maliciousPatterns = theBetrayer.maliciousPatterns;
        
        Thread TaskThree = new Thread (theDeparted);
        TaskThree.setName("theDeparted");
        TaskThree.start();
        
        try {
                t3.await();
            }
        catch (Exception exc) {
            System.out.println(exc);
        }
   
        CountDownLatch t4 = new CountDownLatch(1);
        NicWatcher nicWatcher = new NicWatcher(t4);
        
        nicWatcher.ipFreq = theBetrayer.ipFreq;
        nicWatcher.patternFreq = theBetrayer.patternFreq;
        nicWatcher.maliciousIPs = theBetrayer.maliciousIPs;
        nicWatcher.maliciousPatterns = theBetrayer.maliciousPatterns;
        
        Thread TaskFour = new Thread (nicWatcher);
        TaskFour.setName("nicWatcher");
        TaskFour.start();
        
        try {
                t4.await();
            }
        catch (Exception exc) {
            System.out.println(exc);
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	System.out.println();
            	System.out.println("Interrupted");
            	System.out.println();
            	
            	sendGenericId.iAmDead = true;
            	theBetrayer.iAmDead = true;
            	theDeparted.filefound = true;
                theDeparted.iAmDead = true;
                nicWatcher.iAmDead = true;
                
                try {
        			TaskOne.join();
        		} catch (InterruptedException e) {
        		}
                
                try {
        			TaskTwo.join();
        		} catch (InterruptedException e) {
        		}
                        
                try {
        			TaskThree.join();
        		} catch (InterruptedException e) {
        		}
                
                try {
        			TaskFour.join();
        		} catch (InterruptedException e) {
        		}
                
                LeofisWebServiceInterface iface = ThreadPlay.webInterfaceInit();
				try {
					iface.unregister(SendGenericId.nodeID);
				} catch (RemoteException e) {
					System.out.println("Error. Server Communication Stopped abduantly.");
				}
                
                System.out.println("The Main Thread Finished.. All good.!");
            }
           }); 
    }
    
    public static void getPropValues() 
	{
		Properties properties = null;
		try {
			File file = new File("./config.properties");
			FileInputStream fileInput = new FileInputStream(file);
			properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			try {
	    		
				wtSendGenericID = Integer.parseInt(properties.getProperty("wtSendGenericID"));
				wtBetrayer = Integer.parseInt(properties.getProperty("wtBetrayer"));
	    		wtDeparted = Integer.parseInt(properties.getProperty("wtDeparted"));
	    		wtNicWatcher = Integer.parseInt(properties.getProperty("wtNicWatcher"));
	    		wtWatcher = Integer.parseInt(properties.getProperty("wtWatcher"));
	    		loop = Integer.parseInt(properties.getProperty("loop"));
	    		
	    		destination = properties.getProperty("destination");
				port = properties.getProperty("port");
				servicename = properties.getProperty("servicename");
				wsdl = properties.getProperty("wsdl");
	    		
	    		
	    	} catch (Exception x)
	    	{
	    		System.out.println("-----------------------------------------------------------------");
	    		System.out.println("You must enter the frequency values in the properties file.");
	    		System.out.println("-----------------------------------------------------------------");
	    		System.exit(-1);
	    	}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println("-----------------------------------------------------------------");
			System.out.println("Properties file wasn't found. Place it outside the execution jar.");
			System.out.println("-----------------------------------------------------------------");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Error while closing Properties file.");
		}
	}
    
    public static LeofisWebServiceInterface webInterfaceInit()
    {
    	LeofisWebServiceImplementationServiceLocator locator;
		LeofisWebServiceInterface iface = null;
		
		// "http://127.0.0.1:9999/LeofisService/LeofisService?WSDL"
		String uri = "http://"+destination+":"+port+"/"+servicename+"/"+wsdl;
		QName qname = new QName("http://server/", "LeofisWebServiceImplementationService");
		
	    try {
	    	locator = new LeofisWebServiceImplementationServiceLocator(uri, qname);
	    	iface = locator.getLeofisWebServiceImplementationPort();
	    	
		} catch (ServiceException x) {
			System.out.println("Connection is valid but cannot be enstablished.");
		}
		return iface;
    }
}