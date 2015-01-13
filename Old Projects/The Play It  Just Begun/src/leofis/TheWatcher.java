package leofis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.nio.BufferUnderflowException;

public class TheWatcher extends Thread {

	Hashtable<String, Integer> ipFreq;
	Hashtable<String, Integer> patternFreq;
	ArrayList<String> maliciousIPs;
	ArrayList<String> maliciousPatterns;
	Pcap pcap;

	/***************************************************/
	PcapIf device;
	Tcp tcp = new Tcp();
	Udp udp = new Udp();
	Ip4 ip = new Ip4();
	Payload payload = new Payload();
	String myIP;
	
	byte[] dIP = new byte[4];
	byte[] sIP = new byte[4];

	/***************************************************/

	public TheWatcher(Hashtable<String, Integer> ipFreq,
			Hashtable<String, Integer> patternFreq,
			ArrayList<String> maliciousIPs,
			ArrayList<String> maliciousPatterns, PcapIf device) {
		this.ipFreq = ipFreq;
		this.patternFreq = patternFreq;
		this.maliciousIPs = maliciousIPs;
		this.maliciousPatterns = maliciousPatterns;
		this.device = device;
	}

	public byte[] getHwAddress() {
		byte[] mac = null;
		try {
			mac = device.getHardwareAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mac;
	}
	
	public String getHwName() {
		String mac = null;
		mac = device.getName();
		return mac;
	}

	public void killTheWatcher() {
		try {
			pcap.breakloop();
		} catch (Exception e) {

		}
	}

	public void run() {

		System.out.println("I am " + device.getName()+ " and i was just connected !");
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		/***************************************************************************
		 * Second we open up the selected device
		 **************************************************************************/

		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = ThreadPlay.wtWatcher * 1000; // 10 seconds in millis
		pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}

		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {
				if (findIP()) {
					try {
						// System.out.println("I am here and i cannot stop it "+
						// device.getName());
						PcapPacket threadPacket = new PcapPacket(packet);
						if (threadPacket.hasHeader(ip)) {
							if ((threadPacket.hasHeader(tcp) && (threadPacket.getHeader(tcp) != null))
									|| (threadPacket.hasHeader(udp) && (threadPacket.getHeader(udp) != null))) {
								/****** Index out of bounds ******/
								if (ip.destination() != null) {
									
									dIP = ip.destination(); /****** null pointer exception ******/
									sIP = ip.source();
									String sourceIP = FormatUtils.ip(sIP);
									String destinationIP = FormatUtils.ip(dIP);

									String payString = null;

									if (threadPacket.hasHeader(payload)&& ip.hasPayload()) {
										/****** Index out of bounds ******/
										byte[] ba = payload.getByteArray(0,payload.size());
										payString = new String(ba);
									}

									ArrayList<String> maliciousPattern = new ArrayList<String>();
									if (payString != null) {
										synchronized (maliciousPatterns) {
											for (String patern : maliciousPatterns) {
												Pattern p = Pattern.compile(patern);
												Matcher matcher = p.matcher(payString);

												int count = 0;
												while (matcher.find())
													count++;

												for (int i = 0; i < count; i++)
													maliciousPattern.add(patern);
											}
										}
									}

									// synchronized (patternFreq) {
									for (String string : maliciousPattern) {
										String key = "| " + myIP + " | "+ string;
										synchronized (patternFreq) {
											if (patternFreq.get(key) == null) {
												// key does not already exist
												patternFreq.put(key,new Integer(1));
											} else {
												// key already exists
												Integer value = patternFreq.get(key);
												value++;
												patternFreq.put(key, value);
											}
										}
									}

									String maliciousIP = null;

									synchronized (maliciousIPs) {
										if (maliciousIPs.contains(sourceIP))
											maliciousIP = sourceIP;
									}

									synchronized (maliciousIPs) {
										if (maliciousIPs.contains(destinationIP))
											maliciousIP = destinationIP;
									}

									synchronized (ipFreq) {
										if (maliciousIP != null) {
											String key = "| " + myIP + " | "+ maliciousIP;
											if (ipFreq.get(key) == null) {
												// key does not already exist
												ipFreq.put(key, new Integer(1));
											} else {
												// key already exists
												Integer value = ipFreq.get(key);
												value++;
												ipFreq.put(key, value);
											}
										}
									}
								}
							}
						}
					} catch (IndexOutOfBoundsException | BufferUnderflowException ex) {
						ex.getMessage();
					}
				}
			}
		};

		/***************************************************************************
		 * Fourth we enter the loop and tell it to capture 10 packets. The loop
		 * method does a mapping of pcap.datalink() DLT value to JProtocol ID,
		 * which is needed by JScanner. The scanner scans the packet buffer and
		 * decodes the headers. The mapping is done automatically, although a
		 * variation on the loop method exists that allows the programmer to
		 * sepecify exactly which protocol ID to use as the data link type for
		 * this pcap interface.
		 **************************************************************************/
		//int sign = 0;
		try {
			/*sign =*/ pcap.loop(-1, jpacketHandler, "Capture for " + device.getName()+ " stopped");
		} catch (Exception e) {
		} finally {
			pcap.close();
		}
		
		System.out.println("Graceful exit from " + device.getName());
		//if(sign==0)System.out.println("The count expired");
		//if(sign==-1)System.out.println("Error in pcap loop");
		//if(sign==-2)System.out.println("Exit after a break.loop command");
	}

	private boolean findIP() {

		InputStreamReader input = null;
		BufferedReader reader = null;
		String line = null;
		String ip = "inet addr:";
		int counter = 0;

		/*
		 * try { Thread.sleep(5000); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */

		try {
			Process p = Runtime.getRuntime().exec(
					"ifconfig " + device.getName());
			input = new InputStreamReader(p.getInputStream());
			reader = new BufferedReader(input);

			while ((line = reader.readLine()) != null) {
				if (counter == 1) {
					if (line.contains(ip)) {
						String part = line.substring(20, 35);
						// System.out.println(part+ " The Part!");
						int spacePos = part.indexOf(" ");
						String partTwo = part.substring(0, spacePos);
						// System.out.println(device.getName()+" "+partTwo);
						myIP = device.getName() + " " + partTwo;
					} else {
						// System.out.println("No IP is given to "+
						// device.getName());
						return false;
					}
				}
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}