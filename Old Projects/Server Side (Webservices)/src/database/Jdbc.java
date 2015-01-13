package database;

import gui.Gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.table.DefaultTableModel;

import crypto.Encryptor;
import server.AvailableNodes;
import server.MaliciousPatterns;
import server.StatisticalReports;

public class Jdbc {
	private String jdbcDriver;
	private String jdbcURL;
	private String jdbcURLCreate;
	private String username;
	private String password;
	private String action;

	Connection connection = null;
	PreparedStatement statement = null;

	private SupportMemory memory = new SupportMemory();

	public Jdbc() {
		getPropValues();
		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException ex) {
			System.out.println("Error: Unable to load driver class!");
			System.exit(1);
		}

		try {
			if (action.equals("create")) {
				connection = DriverManager.getConnection(jdbcURLCreate, username,password);
				createDatabase();
				connection.close();
				connection = DriverManager.getConnection(jdbcURL, username,password);
			} else if(action.equals("init")) {
				connection = DriverManager.getConnection(jdbcURL, username,password);
				initValues();
			}

		} catch (SQLException e) {
			System.out.println("Error: Check database property file for right credentials.");
			System.exit(1);
		}
	}

	private void initValues() {
		ResultSet resultSet = null;
		ResultSet resultSetTwo = null;
		ResultSet resultSetThree = null;
		ResultSet resultSetFour = null;
		
		try {
			
			String sql_query = "select * from Computer";
			statement =  connection.prepareStatement(sql_query);
			resultSet = statement.executeQuery(sql_query);
			while (resultSet.next())memory.genericIDs.add(resultSet.getString(2));
			
			sql_query = "select * from MaliciousIP";
			statement =  connection.prepareStatement(sql_query);
			resultSetTwo = statement.executeQuery(sql_query);
			while (resultSetTwo.next())memory.listIP.add(resultSetTwo.getString(2));
			
			sql_query = "select * from MaliciousPattern";
			statement =  connection.prepareStatement(sql_query);
			resultSetThree = statement.executeQuery(sql_query);
			while (resultSetThree.next())memory.listPattern.add(resultSetThree.getString(2));
			
			sql_query = "select * from Admin";
			statement =  connection.prepareStatement(sql_query);
			resultSetFour = statement.executeQuery(sql_query);
			while (resultSetFour.next())memory.usersAndroid.add(resultSetFour.getString(2));

		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (resultSetTwo != null)
				try {
					resultSetTwo.close();
				} catch (SQLException ignore) {
				}
			if (resultSetThree != null)
				try {
					resultSetThree.close();
				} catch (SQLException ignore) {
				}
			if (resultSetFour != null)
				try {
					resultSetFour.close();
				} catch (SQLException ignore) {
				}
		}
	}

	public synchronized boolean checkGenericID(String nodeID) {
		
		if (nodeID.contains(" ")) return false;
		
		ResultSet resultSet = null;
		boolean result = true;
		try {
			
			//if(!memory.liveIDs.contains(nodeID)) memory.liveIDs.add(nodeID);
			memory.liveIDsPartTwo.put(nodeID, System.currentTimeMillis()/1000);
			String sql_query = "select * from Computer";
			statement =  connection.prepareStatement(sql_query);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				if (nodeID.equals(resultSet.getString(2))) {
					result = false;
					break;
				}
			}
			statement.close();

			if (result) {
				sql_query = "insert into Computer (genericID) values (?)";
				statement =  connection.prepareStatement(sql_query);
				statement.setString(1, nodeID);
				statement.executeUpdate();
				memory.genericIDs.add(nodeID);
			}
			statement.close();
		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return result;
	}

	public synchronized boolean deleteGenericID(String nodeID) {
		
		ResultSet resultSet = null;
		boolean result = false;

		try {
			if (memory.genericIDs.contains(nodeID)) {
				
				String sql_query = "select idComputer from Computer where genericID = ? ";
				statement = connection.prepareStatement(sql_query);
				statement.setString(1, nodeID);
				resultSet = statement.executeQuery();
				resultSet.next();
				int i = resultSet.getInt(1);
				statement.close();
				
				sql_query = "delete from MaliciousIPCount where Computer_idComputer = ? ";
				statement =  connection.prepareStatement(sql_query);
				statement.setInt(1, i);
				statement.executeUpdate();
				statement.close();
				
				sql_query = "delete from MaliciousPatternCount where Computer_idComputer = ? ";
				statement = connection.prepareStatement(sql_query);
				statement.setInt(1, i);
				statement.executeUpdate();
				statement.close();
				
				sql_query = "delete from Computer where genericID = ? ";
				statement = connection.prepareStatement(sql_query);
				statement.setString(1, nodeID);
				statement.executeUpdate();
				statement.close();

				memory.genericIDs.remove(nodeID);
				//if (memory.liveIDs.contains(nodeID)) memory.liveIDs.remove(nodeID);
				if (memory.liveIDsPartTwo.containsKey(nodeID)) memory.liveIDsPartTwo.remove(nodeID);
				result = true;
			}
		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return result;
	}
	
	public synchronized boolean removeGenericID(String nodeID) {
		
		if (memory.liveIDsPartTwo.containsKey(nodeID)) 
		{
			memory.liveIDsPartTwo.remove(nodeID);
			return true;
		}
		else return false;
	}

	public synchronized MaliciousPatterns checkMaliciousList(String nodeID) {

		MaliciousPatterns malipate = new MaliciousPatterns();

		ResultSet resultSetOne = null;
		ResultSet resultSetTwo = null;
		String key = nodeID;

		try {

			if (memory.memoryIP.get(key) == null) {
				memory.memoryIP.put(key, new Integer(0));
			}
			
			String sql_query = "select * from MaliciousIP where idMaliciousIP > ?";
			statement = connection.prepareStatement(sql_query);
			statement.setInt(1, memory.memoryIP.get(key));
			resultSetOne = statement.executeQuery();
			
			Integer value = memory.memoryIP.get(key);
			while (resultSetOne.next()) {
				malipate.addIP((resultSetOne.getString(2)));
				value++;
			}
			statement.close();
			
			memory.memoryIP.put(key, value);

			if (memory.memoryPattern.get(key) == null) {
				memory.memoryPattern.put(key, new Integer(0));
			}
			
			sql_query = "select * from MaliciousPattern where idMaliciousPattern > ?";
			statement = connection.prepareStatement(sql_query);
			statement.setInt(1, memory.memoryPattern.get(key));
			resultSetTwo = statement.executeQuery();
			
			value = memory.memoryPattern.get(key);
			while (resultSetTwo.next()) {
				malipate.addPa(resultSetTwo.getString(2));
				value++;
			}
			statement.close();
			memory.memoryPattern.put(key, value);

		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSetOne != null)
				try {
					resultSetOne.close();
				} catch (SQLException ignore) {
				}
			if (resultSetTwo != null)
				try {
					resultSetTwo.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return malipate;
	}
	
	public synchronized void sendStatistical(String nodeID,StatisticalReports reports) {
				
		if (reports.getLiveInterfaces().entrySet().size() >= 1)
		{
			for (Map.Entry<String, String> entry : reports.getLiveInterfaces().entrySet()) {
				memory.getLiveInterfaces().put(entry.getKey(), entry.getValue());
			}
			return;
		}

		ResultSet resultSetOne = null;
		ResultSet resultSetTwo = null;
		ResultSet resultSetThree = null;

		try {
			
			if (reports.getIpFreq().entrySet().size() >= 1) {
				for (Map.Entry<String, Integer> entry : reports.getIpFreq().entrySet()) {
					String key = entry.getKey();
					Integer value = entry.getValue();
					ArrayList<String> oneByOne = constructFields(key);
					
					String sql_query = "select idComputer from Computer where genericID = ?";
					statement = connection.prepareStatement(sql_query);
					statement.setString(1, nodeID);
					resultSetOne = statement.executeQuery();
					resultSetOne.next();
					
					int coID;
					if (resultSetOne.last()) coID = resultSetOne.getInt(1);
					else coID = 0;
					statement.close();
					
					sql_query = "select idMaliciousIP from MaliciousIP where name = ?";
					statement = connection.prepareStatement(sql_query);
					statement.setString(1, oneByOne.get(2));
					resultSetTwo = statement.executeQuery();
					resultSetTwo.next();
					
					int maID;
					if (resultSetTwo.last()) maID = resultSetTwo.getInt(1);
					else maID = 0;
					statement.close();
					
					if (coID!=0 && maID!=0)
					{
						sql_query = "select * from MaliciousIPCount where Computer_idComputer = ? and interfaceName = ? and InterfaceIP = ? and MaliciousIP_idMaliciousIP = ?";
						statement = connection.prepareStatement(sql_query);
						statement.setInt(1, coID);
						statement.setString(2, oneByOne.get(0));
						statement.setString(3, oneByOne.get(1));
						statement.setInt(4, maID);
						resultSetThree = statement.executeQuery();
						
						if (resultSetThree.next())
							{
								sql_query = "update MaliciousIPCount set count = ? where Computer_idComputer = ? and interfaceName = ? and InterfaceIP = ? and MaliciousIP_idMaliciousIP = ?";
								statement = connection.prepareStatement(sql_query);
								statement.setInt(1, value+resultSetThree.getInt(5));
								statement.setInt(2, coID);
								statement.setString(3, oneByOne.get(0));
								statement.setString(4, oneByOne.get(1));
								statement.setInt(5, maID);
								statement.executeUpdate();
								statement.close();
							}
						else 
							{
								sql_query = "insert into MaliciousIPCount (Computer_idComputer,interfaceName,InterfaceIP,MaliciousIP_idMaliciousIP,count) values (?,?,?,?,?)";
								statement = connection.prepareStatement(sql_query);
								statement.setInt(1, coID);
								statement.setString(2, oneByOne.get(0));
								statement.setString(3, oneByOne.get(1));
								statement.setInt(4, maID);
								statement.setInt(5, value);
								statement.executeUpdate();
								statement.close();
							}
					}
				}
			}
			

			if (reports.getPatternFreq().entrySet().size() >= 1) {
				for (Map.Entry<String, Integer> entry : reports
						.getPatternFreq().entrySet()) {
					String key = entry.getKey();
					Integer value = entry.getValue();
					ArrayList<String> oneByOne = constructFields(key);
					
					String sql_query = "select idComputer from Computer where genericID = ?";
					statement = connection.prepareStatement(sql_query);
					statement.setString(1, nodeID);
					resultSetOne = statement.executeQuery();
					resultSetOne.next();
					
					int coID;
					if (resultSetOne.last()) coID = resultSetOne.getInt(1);
					else coID = 0;
					statement.close();
					
					sql_query = "select idMaliciousPattern from MaliciousPattern where name = ?";
					statement = connection.prepareStatement(sql_query);
					statement.setString(1, oneByOne.get(2));
					resultSetTwo = statement.executeQuery();
					resultSetTwo.next();
					
					int maID;
					if (resultSetTwo.last()) maID = resultSetTwo.getInt(1);
					else maID = 0;
					statement.close();

					if (coID!=0 && maID!=0)
					{
						sql_query = "select * from MaliciousPatternCount where Computer_idComputer = ? and interfaceName = ? and InterfaceIP = ? and MaliciousPattern_idMaliciousPattern = ?";
						statement = connection.prepareStatement(sql_query);
						statement.setInt(1, coID);
						statement.setString(2, oneByOne.get(0));
						statement.setString(3, oneByOne.get(1));
						statement.setInt(4, maID);
						resultSetThree = statement.executeQuery();
						
						if (resultSetThree.next())
							{
								sql_query = "update MaliciousPatternCount set count = ? where Computer_idComputer = ? and interfaceName = ? and InterfaceIP = ? and MaliciousPattern_idMaliciousPattern = ?";
								statement = connection.prepareStatement(sql_query);
								statement.setInt(1, value+resultSetThree.getInt(5));
								statement.setInt(2, coID);
								statement.setString(3, oneByOne.get(0));
								statement.setString(4, oneByOne.get(1));
								statement.setInt(5, maID);
								statement.executeUpdate();
								statement.close();
							}
						else
							{
								sql_query = "insert into MaliciousPatternCount (Computer_idComputer,interfaceName,InterfaceIP,MaliciousPattern_idMaliciousPattern,count) values (?,?,?,?,?)";
								statement = connection.prepareStatement(sql_query);
								statement.setInt(1, coID);
								statement.setString(2, oneByOne.get(0));
								statement.setString(3, oneByOne.get(1));
								statement.setInt(4, maID);
								statement.setInt(5, value);
								statement.executeUpdate();
								statement.close();
							}
					}
				}
			}

		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSetOne != null)
				try {
					resultSetOne.close();
				} catch (SQLException ignore) {
				}
			if (resultSetTwo != null)
				try {
					resultSetTwo.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
	}
	
	public synchronized int checkLogin(String username,String password)
	{
		ResultSet resultSet = null;
		int result = 0;
		
		try {
			
			String sql_query = "select * from Admin where username = ?";
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			while(resultSet.next())
				{
					if (!password.equals(resultSet.getString(3))) result = 0;
					else if (resultSet.getBoolean(4)) result = 1;
					else result = 2;
				}

			statement.close();
			
			memory.usersAndroid.add(username);
			
		}catch(SQLException x){x.printStackTrace();}
		finally
		{
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return result;
	}
	
	public synchronized boolean checkLogout(String username, String password)
	{
		
		ResultSet resultSet = null;
		boolean result = false;
		
		try {
			String sql_query = "select * from Admin where username = ?";
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) if (!password.equals(resultSet.getString(3))) result = false;
			statement.close();
		}catch(SQLException x){x.printStackTrace();}
		finally
		{
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		
		if (memory.usersAndroid.contains(username))
		{
			memory.usersAndroid.remove(username);
			result = true;
		}
		return result;
	}
	
	public synchronized boolean checkAndroidDevice(String username, String password,AvailableNodes nodes)
	{
		if (username.contains(" ") || password.contains(" ") || nodes == null || (nodes.getManagedPCs()==null)) return false;
		
		List<String> managedPCs =  Arrays.asList(nodes.getManagedPCs().split(" "));
		
		ResultSet resultSet = null;
		ResultSet resultSetTwo = null;
		ResultSet resultSetThree = null;
		ArrayList<Integer> tempComputerIDs = new ArrayList<>();
		
		int size = managedPCs.size();
		int count = 0;
		try {
			String sql_query = "select * from Admin";
			statement = connection.prepareStatement(sql_query);
			resultSet = statement.executeQuery();
			while (resultSet.next())
			{
				if(username.equals(resultSet.getString(2))) return false;
			}			
			statement.close();
			
			sql_query = "select * from Computer";
			statement =  connection.prepareStatement(sql_query);
			resultSetTwo = statement.executeQuery();
			while (resultSetTwo.next()) {
				for (int i=0; i < managedPCs.size(); i++)
				{
					if (managedPCs.get(i).equals(resultSetTwo.getString(2))) {
						if (resultSetTwo.getString(3)!=null) return false; /* My precious ..! */
						count++;
						tempComputerIDs.add(resultSetTwo.getInt(1));
					}
				}
			}
			statement.close();

			if (size == count) {
				
				sql_query = "insert into Admin (username , password,userType) values (?,?,?)";
				statement = connection.prepareStatement(sql_query);
				statement.setString(1, username);
				statement.setString(2, password);
				statement.setBoolean(3, false);
				statement.executeUpdate();
				statement.close();
				
				sql_query = "select * from Admin where username = ?";
				statement = connection.prepareStatement(sql_query);
				statement.setString(1, username);
				resultSetThree = statement.executeQuery();
				int adminID = -1;
				while (resultSetThree.next())  adminID = resultSetThree.getInt(1);
				for ( int i=0; i <tempComputerIDs.size(); i++)
				{
					sql_query = "update Computer set Admin_idAdmin = ? where idComputer = ?";
					statement = connection.prepareStatement(sql_query);
					statement.setInt(1, adminID);
					statement.setInt(2, tempComputerIDs.get(i));
					statement.executeUpdate();
					statement.close();
				}
			}
			else return false;
		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (resultSetTwo != null)
				try {
					resultSetTwo.close();
				} catch (SQLException ignore) {
				}
			if (resultSetThree != null)
				try {
					resultSetThree.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return true;
	}

	public synchronized List<StatisticalReports> getStatisticalMobile(String username,String password)
	{
		List<StatisticalReports> statistical = null;
		
		ResultSet resultSet = null;
		ResultSet resultSetTwo = null;
		ResultSet resultSetThree = null;
		ResultSet resultSetFour = null;
		@SuppressWarnings("unused")
		int admindID = -1; /* because now it takes all the malicious instead of per admin */
		
		try {
			
			String sql_query = "select * from Admin where username = ?";
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
				{
					if (!password.equals(resultSet.getString(3))) return null;
					admindID = resultSet.getInt(1);
				}
			statement.close();
		
			statistical = new ArrayList<>();
			
			sql_query = "select * from Computer";/* where Admin_idAdmin = ?";*/
			statement = connection.prepareStatement(sql_query);
			//statement.setInt(1, admindID);
			resultSetTwo = statement.executeQuery();
			
			//Statement statementTwo = connection.createStatement();
			while(resultSetTwo.next()) 
			{
				StatisticalReports report = new StatisticalReports();

				Hashtable<String, Integer> ipFreq = new Hashtable<>();
				Hashtable<String, Integer> paFreq = new Hashtable<>();
				
				int pcID = resultSetTwo.getInt(1);
				
				//sql_query = "select * from MaliciousIPCount where Computer_idComputer =  ?";
				sql_query = "select Computer.genericID,MaliciousIPCount.InterfaceName, "
						+ "MaliciousIPCount.InterfaceIP,MaliciousIP.name,MaliciousIPCount.count from MaliciousIPCount Inner Join "
						+ "MaliciousIP on MaliciousIPCount.MaliciousIP_idMaliciousIP = MaliciousIP.idMaliciousIP Inner Join "
						+ "Computer on MaliciousIPCount.Computer_idComputer=Computer.idComputer "
						+ "where Computer.idComputer = ?";
				statement = connection.prepareStatement(sql_query);
				statement.setInt(1, pcID);
				resultSetThree = statement.executeQuery();
				
				while(resultSetThree.next()) 
				{
					ipFreq.put(resultSetThree.getString(1)+" | " +resultSetThree.getString(2)+" | "+resultSetThree.getString(3) +" | "+resultSetThree.getString(4)+" | ", resultSetThree.getInt(5));
				}
				statement.close();
				
				//sql_query = "select * from MaliciousPatternCount where Computer_idComputer =  ?";
				sql_query = "select Computer.genericID,MaliciousPatternCount.InterfaceName, "
						+ "MaliciousPatternCount.InterfaceIP,MaliciousPattern.name,MaliciousPatternCount.count from MaliciousPatternCount Inner Join "
						+ "MaliciousPattern on MaliciousPatternCount.MaliciousPattern_idMaliciousPattern = MaliciousPattern.idMaliciousPattern Inner Join "
						+ "Computer on MaliciousPatternCount.Computer_idComputer=Computer.idComputer "
			 			+ "where Computer.idComputer = ?";		
				statement = connection.prepareStatement(sql_query);
				statement.setInt(1, pcID);
				resultSetFour= statement.executeQuery();
				
				while(resultSetFour.next()) 
				{
					paFreq.put(resultSetFour.getString(1)+" | " +resultSetFour.getString(2)+" | "+resultSetFour.getString(3) +" | "+resultSetFour.getString(4)+" | ", resultSetFour.getInt(5));
				}
				statement.close();
				
				report.setIpFreqS(ipFreq.toString());
				report.setPatternFreqS(paFreq.toString());
				report.setLiveInterfacesS(memory.getLiveInterfaces().toString());
				
				report.setIpFreq(ipFreq);
				report.setPatternFreq(paFreq);
				report.setLiveInterfaces(memory.getLiveInterfaces());
				
				statistical.add(report);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (resultSetTwo != null)
				try {
					resultSetTwo.close();
				} catch (SQLException ignore) {
				}
			if (resultSetThree != null)
				try {
					resultSetThree.close();
				} catch (SQLException ignore) {
				}
			if (resultSetFour != null)
				try {
					resultSetFour.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return statistical;
	}
	
	public synchronized String getMaliciousMobile(String username, String password)
	{
		ResultSet resultSet = null;
		try {
			
			String sql_query = "select * from Admin where username = ?";
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) if (!password.equals(resultSet.getString(3))) return null;
			statement.close();
			
		}catch(SQLException x){x.printStackTrace();}
		finally
		{
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		
		String maliciousString = "";
		
		for (int i=0; i< memory.listIP.size(); i++ )
		{
			maliciousString = maliciousString + memory.listIP.get(i) + " ";
		}
		
		maliciousString = maliciousString +  " <|> ";
		
		for (int i=0; i< memory.listPattern.size(); i++ )
		{
			maliciousString = maliciousString  + memory.listPattern.get(i) + " ";
		}
		
		byte[] decodedKey = Base64.getDecoder().decode("95iFzT0xmGc=");
		SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
		
		try {
			
			Encryptor encrypter = new Encryptor(key);
			return encrypter.encrypt(maliciousString);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized boolean sendMaliciousMobile(String username, String password, String malliciousIP, String malliciousPattern)
	{
		ResultSet resultSet = null;
		boolean result = false;
		
		try {
			
			String sql_query = "select * from Admin where username = ?";
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) if (!password.equals(resultSet.getString(3))) return result;
			statement.close();
		}catch(SQLException x){x.printStackTrace();}
		finally
		{
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		/*Probably if we have string of strings splitter will be useful*/
		if(!malliciousIP.isEmpty())
			{
				addMaliciousPattern(malliciousIP, 'i');
				result = true;
			}
		if(!malliciousPattern.isEmpty())
			{
				addMaliciousPattern(malliciousPattern, 'w');
				result = true;
			}
		return result;
	}

	private ArrayList<String> constructFields(String value) {
		String[] array = value.split(" | ");
		ArrayList<String> oneByOne = new ArrayList<>();
		for (String ss : array) {
			if (!ss.contains("|"))
				oneByOne.add(ss);
		}
		return oneByOne;
	}

	public synchronized void addMaliciousPattern(String pattern, char c)
	{
		if (pattern.contains(" ")) return;
		
		ResultSet resultSet = null;
		boolean result = true;
		try {
			
			if ( c == 'i') 
				{
					String sql_query = "select * from MaliciousIP";
					statement = connection.prepareStatement(sql_query);
					resultSet = statement.executeQuery();
				}
			else if ( c == 'w')
				{
					String sql_query = "select * from MaliciousPattern";
					statement = connection.prepareStatement(sql_query);
					resultSet = statement.executeQuery();
				}
			else return;
			
			// the addition of list came later so we can just check the lists */
			//if (memory.listIP.contains(pattern) || memory.listPattern.contains(pattern)) return false;
		
			while (resultSet.next()) {
				if (pattern.equals(resultSet.getString(2))) {
					result = false;
					break;
				}
			}

			if (result) {
				statement.close();
				if ( c == 'i')
				{
					String sql_query = "insert into MaliciousIP (name) values (?)";
					statement = connection.prepareStatement(sql_query);
					statement.setString(1, pattern);
					statement.executeUpdate();
					memory.listIP.add(pattern);
				}
				else
				{
					String sql_query = "insert into MaliciousPattern (name) values (?)";
					statement = connection.prepareStatement(sql_query);
					statement.setString(1, pattern);
					statement.executeUpdate();
					memory.listPattern.add(pattern);
				}
			}
			
		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
	}
	
	public synchronized ArrayList<String> perCoMalicious(String nodeID, char c)
	{
		ArrayList<String> returnedList = null;
		ResultSet resultSetOne = null;
		try {
			if (memory.genericIDs.contains(nodeID))
			{
				if(c=='i' && memory.memoryIP.get(nodeID)!=null)
				{
					returnedList = new ArrayList<>();
					String sql_query = "select name from MaliciousIP where idMaliciousIP <= ?";
					statement = connection.prepareStatement(sql_query);
					statement.setInt(1, memory.memoryIP.get(nodeID));
					resultSetOne = statement.executeQuery();
					while(resultSetOne.next()) returnedList.add(resultSetOne.getString(1));
					statement.close();
				}
				else if (c=='w' && memory.memoryPattern.get(nodeID)!=null)
				{
					returnedList = new ArrayList<>();
					String sql_query = "select name from MaliciousPattern where idMaliciousPattern <= ?";
					statement = connection.prepareStatement(sql_query);
					statement.setInt(1, memory.memoryPattern.get(nodeID));
					resultSetOne = statement.executeQuery();
					while(resultSetOne.next()) returnedList.add(resultSetOne.getString(1));
					statement.close();
				}
			}
		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (resultSetOne != null)
				try {
					resultSetOne.close();
				} catch (SQLException ignore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
		}
		return returnedList;
	}
	
	public synchronized ArrayList<String> liveComputers()
	{
		Hashtable<String, Long> tempHashTable = new Hashtable<>();
		tempHashTable.putAll(memory.liveIDsPartTwo);
		for (String key : memory.liveIDsPartTwo.keySet()) {
			if(memory.liveIDsPartTwo.get(key)+30 < System.currentTimeMillis()/1000) tempHashTable.remove(key);
		}
		memory.liveIDsPartTwo.clear();
		memory.liveIDsPartTwo.putAll(tempHashTable);
		ArrayList<String> returnedList = new ArrayList<String>(tempHashTable.keySet());
		
		for (String key : memory.liveInterfaces.keySet()) {
			String[] parts = key.split(" ");
			if (!returnedList.contains(parts[0]))
			{
				if(!memory.liveInterfaces.get(key).contains("has been shutted down."))
					memory.liveInterfaces.put(key, "closed abduantly !");
			}
		}
		
		return returnedList;
	}
	
	public synchronized DefaultTableModel guiResultSetQuery(char c ,String genericID) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		String sql_query = null;
		
		try {
			connection = DriverManager.getConnection(jdbcURL, username,password);
			
			if (c=='1')
				{
					sql_query = "select Computer.genericID,MaliciousPatternCount.InterfaceName, "
							+ "MaliciousPatternCount.InterfaceIP,MaliciousPattern.name,MaliciousPatternCount.count from MaliciousPatternCount Inner Join "
							+ "MaliciousPattern on MaliciousPatternCount.MaliciousPattern_idMaliciousPattern = MaliciousPattern.idMaliciousPattern Inner Join "
							+ "Computer on MaliciousPatternCount.Computer_idComputer=Computer.idComputer";
					statement =  connection.prepareStatement(sql_query);
				}
			else if (c=='2')
				{
				sql_query = "select Computer.genericID,MaliciousIPCount.InterfaceName, "
						+ "MaliciousIPCount.InterfaceIP,MaliciousIP.name,MaliciousIPCount.count from MaliciousIPCount Inner Join "
						+ "MaliciousIP on MaliciousIPCount.MaliciousIP_idMaliciousIP = MaliciousIP.idMaliciousIP Inner Join "
						+ "Computer on MaliciousIPCount.Computer_idComputer=Computer.idComputer";
					statement =  connection.prepareStatement(sql_query);
				}
			else if (c=='3') 
				{
				sql_query = "select Computer.genericID,MaliciousIPCount.InterfaceName, "
						+ "MaliciousIPCount.InterfaceIP,MaliciousIP.name,MaliciousIPCount.count from MaliciousIPCount Inner Join "
						+ "MaliciousIP on MaliciousIPCount.MaliciousIP_idMaliciousIP = MaliciousIP.idMaliciousIP Inner Join "
						+ "Computer on MaliciousIPCount.Computer_idComputer=Computer.idComputer "
						+ "where Computer.genericID = '"+genericID+"'";
					statement =  connection.prepareStatement(sql_query);
					//statement.setString(1, genericID);
				}
			else if (c=='4')
				{
				sql_query = "select Computer.genericID,MaliciousPatternCount.InterfaceName, "
						+ "MaliciousPatternCount.InterfaceIP,MaliciousPattern.name,MaliciousPatternCount.count from MaliciousPatternCount Inner Join "
						+ "MaliciousPattern on MaliciousPatternCount.MaliciousPattern_idMaliciousPattern = MaliciousPattern.idMaliciousPattern Inner Join "
						+ "Computer on MaliciousPatternCount.Computer_idComputer=Computer.idComputer "
			 			+ "where Computer.genericID = '"+genericID+"'";
					statement =  connection.prepareStatement(sql_query);
					//statement.setString(1, genericID);
				}

			resultSet = statement.executeQuery(sql_query);
			
			ResultSetMetaData metaData = resultSet.getMetaData();
			
		    Vector<String> columnNames = new Vector<String>();
		    int columnCount = metaData.getColumnCount();
		    for (int column = 1; column <= columnCount; column++) {
		    	if(!metaData.getColumnName(column).contains("name"))
		        columnNames.add(metaData.getColumnName(column));
		    	else columnNames.add("Malicious");
		    }

		    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		    while (resultSet.next()) {
		        Vector<Object> vector = new Vector<Object>();
		        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
		            vector.add(resultSet.getObject(columnIndex));
		        }
		        data.add(vector);
		    }
		    return new DefaultTableModel(data, columnNames);
		    
		} catch (SQLException x) {
			x.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException ignore) {
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException ignore) {
				}
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignore) {
				}
		}
		return null;
	}
		
	public void visualizeDatabase()
	{
		new Gui(this);
 	}
	
	public SupportMemory getMemory() {
		return memory;
	}
	
	public void closeDB() {
		if (statement != null)
			try {
				statement.close();
			} catch (SQLException ignore) {
			}
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException ignore) {
			}
		System.out.println("Closing Connection with the Database.");
	}
	
 	private void getPropValues() {
		Properties properties = null;
		try {
			File file = new File("./database.properties");
			FileInputStream fileInput = new FileInputStream(file);
			properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			try {
				username = properties.getProperty("username");
				password = properties.getProperty("password");
				jdbcDriver = properties.getProperty("jdbcDriver");
				jdbcURL = properties.getProperty("jdbcURL");
				jdbcURLCreate = properties.getProperty("jdbcURLCreate");
				action = properties.getProperty("action");

			} catch (Exception x) {
				System.out
						.println("-----------------------------------------------------------------");
				System.out
						.println("You must enter the frequency values in the properties file.");
				System.out
						.println("-----------------------------------------------------------------");
				System.exit(-1);
			}
		} catch (FileNotFoundException e) {
			System.out
					.println("-----------------------------------------------------------------");
			System.out
					.println("Properties file wasn't found. Place it outside the execution jar.");
			System.out
					.println("-----------------------------------------------------------------");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Error while closing Properties file.");
		}
	}
 	
 	private void createDatabase()
	{
 		Statement statementCreate = null;
 		
		String sql_query =
				"SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;"+
				"SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;"+
				"SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';"+

				"CREATE SCHEMA IF NOT EXISTS `nsa_di` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;"+
				"USE `nsa_di` ;"+

				"DROP TABLE IF EXISTS `nsa_di`.`MaliciousIP` ;"+

				"CREATE  TABLE IF NOT EXISTS `nsa_di`.`MaliciousIP` ( "+
				" `idMaliciousIP` INT NOT NULL AUTO_INCREMENT ,"+
				" `name` VARCHAR(45) NOT NULL ,"+
				" PRIMARY KEY (`idMaliciousIP`) )"+
				"ENGINE = InnoDB;"+

				"DROP TABLE IF EXISTS `nsa_di`.`MaliciousPattern` ;"+

				"CREATE  TABLE IF NOT EXISTS `nsa_di`.`MaliciousPattern` ("+
				" `idMaliciousPattern` INT NOT NULL AUTO_INCREMENT ,"+
				" `name` VARCHAR(45) NOT NULL ,"+
				" PRIMARY KEY (`idMaliciousPattern`) )"+
				"ENGINE = InnoDB;"+

				"DROP TABLE IF EXISTS `nsa_di`.`Admin` ;"+

				"CREATE  TABLE IF NOT EXISTS `nsa_di`.`Admin` ("+
				"`idAdmin` INT NOT NULL AUTO_INCREMENT ,"+
				"`username` VARCHAR(45) NOT NULL ,"+
				"`password` VARCHAR(45) NOT NULL ,"+
				"`userType` TINYINT(1) NOT NULL ,"+
				" PRIMARY KEY (`idAdmin`) )"+
				"ENGINE = InnoDB;"+

				"DROP TABLE IF EXISTS `nsa_di`.`Computer` ;"+

				"CREATE  TABLE IF NOT EXISTS `nsa_di`.`Computer` ("+
				"  `idComputer` INT NOT NULL AUTO_INCREMENT ,"+
				"  `genericID` VARCHAR(45) NOT NULL ,"+
				"  `Admin_idAdmin` INT NULL ,"+
				"  PRIMARY KEY (`idComputer`) ,"+
				"  INDEX `fk_Computer_Admin1` (`Admin_idAdmin` ASC) ,"+
				"  CONSTRAINT `fk_Computer_Admin1`"+
				"    FOREIGN KEY (`Admin_idAdmin` )"+
				"    REFERENCES `nsa_di`.`Admin` (`idAdmin` )"+
				"    ON DELETE NO ACTION"+
				"    ON UPDATE NO ACTION)"+
				"ENGINE = InnoDB;"+

				"DROP TABLE IF EXISTS `nsa_di`.`MaliciousIPCount` ;"+

				"CREATE  TABLE IF NOT EXISTS `nsa_di`.`MaliciousIPCount` ("+
				"  `Computer_idComputer` INT NOT NULL ,"+
				"  `interfaceName` VARCHAR(45) NOT NULL ,"+
				"  `InterfaceIP` VARCHAR(45) NOT NULL ,"+
				"  `MaliciousIP_idMaliciousIP` INT NOT NULL ,"+
				"  `count` INT NOT NULL ,"+
				"  PRIMARY KEY (`Computer_idComputer`, `interfaceName`, `InterfaceIP`, `MaliciousIP_idMaliciousIP`) ,"+
				"  INDEX `fk_InterfaceIP_has_MaliciousIP_MaliciousIP1` (`MaliciousIP_idMaliciousIP` ASC) ,"+
				"  INDEX `fk_MaliciousIPCount_Computer1` (`Computer_idComputer` ASC) ,"+
				"  CONSTRAINT `fk_InterfaceIP_has_MaliciousIP_MaliciousIP1`"+
				"    FOREIGN KEY (`MaliciousIP_idMaliciousIP` )"+
				"    REFERENCES `nsa_di`.`MaliciousIP` (`idMaliciousIP` )"+
				"    ON DELETE NO ACTION"+
				"    ON UPDATE NO ACTION,"+
				"  CONSTRAINT `fk_MaliciousIPCount_Computer1`"+
				"    FOREIGN KEY (`Computer_idComputer` )"+
				"    REFERENCES `nsa_di`.`Computer` (`idComputer` )"+
				"    ON DELETE NO ACTION"+
				"    ON UPDATE NO ACTION)"+
				"ENGINE = InnoDB;"+

				"DROP TABLE IF EXISTS `nsa_di`.`MaliciousPatternCount` ;"+

				"CREATE  TABLE IF NOT EXISTS `nsa_di`.`MaliciousPatternCount` ("+
				 " `Computer_idComputer` INT NOT NULL ,"+
				 " `InterfaceName` VARCHAR(45) NOT NULL ,"+
				 " `InterfaceIP` VARCHAR(45) NOT NULL ,"+
				 " `MaliciousPattern_idMaliciousPattern` INT NOT NULL ,"+
				 " `count` INT NOT NULL ,"+
				 " PRIMARY KEY (`Computer_idComputer`, `InterfaceName`, `InterfaceIP`, `MaliciousPattern_idMaliciousPattern`) ,"+
				 " INDEX `fk_MaliciousPattern_has_InterfaceIP_MaliciousPattern1` (`MaliciousPattern_idMaliciousPattern` ASC) ,"+
				 " INDEX `fk_InterfaceIPCount_Computer1` (`Computer_idComputer` ASC) ,"+
				 " CONSTRAINT `fk_MaliciousPattern_has_InterfaceIP_MaliciousPattern1`"+
				 "   FOREIGN KEY (`MaliciousPattern_idMaliciousPattern` )"+
				 "   REFERENCES `nsa_di`.`MaliciousPattern` (`idMaliciousPattern` )"+
				 "   ON DELETE NO ACTION"+
				 "   ON UPDATE NO ACTION,"+
				 " CONSTRAINT `fk_InterfaceIPCount_Computer1`"+
				 "   FOREIGN KEY (`Computer_idComputer` )"+
				 "   REFERENCES `nsa_di`.`Computer` (`idComputer` )"+
				 "   ON DELETE NO ACTION"+
				 "   ON UPDATE NO ACTION)"+
				"ENGINE = InnoDB;"+



				"SET SQL_MODE=@OLD_SQL_MODE;"+
				"SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;"+
				"SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;";
				
			 try {
				 statementCreate = connection.createStatement();
				 connection.setAutoCommit(false);
				 String[] queries = sql_query.split(";");
				 for (String oneQuery : queries) {					 
					 statementCreate.addBatch(oneQuery);
				 }
				 statementCreate.executeBatch();
				 connection.commit();
				 
			} catch (SQLException x) { x.printStackTrace();
			}finally
			{
				if (statementCreate!= null)
					try {
						statementCreate.close();
					} catch (SQLException ignore) {
					}
			}
	}
}
