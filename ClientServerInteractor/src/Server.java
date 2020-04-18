/* 
 * DS (Distributed Systems) Lab-1
 * Name : Amitesh Mathur
 * Student ID : 1001563299 and NetID : axm3299
 * Parts of the code have been used and referenced from the following web pages:
 *  1)https://github.com/kgole/SocketProgramming-Thesaurus/tree/master/src
 *  2)http://cs.lmu.edu/~ray/notes/javanetexamples/
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server {
	
	JFrame jframe = new JFrame("Server");
	static JTextArea jtextarea = new JTextArea(70, 70);
	private static ArrayList<String> cnames = new ArrayList<String>(); // ArrayList to store just the list of names of the connected clients
	private static Map<String,PrintWriter> clcon = new HashMap<String,PrintWriter>(); //Hashmap to store the list connections of all the clients
	private static final int PORT = 9090; // The port number on which the server listens to the client
	// Initializing the constructor to create a GUI
	public Server() {
		jtextarea.setEditable(true);
		jframe.getContentPane().add(new JScrollPane(jtextarea), "Center");
		jframe.pack();
		jtextarea.append("\n The Server is now running\n");
	
	}
	

	// The Client Class handles a clients connections with other clients
	private static class Client extends Thread {
		private Socket socket;
		private String cname;
		private BufferedReader in;
		private PrintWriter out;

		// Constructor to start a connection with other client
		public Client(Socket socket) {
			this.socket = socket;
		}

		// Method to give a client their name
		public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
           //While loop runs till a user enters a unique name for a client by making comparisions to the Array list of client names.
                while (true) {
                    out.println("SUBMITNAME");
                    cname = in.readLine();
                    if (cname == null) {
                        return;
                    }
                    if (cname.equals(".*-")) {
	                      break;
	                      }
                    synchronized (cnames) {
                        if (!cnames.contains(cname)) {
                            cnames.add(cname);
                            break;
                        }
                        else 
                        {
                        	out.println("REDUNDANT");
                        }
                    }
                }
                

                out.println("NAMEACCEPTED,"+ cname);  //Here we accept a clients name and add its connection to hashmap.
                System.out.println(out);
                for(PrintWriter writer : clcon.values())
                {
                	writer.println("\n"+ cname + " is now connected\n");
               
                }
                clcon.put(cname,out);
                System.out.println("The client "+ cname +" has now been connected");  
                jtextarea.append(cname + " is connected\n");
                System.out.println(clcon);
         
               // Using the while loop to unicast or broadcast the messages.
                while (true) 
                {
                    String sentence = in.readLine();
                   // writeFile("Content:"+s+"\n");
                    jtextarea.append("Content:"+sentence+"  This is a broadcast  "+"\n");
                    System.out.println(sentence);
                    String[] msg = sentence.split(",");//Array to store users input.
                    System.out.println(msg[0]);
                    if(msg[0].contentEquals("Uni")) //To unicast the message to a particular client mentioned by user.
                    {
                     if(clcon.containsKey(msg[1]))//checking within hashmap.
                    	
                     {
                    	System.out.println("test1");
                    	clcon.get(msg[1]).println("MESSAGE," + cname + "," + msg[2]);             	
                     } 
                     else {
                    	 JOptionPane.showMessageDialog(null," Please enter an existing and connected client's name ");//To let the user enter a valid client name
                     }
                     }
                    else if(msg[0].contentEquals("quit")) //To let a user end the connection and close his window.
                    {
                    	socket.close();
                    }               
                    else if(msg[0].contentEquals("Multi"))//For broadcasting message to all connected clients.
                    {
                    System.out.println(msg);
                    
                    for (PrintWriter writer : clcon.values()) {          		
                        writer.println("MESSAGE," + cname + "," + msg[1]);
                    }}
                    jtextarea.append( "Message from the client  "+ cname + "::" + msg[1]+"\n"); 
                }
            } catch (IOException ex) {
                System.out.println(ex);
            } 
            finally //To close  a connection and disconnect the client and notify the server that the client has been disconnected.
            {
              
                if (cname != null) {
                    cnames.remove(cname);    
                    System.out.println("discons1");
                }
                
                if (out != null) {
                	clcon.remove(out);   
                    for(PrintWriter writer : clcon.values()) {
                    	writer.println(cname + " is disconnected\n");
                    	System.out.println("has been broadcasted");
                    }
                    jtextarea.append(cname + " is disconnected\n");
                	
                    System.out.println("is discon");
                }
                try {
                    socket.close(); //closing a socket connection
                 
                } catch (IOException ex) {
                System.out.println(ex);
                }
            }
        }
    }
	

	// Main method to initialize the server
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.jframe.setVisible(true);
		jtextarea.append("\n Run the Client now, You can run as many clients as you wish but all should have unique names\n");
		jtextarea.append("\n When the client asks for IP address enter 127.0.0.1. And make sure you \n to open this window again to view incoming requests\n");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Client(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}
}