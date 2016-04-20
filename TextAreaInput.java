import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextAreaInput {
	static HashMap channelHashMap; /// nika idedu i Hashmapa ir kanala
	static String textInputStr; //tekstui is rasymo fieldo.
	static Socket ircServer=null;
	static BufferedReader is = null;
	static PrintWriter os = null;
	static String fromServer=null;
	static String channel; //kanalo pavadinimu
	static String[] textInputStrArray;
	static String[] textOutputStrArray;
	static String[] privmsgStrArray;
	static String[] actionStrArray;
	static boolean connectedToServer=false;
	static boolean osConnected=false;
	public static void main(String[] args) throws IOException {
		channelHashMap=new HashMap(); /// nika idedu i Hashmapa ir kanala objekta
		// Pagrindiniai langai
		JFrame window = new JFrame("IRC");
		final JFrame confWindow = new JFrame("Configuration");
		JPanel content = new JPanel();
		// tabas kanalams
		final JTabbedPane tabbedPane = new JTabbedPane();
// Vartotjo inicijavimas ------------------------------------------------------------------------------------------------------	
		//Sukuriu vartotoja
		User user = new User();
		
		//Kanalas tarp serverio ir kliento jame taip pat yra teksto laukas kuri talpinu i pagrindini langa.
		Channel main = new Channel();
		
		//Pridedu nauja isokstanti langa kuriame surasomi vartotojo duomenys.
		JButton configurationButton = new JButton("Configuration");
		final JTextField textInputField = new JTextField();
		JPanel confPanel = new JPanel();
		confPanel.setLayout(new GridLayout(14,1));
		final JLabel nickLabel = new JLabel("Nick:");
		confPanel.add(nickLabel);
		
		//Irasau nickname lauka
		final JTextField nickTField = new JTextField();
		nickTField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent evt) {
				
				if (connectedToServer==false) {
					user.setNick(nickTField.getText());
				}
			}	
			public void keyPressed(KeyEvent evt) { }
			public void keyReleased(KeyEvent evt) { 
				user.setNick(nickTField.getText());
			}
		});
		//pridedu i langa nicka
		confPanel.add(nickTField);
		
		
		//Irasau tikraji varda
		final JLabel realNameLabel = new JLabel("Real name:");
		confPanel.add(realNameLabel);
		final JTextField realNameTField = new JTextField();
		realNameTField.addKeyListener(new KeyListener () {
			public void keyTyped(KeyEvent evt) {
				user.setRealName(realNameTField.getText());
			}
			public void keyPressed(KeyEvent evt) { }
			public void keyReleased(KeyEvent evt) { 
				user.setRealName(realNameTField.getText());
			}
		});
		//pridedu i langa tikraji varda
		confPanel.add(realNameTField);
		
		//Irasau serverio adresa
		final JLabel serverLabel = new JLabel("Server:");
		confPanel.add(serverLabel);
		
		final JTextField serverTField = new JTextField();
		serverTField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent evt) {
				user.setServer(serverTField.getText());
			}
			public void keyPressed(KeyEvent evt) { }
			public void keyReleased(KeyEvent evt) {
				user.setServer(serverTField.getText());
			}
		});
		//Pridedu i langa tikraji varda
		confPanel.add(serverTField);
		
		//Pridedu connect mygtuka
		final JButton connectButton = new JButton("Connect");
		//Laukiu vartotojo mygtuko paspaudimo
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String serverStr;
				serverStr=serverTField.getText();
				textInputField.setText("/server " + serverStr);
				textInputField.postActionEvent();
				textInputField.setText("");
			}
		});
		///Pridedu connect mygtuka
		confPanel.add(connectButton);
		
//Jungiuosi prie serverio ir rasau serveriui komandas -----------------------------------------------------------------------
		textInputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//Pasalinu langa configuration
				confWindow.dispose();
				textInputStrArray = new String[3];
				textInputStr=textInputField.getText();
				textInputField.setText("");
				
				textInputStrArray[0]=textInputStr;
				textInputStrArray=textInputStrArray[0].split(" ", 2);
				
				if (textInputStrArray[0].toLowerCase().equals("/server")==true) {
					try {
						ircServer=new Socket(textInputStrArray[1], 6667);
						is = new BufferedReader(new InputStreamReader(ircServer.getInputStream()));
						os = new PrintWriter(ircServer.getOutputStream(), true);
						main.addTextInTextArea("Connected to " + textInputStrArray[1] + "\n");
						osConnected=true;
						nickTField.setEditable(false);
						realNameTField.setEditable(false);
						serverTField.setEditable(false);
						os.println("USER " + user.getNick() + " 0 * :" + user.getRealName() + "\n" );
						os.println("NICK " + user.getNick() + "\n");
					}
					catch (Exception e) {
						main.addTextInTextArea("Could not connect to " + user.getServer() + "\n");
					}
				}
