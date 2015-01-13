package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import database.Jdbc;

public class Gui extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField textFieldOne;
	private JTextField textFieldTwo;
	private JTextField textFieldThree;
	private JTextField textmali;
	private JTextField textlive;
	
	private JTextField textOnepa;
	private JTextField textOnepi;
	
	private JTabbedPane allTabs;
	
	private JPanel panelOne;
	private JPanel panelTwo;
	private JPanel panelThree;
	
	private JLabel labelOne;
	private JLabel labelTwo;
	
	final Jdbc database;
	
	public Gui(final Jdbc database ) {
		
		this.database = database;
		
		setLayout(new BorderLayout());
		
		allTabs = new JTabbedPane();
								
		panelOne = new JPanel();
		panelOne.setLayout(null);
		
		panelTwo = new JPanel();
		panelTwo.setLayout(null);
		
		panelThree = new JPanel();
		panelThree.setLayout(null);		
		
		
		JLabel mainlabel = new JLabel("Add Malicious Patterns & IPs : ");
		mainlabel.setBounds(125, 10, 300, 15);
		mainlabel.setForeground(Color.DARK_GRAY);
		panelOne.add(mainlabel);
		
		labelOne = new JLabel("");
		panelOne.add(labelOne);
		
		labelTwo = new JLabel("");
		panelTwo.add(labelTwo);
		
		/*--------------------- the first button ------------------------*/
		JButton buttonOne = new JButton("Add Malicious IP");
		buttonOne.setActionCommand("ip");		
		buttonOne.setBounds(5, 60, 189, 29);
		buttonOne.addActionListener(new Action());
		panelOne.add(buttonOne);
		
		textFieldOne = new JTextField(15);
		textFieldOne.setBounds(200, 60, 260, 30);
		panelOne.add(textFieldOne);
		
		/*---------------------- the second button -----------------------*/
		
		JButton buttonTwo = new JButton("Add Malicious Pattern");
		buttonTwo.setActionCommand("pattern");
		buttonTwo.setBounds(5, 120, 189, 29);
		buttonTwo.addActionListener(new Action());
		panelOne.add(buttonTwo);
		
		textFieldTwo = new JTextField(15);
		textFieldTwo.setBounds(200, 120, 260, 30);
		panelOne.add(textFieldTwo);
		
		/*------------------------ the third button ----------------------*/
		
		JButton maliper = new JButton("Show Malicious per ID :");
		maliper.setActionCommand("allmali");		
		maliper.setBounds(5, 187, 220, 20);
		maliper.addActionListener(new Action());
		panelOne.add(maliper);
		
		textmali = new JTextField(15);
		textmali.setBounds(240, 187, 221, 21);
		panelOne.add(textmali);
		
		JButton buttonThree = new JButton("All Mallicious IP");
		buttonThree.setActionCommand("showip");
		buttonThree.setBounds(5, 210, 220, 20);
		buttonThree.addActionListener(new Action());
		panelOne.add(buttonThree);
		
		JButton buttonFour = new JButton("All Mallicious Patterns");
		buttonFour.setActionCommand("showma");
		buttonFour.setBounds(240, 210, 220, 20);
		buttonFour.addActionListener(new Action());
		panelOne.add(buttonFour);
		
		/***** Second Panel *****/
		JButton buttonFive = new JButton("Show Registered Computers");
		buttonFive.setActionCommand("showco");		
		buttonFive.setBounds(55, 5, 350, 20);
		buttonFive.addActionListener(new Action());
		panelTwo.add(buttonFive);
		
		JButton livecomp = new JButton("Show LIVE Computers");
		livecomp.setActionCommand("liveco");		
		livecomp.setBounds(55, 123, 350, 20);
		livecomp.addActionListener(new Action());
		panelTwo.add(livecomp);
		
		JButton showInter = new JButton("Show Interfaces per ID :");
		showInter.setActionCommand("live");		
		showInter.setBounds(1, 220, 230, 20);
		showInter.addActionListener(new Action());
		panelTwo.add(showInter);
		
		textlive = new JTextField(15);
		textlive.setBounds(230, 220, 230, 21);
		panelTwo.add(textlive);
		
		JLabel delabel = new JLabel("ATTENTION : An existing Node can be DELETED . Be Carefull!!!");
		delabel.setBounds(10, 325, 500, 15);
		delabel.setForeground(Color.RED);
		panelTwo.add(delabel);
		
		JButton buttonsix = new JButton("DELETE the Computer :");
		buttonsix.setActionCommand("del");		
		buttonsix.setBounds(1, 350, 220, 20);
		buttonsix.addActionListener(new Action());
		panelTwo.add(buttonsix);
		
		textFieldThree = new JTextField(15);
		textFieldThree.setBounds(230, 350, 230, 21);
		panelTwo.add(textFieldThree);
		
		/***** Third Panel *****/
		
		JButton showallip = new JButton("All IP Statistics :");
		showallip.setActionCommand("showallip");		
		showallip.setBounds(1, 10, 230, 20);
		showallip.addActionListener(new Action());
		panelThree.add(showallip);
		
		JButton showallpa = new JButton("All Pattern Statistics :");
		showallpa.setActionCommand("showallpa");		
		showallpa.setBounds(230, 10, 230, 20);
		showallpa.addActionListener(new Action());
		panelThree.add(showallpa);
		
		JButton showOneip = new JButton("Per ID IP Statistics :");
		showOneip.setActionCommand("showoneip");		
		showOneip.setBounds(1, 40, 230, 20);
		showOneip.addActionListener(new Action());
		panelThree.add(showOneip);
		
		textOnepi = new JTextField(15);
		textOnepi.setBounds(230, 40, 230, 21);
		panelThree.add(textOnepi);
		
		JButton showOnepa = new JButton("Per ID Patterns Statistics :");
		showOnepa.setActionCommand("showonepa");		
		showOnepa.setBounds(1, 70, 230, 20);
		showOnepa.addActionListener(new Action());
		panelThree.add(showOnepa);
		
		textOnepa = new JTextField(15);
		textOnepa.setBounds(230, 70, 230, 21);
		panelThree.add(textOnepa);
		
		allTabs.addTab("Malicious Patterns & IPs", panelOne);
		allTabs.addTab("Registered Computers", panelTwo);
		allTabs.addTab("Statistics", panelThree);
		
		add(allTabs, BorderLayout.CENTER);
		
		initializeWindow();
	}
	
	private  void initializeWindow ()
	{
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(468, 450);		
		this.setTitle("Managment");
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private class Action implements ActionListener 
	{
		public void actionPerformed(ActionEvent action)
		{
			String command = action.getActionCommand();
				
			if( command.equals("ip"))  {
		            String ip = textFieldOne.getText();
		            if ((!ip.isEmpty()) && (!ip.contains(" "))){
		            database.addMaliciousPattern(ip, 'i');
		            labelOne.setBounds(201,98, 300, 15);
		            labelOne.setForeground(Color.BLUE);
					labelOne.setText("IP : "+ textFieldOne.getText()+" added");
					panelOne.add(labelOne);
					textFieldOne.setText("");
		         }
	         }
	         else if( command.equals("pattern") )  {
	        	 	String pattern = textFieldTwo.getText();
	        	 	if (!pattern.isEmpty() && (!pattern.contains(" "))){
	        	 	database.addMaliciousPattern(pattern, 'w');
	        	 	labelOne.setBounds(201,160, 300, 15);
		            labelOne.setForeground(Color.BLUE);
					labelOne.setText("Pattern : "+ textFieldTwo.getText()+" added");
					panelOne.add(labelOne);
					textFieldTwo.setText("");
	        	 	}
	         }
	         else if( command.equals("showip") )  {
	        	
	        	ArrayList<String> ips = database.getMemory().getListIP();
	        	String dataOne[] = (String[]) ips.toArray(new String[ips.size()]);
	        	
	     		JList<String> myList = new JList<>(dataOne);
	     		JScrollPane scrollableList = new JScrollPane(myList);
	     		scrollableList.setBounds(5, 235, 220, 160);
	     		panelOne.add(scrollableList);
	     		
	     		panelOne.revalidate();
	        	 
	         }
	         else if( command.equals("showma") )  {
	        	
	        	ArrayList<String> pas = database.getMemory().getListPattern();
		        String dataTwo[] = (String[]) pas.toArray(new String[pas.size()]);
	        	
	     		JList<String> myList = new JList<>(dataTwo);
	     		JScrollPane scrollableList = new JScrollPane(myList);
	     		scrollableList.setBounds(240, 235, 220, 160);
	     		panelOne.add(scrollableList);
	     		
	     		panelOne.revalidate();
	         }
			
	         else if( command.equals("allmali") )  {
	        	 
	        	 
	        	 
        	 		String nodeID = textmali.getText();
        	 		if (nodeID!=null)
        	 		{
	        	 		ArrayList<String> ips = new ArrayList<>();
	        	 		ArrayList<String> pas = new ArrayList<>();
	        	 		
			        	ips = database.perCoMalicious(nodeID, 'i');
			        	pas = database.perCoMalicious(nodeID, 'w');
			        	
			        	if (ips!=null)
			        	{
			        		String dataOne[] = (String[]) ips.toArray(new String[ips.size()]);
			        		JList<String> myList = new JList<>(dataOne);
				     		JScrollPane scrollableList = new JScrollPane(myList);
				     		scrollableList.setBounds(5, 235, 220, 160);
				     		panelOne.add(scrollableList);
			        	}
			        	else
			        	{
			        		String dataOne[] = {"------           NO IP YET          -------"};
			        		JList<String> myList = new JList<>(dataOne);
				     		JScrollPane scrollableList = new JScrollPane(myList);
				     		scrollableList.setBounds(5, 235, 220, 160);
				     		panelOne.add(scrollableList);
			        	}
			        	if (ips!=null)
			        	{
				        	String dataTwo[] = (String[]) pas.toArray(new String[pas.size()]);
				        	JList<String> myListTwo = new JList<>(dataTwo);
				     		JScrollPane scrollableListTwo = new JScrollPane(myListTwo);
				     		scrollableListTwo.setBounds(240, 235, 220, 160);
				     		panelOne.add(scrollableListTwo);
			        	}else
			        	{
			        		String dataTwo[] = {"-----      NO PATTERN YET     ------"};
				        	JList<String> myListTwo = new JList<>(dataTwo);
				     		JScrollPane scrollableListTwo = new JScrollPane(myListTwo);
				     		scrollableListTwo.setBounds(240, 235, 220, 160);
				     		panelOne.add(scrollableListTwo);
			        	}
			     		panelOne.revalidate();
        	 		}
		         }
			
	         else if( command.equals("showco") )  {
		        	
		        	ArrayList<String> pas = database.getMemory().getGenericIDs();
			        String data[] = (String[]) pas.toArray(new String[pas.size()]);
		        	
		     		JList<String> myList = new JList<>(data);
		     		JScrollPane scrollableList = new JScrollPane(myList);
		     		scrollableList.setBounds(55, 27, 350, 90);
		     		panelTwo.add(scrollableList);
		     		panelTwo.revalidate();
		         }
	         else if( command.equals("liveco") )  {
		        	
		        	ArrayList<String> pas = database.liveComputers();
			        String data[] = (String[]) pas.toArray(new String[pas.size()]);
		        	
		     		JList<String> myList = new JList<>(data);
		     		JScrollPane scrollableList = new JScrollPane(myList);
		     		scrollableList.setBounds(55, 145, 350, 70);
		     		panelTwo.add(scrollableList);
		     		panelTwo.revalidate();
		         }
			
	         else if( command.equals("live") )  {
		        	
	        	 	boolean exists = false;
	        	 	String liveID = textlive.getText();
	        	 	//textlive.setText("");
	        	 	
	        	 	if (!liveID.equals("") && !liveID.contains(" ")){
		        	 	ArrayList<String> devices = new ArrayList<>();
		        	 	synchronized (database.getMemory()) {
							Hashtable<String, String> livedevices = database.getMemory().getLiveInterfaces();
							for (Map.Entry<String, String> entry : livedevices.entrySet()) {
								String key = entry.getKey();
								if (key.contains(liveID))
								{
									String[] parts = key.split(" ");
									devices.add("Interface "+parts[1]+" is currently " +entry.getValue());
									exists = true;
								}
							}
						}
		        	 	
		        	 	if(exists)
				        {
		        	 		String data[] = (String[]) devices.toArray(new String[devices.size()]);
			        	
		        	 		JList<String> myList = new JList<>(data);
		        	 		JScrollPane scrollableList = new JScrollPane(myList);
		        	 		scrollableList.setBounds(1, 245, 460, 70);
		        	 		panelTwo.add(scrollableList);
		        	 		panelTwo.revalidate();
				        }
	        	 	}
	        	 	else 
	        	 	{
	        	 		String[] data = {"The Computer is Offline or doesn't exist in our Database."};

	        	 		JList<String> myList = new JList<>(data);
	        	 		JScrollPane scrollableList = new JScrollPane(myList);
	        	 		scrollableList.setBounds(1, 245, 460, 70);
	        	 		panelTwo.add(scrollableList);
	        	 		panelTwo.revalidate();
	        	 		//labelTwo.setBounds(10,250, 450, 15);
		        	 	//labelTwo.setForeground(Color.RED);
		        	 	//labelTwo.setText("The Computer is Offline or doesn't exist in our Database.");
						//panelTwo.add(labelTwo);
	        		}
		         }
			
	         else if( command.equals("del") )  {
		        	
	        	 	String pattern = textFieldThree.getText();
	        	 	
	        	 	if (database.deleteGenericID(pattern)){
		        	 	
		        	 	labelTwo.setBounds(10,382, 450, 15);
		        	 	labelTwo.setForeground(Color.RED);
		        	 	labelTwo.setText("The "+ textFieldThree.getText()+" just deleted.");
						panelTwo.add(labelTwo);
						textFieldThree.setText("");
	        	 	}else
	        	 	{
	        	 		labelTwo.setBounds(10,382, 450, 15);
	        	 		labelTwo.setForeground(Color.RED);
	        	 		labelTwo.setText("Error code: 404. The Computer doesn't exists in our database.");
						panelTwo.add(labelTwo);
						textFieldThree.setText("");
	        	 	}
		         }
			
	         else if( command.equals("showallpa") )  {
		        	
	        	 	JTable table = new JTable(database.guiResultSetQuery('1',null));
	        	 	
	        	 	JScrollPane panel = new JScrollPane(table);
	        	 	panel.setBounds(1, 100, 460, 300);
	        		panelThree.add(panel);
		         }
			
	         else if( command.equals("showallip") )  {
		        	
	        	 	JTable table = new JTable(database.guiResultSetQuery('2',null));

	        	 	JScrollPane panel = new JScrollPane(table);
	        	 	panel.setBounds(1, 100, 460, 300);
	        		panelThree.add(panel);
		         }
			
	         else if( command.equals("showoneip") )  {
		        	
	        	 	String genericID = textOnepi.getText();
	        	 	JTable table = new JTable(database.guiResultSetQuery('3',genericID));

	        	 	JScrollPane panel = new JScrollPane(table);
	        	 	panel.setBounds(1, 100, 460, 300);
	        	 	//textOnepi.setText("");
	        		panelThree.add(panel);
		         }
			
	         else if( command.equals("showonepa") )  {
		        	
	        	 	String genericID = textOnepa.getText();
	        	 	JTable table = new JTable(database.guiResultSetQuery('4',genericID));

	        	 	JScrollPane panel = new JScrollPane(table);
	        	 	panel.setBounds(1, 100, 460, 300);
	        	 	//textOnepa.setText("");
	        		panelThree.add(panel);
		         }
		}
	}
}