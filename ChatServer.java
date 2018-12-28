package chatroom;

import com.sun.security.ntlm.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * MELODY LEE
 */
public class ChatServer extends ChatWindow {

	private ArrayList<ClientHandler> clients = new ArrayList<>();

	public ChatServer(){
		super();
		this.setTitle("Chat Server");
		this.setLocation(80,80);

		try {
			// Create a listening service for connections at the designated port number.
			ServerSocket srv = new ServerSocket(2113);

			while (true) {
				// The method accept() blocks until a client connects.
				printMsg("Waiting for a connection");

				// Setup new socket for client
				Socket socket = srv.accept();

				// Create new ClientHandler for every connection
				ClientHandler handler = new ClientHandler(socket);
				// Add to ArrayList of ClientHandlers
				clients.add(handler);
				// Create new thread for that client
				handler.connect();

			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/** This inner class handles communication to/from one client. */
	class ClientHandler implements Runnable {
		private PrintWriter writer;
		private BufferedReader reader;

		public ClientHandler(Socket socket) {
			try {
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e){
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
				}
		}

		public void handleConnection() {
			try {
				while(true) {
					// Read a message from the client
					String msg = readMsg();
					// Send message back to each client
					sendMsg(msg);
				}
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		/** Receive and display a message */
		public String readMsg() throws IOException {
			String s = reader.readLine();

			// Print message to server
            printMsg(s);

			return s;
		}

		/** Send a string back to all connected clients **/
		public void sendMsg(String s){
		    // Loop through clients ArrayList
			for (ClientHandler client : clients) {
				client.writer.println(s);
			}
		}

		/** Once thread starts, run() executes **/
		public void run() {
			handleConnection();
		}

		/** Create and start new thread for client **/
		public void connect() {
			Thread t = new Thread(this);
			t.start();
		}

	}

	public static void main(String args[]){
		new ChatServer();
	}
}