// Komandos siunciamos serveriui -----------------------------------------------------------------------------------------------
				//komanda iseiti is chato
				else if (textInputStrArray[0].toLowerCase().equals("/quit")==true) {
					os.println("QUIT :" + textInputStrArray[1] + "\n");
					main.addTextInTextArea("Quitting: (" + textInputStrArray[1] + ")\n");
				}//komanda /join
				else if (textInputStrArray[0].toLowerCase().equals("/join")==true) {
					os.println("JOIN :" + textInputStrArray[1] + "\n");
					main.addTextInTextArea("Attempting to join " + textInputStrArray[1] + "\n");
					channel = textInputStrArray[1]; // Isaugau kanala
				}// privacios zinutes komanda
				else if(textInputStrArray[0].toLowerCase().equals("/msg")==true){
					 String[] msgStringArray;
					 msgStringArray=new String[5];
					 msgStringArray=textInputStrArray[1].split(" ", 2); //antras param kiek stringu grazint
					 if (channelHashMap.containsKey(msgStringArray[0])==false) {
						 //sukuriu kanala jei tokio nera.
						 channelHashMap.put(msgStringArray[0], new Channel());
						 tabbedPane.addTab(msgStringArray[0], ((Channel) channelHashMap.get(msgStringArray[0])).getChannelPanel());
						 ((Channel) channelHashMap.get(msgStringArray[0])).setIsChannel(false);
					 }
					 os.println("PRIVMSG " + msgStringArray[0] + " :" + msgStringArray[1]);
					 ((Channel) channelHashMap.get(msgStringArray[0])).addTextInTextArea("<" + user.getNick() + "> " + msgStringArray[1] + "\n");
					 
				}//komanda /help atspaudiinu meniu i ekrana
				else if(textInputStrArray[0].toLowerCase().equals("/help")==true){
					main.addTextInTextArea("Display all chat commands: \n");
					main.addTextInTextArea("/quit\n" + "/join [channel name] \n"+
					"/msg [nick] [message content] \n" +  "/part [channel name] \n"+
					"/kick [channel name] [nick] \n"+ "/list \n" + "/admin \n" + "/nick [nick] \n" + " whois [nick] \n"+ "/help \n");
				}//komanda iseiti is pokalbio.
				else if (textInputStrArray[0].toLowerCase().equals("/part")==true) {
					os.println("PART :" + textInputStrArray[1] + "\n");
					if (channelHashMap.containsKey(textInputStrArray[1])==true) {
						//Praeinu pro visus tabus ir sunaikinu taba
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							if (tabbedPane.getTitleAt(i).equals(textInputStrArray[1])==true) {
								tabbedPane.removeTabAt(i);
								channelHashMap.remove(textInputStrArray[1]);
							}
						}
					}
					main.addTextInTextArea("Attempting to part " + textInputStrArray[1] + "\n");
				}//pasiziuriu kanalu sarasa
				else if (textInputStrArray[0].toLowerCase().equals("/list")==true) {
					os.println("LIST \n");
					main.addTextInTextArea("Attempting to display all channels on the server " + textInputStrArray[1] + "\n");
				}// pasiziuriu kas servo adminas
				else if (textInputStrArray[0].toLowerCase().equals("/admin")==true) {
					os.println("ADMIN \n");
					main.addTextInTextArea("Attempting to display admin of server " + textInputStrArray[1] + "\n");
				}
				else if (textInputStrArray[0].toLowerCase().equals("/kick")==true) {
					os.println("KICK " + textInputStrArray[1] + "\n");
					main.addTextInTextArea("Attempting to kick " + textInputStrArray[1] + "\n");
				}//keiciu nika
				else if (textInputStrArray[0].toLowerCase().equals("/nick")==true) {
					os.println("NICK :" + textInputStrArray[1] + "\n");
				}// pasiziuriu varotoja
				else if (textInputStrArray[0].toLowerCase().equals("/whois")==true) {
					os.println("WHOIS " + textInputStrArray[1] + "\n");
				}
				//Siunciu zinute kaip pasirenku taba(neleidziu siusti komandu)
				else if ((textInputStr.charAt(0) != '/') && (tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("Main")==false)) {
					os.println("PRIVMSG " + tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + " :" + textInputStr + "\n");
					((Channel) channelHashMap.get(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()))).addTextInTextArea("<" + user.getNick() + "> " + textInputStr + "\n");
				}
			}
		});
		
	//pridedu scroll bar ir nustatau inputFieldo srifta
		textInputField.setFont(new Font("MonoSpaced", Font.BOLD, 12));
		JScrollPane textAreaScroller = new JScrollPane();
		textAreaScroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
			}
		});
		
//Sudedu visus duom Configuration langa ----------------------------------------------------------------------------------------
		confWindow.setContentPane(confPanel);
		confWindow.setSize(300,300);
		confWindow.setResizable(false);
		confWindow.setLocationRelativeTo(null);
		configurationButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent evt) {
				nickTField.setText(user.getNick());
				realNameTField.setText(user.getRealName());
				serverTField.setText(user.getServer());
				confWindow.setVisible(true);
			}
		});
