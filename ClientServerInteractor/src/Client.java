/* 
 * DS (Distributed Systems) Lab-1
 * Name : Amitesh Mathur
 * Student ID : 1001563299 and NetID : axm3299
 * Parts of the code have been used and referenced from the following web pages:
 *  1)https://github.com/kgole/SocketProgramming-Thesaurus/tree/master/src
 *  2)http://cs.lmu.edu/~ray/notes/javanetexamples/
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Client {
	BufferedReader in;
	PrintWriter out;
	JFrame jframe = new JFrame("Client");
	JTextField jtextField = new JTextField(50);
	JTextArea jtextarea = new JTextArea(18, 60);
	Map<String, String> conclients = new HashMap<String, String>(); // HashMap to store the Clients registered with the Server

	public Client() // Constructor to initialize the Client 
	{ 
		jtextarea.setEditable(false);
		jtextField.setEditable(false);
		jframe.getContentPane().add(jtextField, "South");
		jframe.getContentPane().add(new JScrollPane(jtextarea), "Center");
		jframe.pack();
	}


	private String getServerIP()//Method to get the IP address of the server you want to connect too.
	{
		return JOptionPane.showInputDialog(jframe, "Please enter the IP Address of the Server:", "Welcome",JOptionPane.QUESTION_MESSAGE);
	}

	
	private String getUname() //Method to take input the client's name.
	{
		return JOptionPane.showInputDialog(jframe, "Please enter your preffered name:", "Name",JOptionPane.PLAIN_MESSAGE);
	}

	
	
	// run method to start communication between client and the server by passing IP address and port.number.
	private void run() throws IOException, ParseException {
		String serverAddress = getServerIP(); // To get the IP address from getServerIP method
		Socket socket = new Socket(serverAddress, 9090); // To initialize a new socket connection
		in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // instance of Buffer Reader for accepting the messages coming from the server.
		out = new PrintWriter(socket.getOutputStream(), true); // instance of PrintWriter for sending the messages to the client.
		DateTimeFormatter dtfd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Used to get the accurate date and time.
		String tDate = dtfd.format(LocalDateTime.now());
		System.out.println(tDate);
		jtextField.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) { 
				// Get the data
				out.println(jtextField.getText()+",Host:"+serverAddress+",User-Agent:Chrome/51.0,Content-Type:text,Content-Length:"+Integer.toString(jtextField.getText().length())+",Date:"+tDate);
				jtextField.setText("");
			}
		});
		
		String sName=null;
		// Separating and choosing messages from user based upon names of the connected clients
		try 
		{
			while (true) 
			{
				
				String str = in.readLine();
				System.out.println(str);
				if (str.startsWith("SUBMITNAME")) 
				{
					out.println(getUname()); // Send the desired screen name to the server for acceptance
				}
				// //Server checks name acceptance 
				else if (str.startsWith("NAMEACCEPTED")) 
				{
					jtextField.setEditable(true);
					 sName = str.split(",")[1];
					 jtextarea.append("   " + sName + "\nStart sending messages\n");
				} else if (str.startsWith("REDUNDANT")) 
				{
					JOptionPane.showMessageDialog(null, "Name already exist! Please choose another name");
				} else if (str.contains("disconnected")) //disconnected clients
				{
					jtextarea.append(str + "\n");		
				} else if (str.contains("connected")) 
				{
					jtextarea.append(str + "\n");		//Connected clients
				}
				// When name accepted and receives a message from server the block below is executed.
				else if (str.startsWith("MESSAGE")) 
				{
					String[] msg = str.split(",");
					jtextarea.append(msg[1]+ "::" +msg[2] + "\n");
				
				} 
			}
		} catch (Exception e1) {
			socket.close();
			jtextarea.append("The Server is now offline, the communication with other clients is cut too!");
		}
	}

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.jframe.setVisible(true);
		client.run();
	}
}