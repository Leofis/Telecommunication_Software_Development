package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import server.LeofisWebServiceImplementation;

public class ServerMain {
	
	private static String broadcast , port , servicename ;

    public static void main(String[] args) {
    	
    	getPropValues();
    	final LeofisWebServiceImplementation lws = new LeofisWebServiceImplementation();
    	
    	//http://127.0.0.1:9999/LeofisService/LeofisService?WSDL
    	final Endpoint point = Endpoint.publish("http://"+broadcast+":"+port+"/"+servicename+"/", lws);
        
        System.out.println("Web service published to "+broadcast+" in port : "+ port);
        
        lws.getDatabase().visualizeDatabase();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	lws.getDatabase().closeDB();
            	System.out.println();
            	System.out.println("Database successfully closed.");
            	try {
            		point.stop();
            		System.out.println("Endpoint successfully closed.");
            	} catch (Exception ex) {
            		System.out.println("Endpoint couldn't be closed.");
            	}
            	System.out.println("Graceful exit from Server. All good.!");
            }
           }); 
    }
    
 	private static final void getPropValues() {
		Properties properties = null;
		try {
			File file = new File("./server.properties");
			FileInputStream fileInput = new FileInputStream(file);
			properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			try {
				broadcast = properties.getProperty("broadcast");
				port = properties.getProperty("port");
				servicename = properties.getProperty("servicename");

			} catch (Exception x) {
				System.out.println("No values in server.properties file.");
				System.exit(-1);
			}
		} catch (FileNotFoundException e) {
			System.out.println("No Property File Found. Server");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Error while closing Properties file.");
		}
	}
}