//Sudedu visus duom pagrindini window  langa----------------------------------------------------------------------------------------
		tabbedPane.addTab("Main", main.getChannelPanel());
		content.setLayout(new BorderLayout());
		content.add(tabbedPane, BorderLayout.CENTER);
		content.add(configurationButton,BorderLayout.NORTH);
		content.add(textInputField, BorderLayout.SOUTH);
		window.setContentPane(content);
		window.setSize(700,500);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		
//Nuskaitineju informacija is serverio--------------------------------------------------------------------------------------------------------
		while (true) {
			try {
				while ((fromServer=is.readLine()) != null) {		//Skaitau info is srauto
					textOutputStrArray=new String[5];
					privmsgStrArray=new String[5];
					textOutputStrArray=fromServer.split(" :", 2);
					if (textOutputStrArray[0].equals("PING")==true) {
						os.println("PONG :" + textOutputStrArray[1] + "\n");
						break;
					}
					//Atsakymas del join komandos (paimti kazkaip vartotoju lista !!!)
					textOutputStrArray=fromServer.split(" ", 6); 
					if ((textOutputStrArray[1].equals("353"))==true) { // 353 arba 366 The following are returned, in sequence, when joining a channel: 
						if (channelHashMap.containsKey(textOutputStrArray[4])==false){ /// nika idedu i Hashmapa ir kanala
							channelHashMap.put(textOutputStrArray[4], new Channel());
							//sukuriu taba nauja idedu tabo pavdinima kanala ir getinu panel ir idedu i taba.
							tabbedPane.addTab(textOutputStrArray[4], ((Channel) channelHashMap.get(textOutputStrArray[4])).getChannelPanel());
							((Channel) channelHashMap.get(textOutputStrArray[4])).setIsChannel(true);
						}
					}
					//Atsakymas del zinutes ne privacios o kanale
					textOutputStrArray=fromServer.split(" ", 4);
					if (textOutputStrArray[1].equals("PRIVMSG")==true){
						//Paimu username kas atsiute zinute.
						privmsgStrArray=textOutputStrArray[0].split("!", 2);
						privmsgStrArray[0]=privmsgStrArray[0].substring(1);
						// pasiziuriu ar egzistuoja toks kanalas.
						if (channelHashMap.containsKey(textOutputStrArray[2])==true){
							//pasiziuriu ar tai zinute
							if (textOutputStrArray[3].startsWith(":")) {
								((Channel) channelHashMap.get(textOutputStrArray[2])).addTextInTextArea("<" + privmsgStrArray[0] + "> " + textOutputStrArray[3].substring(1) + "\n");
							}
						//pasiziureiu ar egzistuoja toks privatus pokalbis	
						}else if (channelHashMap.containsKey(privmsgStrArray[0])==true){
							((Channel) channelHashMap.get(privmsgStrArray[0])).setIsChannel(false);
							((Channel) channelHashMap.get(privmsgStrArray[0])).addTextInTextArea("<" + privmsgStrArray[0] + "> " + textOutputStrArray[3].substring(1) + "\n");
						}else{ // sukuriu taba jei toks pokalbis neegzistuoja
							channelHashMap.put(privmsgStrArray[0], new Channel());
							tabbedPane.addTab(privmsgStrArray[0], (( Channel) channelHashMap.get(privmsgStrArray[0])).getChannelPanel());
							((Channel) channelHashMap.get(privmsgStrArray[0])).setIsChannel(false);
							((Channel) channelHashMap.get(privmsgStrArray[0])).addTextInTextArea("<" + privmsgStrArray[0] + "> " + textOutputStrArray[3].substring(1) + "\n");
							
						}
					}
					//Atsakymas del ismetimo is pokalbio
					textOutputStrArray=fromServer.split(" ",4);
					if (textOutputStrArray[1].equals("KICK")==true){
						if (channelHashMap.containsKey(textOutputStrArray[2])==true) {
							//Praeinu pro visus tabus ir sunaikinu taba
							for (int i = 0; i < tabbedPane.getTabCount(); i++) {
								if (tabbedPane.getTitleAt(i).equals(textOutputStrArray[2])==true) {
									tabbedPane.removeTabAt(i);
									channelHashMap.remove(textOutputStrArray[2]);
								}
							}
						}
					}
					//Atsakymas del nicko
					textOutputStrArray=fromServer.split(" ",3);
					if (textOutputStrArray[1].equals("NICK")==true){
						user.setNick(textOutputStrArray[2].substring(1));
					}
					//spaudinu serverio zinutas
					main.addTextInTextArea(fromServer + "\n");
				}
			}catch(Exception e){
					System.out.println(e);
			}
		}
	}
}
