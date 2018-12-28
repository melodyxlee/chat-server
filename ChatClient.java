package chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * MELODY LEE
 */
public class ChatClient extends ChatWindow {

	// Inner class used for networking
	private Communicator comm;

	// GUI Objects
	private JTextField serverTxt;
	private JTextField nameTxt;
	private JButton connectB;
	private JTextField messageTxt;
	private JButton sendB;

	public ChatClient(){
		super();
		this.setTitle("Chat Client");
		printMsg("Chat Client Started.");

		// GUI elements at top of window
		// Need a Panel to store several buttons/text fields
		serverTxt = new JTextField("localhost");
		serverTxt.setColumns(15);
		nameTxt = new JTextField("Name");
		nameTxt.setColumns(10);
		connectB = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(serverTxt);
		topPanel.add(nameTxt);
		topPanel.add(connectB);
		contentPane.add(topPanel, BorderLayout.NORTH);

		// GUI elements and panel at bottom of window
		messageTxt = new JTextField("");
		messageTxt.setColumns(40);
		sendB = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageTxt);
		botPanel.add(sendB);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// Resize window to fit all GUI components
		this.pack();

		// Setup the communicator so it will handle the connect button
		Communicator comm = new Communicator();
		connectB.addActionListener(comm);
		sendB.addActionListener(comm);

	}

	/** This inner class handles communication with the server. */
	class Communicator implements ActionListener, Runnable{
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		private int port = 2113;

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			if(actionEvent.getActionCommand().compareTo("Connect") == 0) {
				connect();
			}
			else if(actionEvent.getActionCommand().compareTo("Send") == 0) {
			    // Send the message typed in the text box
				sendMsg(messageTxt.getText());
			}
		}

		/** Connect to the remote server and setup input/output streams. */
		public void connect(){
			try {
				socket = new Socket(serverTxt.getText(), port);
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Message displayed on server once client connects
                writer.println(nameTxt.getText() + " has joined");

				// New thread to read messages
				Thread t = new Thread(this);
				t.start();
			}
			catch(IOException e) {
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		/** Receive and display a message */
		public void readMsg() throws IOException {
			String s = reader.readLine();
			printMsg(s);
		}

		/** Send a string */
		public void sendMsg(String s){
		    // Client wants to change name
			if (s.startsWith("/name")) {
				// Get the text after /name
				String newName = s.substring(s.indexOf("/name") + 6);
				// Print name change message
				writer.println(nameTxt.getText() + " changed name to " + newName);
				changeName(newName);
			}
			// Normal message
			else {
				writer.println(nameTxt.getText() + ": " + s);
			}
		}

		/** Change name **/
		public void changeName(String newName) {
			nameTxt.setText(newName);
		}

		/** Continuously read for messages sent back from server **/
		public void run() {
			try {
			    while (true) {
                    readMsg();
                }
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
	}

	public static void main(String args[]){
		new ChatClient();
	}
}
