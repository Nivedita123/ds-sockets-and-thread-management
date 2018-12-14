/** Name - Nivedita Gautam
 * Student ID = 1001649735
 * */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javafx.application.Platform;

/*
 * Reference - https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
 * https://javarevisited.blogspot.com/2015/06/how-to-create-http-server-in-java-serversocket-example.html
 * 
 * */

// HTTPServer thread which accepts incoming connections and assigns a thread to each client
public class HTTPServer extends Thread {

	// Port on which server is running
	final static int PORT_NUMBER = 4444;

	private static ServerSocket server;

	// Getter method for private variable server
	static public ServerSocket getServer() {
		return server;
	}

	@Override
	public void run() {

		try {

			// Server is listening for connection on Port 4444
			server = new ServerSocket(PORT_NUMBER);

			// Keep accepting connections from clients
			while (true) {

				// Accept connection from client
				Socket clientSocket = server.accept();

				// Assign the client to a ServerThread
				Thread thread = new ServerThread(clientSocket);
				thread.start();
			}

		} catch (IOException e) {

			// Exception is thrown if the port number is busy or not available
			System.out.println("Port " + PORT_NUMBER + " is busy!! Please use another port.");
			System.out.println("Terminating");
			System.exit(0);
		}
	}
}

// Server thread to handle a particular client
class ServerThread extends Thread {

	// Class variables
	final private Socket clientSocket;
	final private String clientName;

	// Constructor to initialize class variables
	public ServerThread(Socket clientSocket) {

		this.clientSocket = clientSocket;
		this.clientName = "Client " + clientSocket.getPort();
	}

	@Override
	public void run() {

		// create an instance of serverGUI class to access its methods
		ServerGUI serverGUI = new ServerGUI();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				// Add the client name to the list of connected clients
				serverGUI.addClient(clientName);
				ServerGUI.addToStatusMsgs(clientName + " got connected.");
			}
		});

		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;

		try {

			// getting data input and output streams for client socket
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

			// keep sending data to client until client disconnects
			while (true) {

				// read HTTP request from client using the data input streams
				String headerFromClient = dataInputStream.readUTF();
				String dataFromClient = dataInputStream.readUTF();
				String httpRequest = headerFromClient + dataFromClient;

				// To Updating GUI components outside the application class
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						// Display the unparsed HTTP request messages on Server GUI
						ServerGUI.addToHttpMsgs(httpRequest);
						ServerGUI.addToStatusMsgs("Server will wait " + dataFromClient + " seconds for client.");
					}
				});

				// wait for xx seconds (as specified in the request from client)
				try {
					Thread.sleep(Integer.parseInt(dataFromClient) * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Getting date in HTTP format Reference -
				// https://stackoverflow.com/questions/7707555/getting-date-in-http-format-in-java
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

				// encode the message in HTTP. Reference - https://www.jmarshall.com/easy/http/
				String httpServerData = "Server waited " + dataFromClient + " seconds for " + clientName;

				/*
				 * initial response Line contains the HTTP version, a response status code , and
				 * an English reason phrase describing the status code. 200 OK - The request
				 * succeeded, and the resulting resource (e.g. file or script output) is
				 * returned in the message body.
				 */
				String statusLine = "HTTP/1.1 200 OK\r\n";
				String host = "Host: " + HTTPServer.getServer().getInetAddress() + ":"
						+ HTTPServer.getServer().getLocalPort() + "\r\n";

				// Date in HTTP format
				String date = "Date: " + dateFormat.format(calendar.getTime()) + "\r\n";
				String contentType = "Content-Type: text/plain\r\n";

				// Content length in bytes
				String contentLength = "Content-Length: " + httpServerData.getBytes("UTF-8").length + "\r\n";
				String userAgent = "User-Agent: " + HTTPServer.getServer() + "\r\n";

				// To separate body from header
				String endHeader = "\r\n";

				// Creating HTTP Response header from above data
				String httpServerHeader = statusLine + host + date + contentType + contentLength + userAgent;

				// sending the http response to client using data output stream
				dataOutputStream.writeUTF(httpServerHeader + endHeader);
				dataOutputStream.writeUTF(httpServerData);

				// To update GUI components from outside of Application class
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						// display HTTP response sent by server on Server GUI
						String httpResponse = httpServerHeader + endHeader + httpServerData;
						ServerGUI.addToHttpMsgs(httpResponse);
					}
				});
			}

		} catch (IOException e) {

			// To update GUI components from outside of Application class
			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					// Remove the name of the disconnected client from the client list and display
					// the message on Server GUI
					serverGUI.removeClient(clientName);
					ServerGUI.addToStatusMsgs(clientName + " got disconnected.");
				}
			});

			try {
				// Close data streams and sockets if client disconnects
				clientSocket.close();
				dataInputStream.close();
				dataOutputStream.close();

			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}
	}
}
