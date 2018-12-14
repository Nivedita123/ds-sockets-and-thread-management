
/** Name - Nivedita Gautam
 * Student ID = 1001649735
 * */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

// Client GUI class
public class ClientGUI extends Application {

	// Class variables

	// the port number on which server is running
	final static int PORT_NUMBER = 4444;

	// Name of the client
	private String clientName = "";

	// Socket for client
	private Socket clientSocket = null;

	// GUI Components
	private TextField clientHeading = new TextField("Client ");
	private Text statusMsgLabel = new Text("\n\nStatus Messages:\n\n");
	private TextArea statusMsg = new TextArea();
	private Text serverMsgLabel = new Text("\n\nMessages from server:\n\n");
	private TextArea serverMsg = new TextArea();
	private Button exitButton = new Button("   Exit   ");

	// to set client heading on the GUI
	public void setHeading(String clientHeading) {

		this.clientHeading.appendText(clientHeading);
	}

	// to append text to status messages text area
	public void addStatusMessage(String statusMsg) {

		this.statusMsg.appendText(statusMsg);
	}

	// to append text to HTTP messages textarea
	public void addServerMessage(String serverMsg) {

		this.serverMsg.appendText(serverMsg);
	}

	// Main function
	public static void main(String[] args) throws IOException {

		// launch the GUI thread
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// Start the client thread to send/receive http messages
		Client c = new Client();
		c.start();

		// creating textflow layout
		TextFlow textFlow = new TextFlow();
		textFlow.setLayoutX(40);
		textFlow.setLayoutY(40);

		// contains list of children to be rendered.
		Group group = new Group(textFlow);

		// setting window height, width and color
		Scene scene = new Scene(group, 1000, 700, Color.WHITE);

		// add the scene to the top level container - stage
		stage.setScene(scene);

		// since client heading is a text field make it un-editable
		clientHeading.setDisable(true);
		// setting font style,type and size
		clientHeading.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(clientHeading);

		// set font style,type and size
		statusMsgLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(statusMsgLabel);
		textFlow.getChildren().add(statusMsg);

		// set font style,type and size
		serverMsgLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(serverMsgLabel);
		textFlow.getChildren().add(serverMsg);

		// exit from the program when users clicks on exit button
		exitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// to add some space before the button
		textFlow.getChildren().add(new Text("              "));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(exitButton);

		// display the stage
		stage.show();
	}

	// Client thread to send receive HTTP messages
	public class Client extends Thread {

		public void run() {

			try {
				// establish connection with the server
				clientSocket = new Socket(InetAddress.getByName("localhost"), PORT_NUMBER);

				// set client name
				clientName = "Client " + clientSocket.getLocalPort();

				// to Update GUI components outside of Application class
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// Display the message on Client GUI
						addStatusMessage(clientName + " is connected to Server on port - " + PORT_NUMBER + " !!\n\n");

						// Display the name of client on Client GUI.
						setHeading("" + clientSocket.getLocalPort());
					}
				});

				// obtain input and out streams of the client socket to send/receive HTTP
				// response
				DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

				// Keep sending HTTp Request to server until user presses exit button or server
				// goes offline
				while (true) {

					// Generate random value to be sent to the server between 5 to 15
					String httpClientData = "" + (int) ((Math.random() * 10) + 5);

					// Getting date in HTTP format
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
					dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

					// encode the message in HTTP. Reference - https://www.jmarshall.com/easy/http/

					// initialRequestLine contains method name, resource name, and http version
					String initialRequestLine = "POST / HTTP/1.1\r\n";

					// below are header lines which provide information about the request
					String host = "Host: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + "\r\n";
					String date = "Date: " + dateFormat.format(calendar.getTime()) + "\r\n";
					String contentType = "Content-Type: text/plain\r\n";
					// Length of Data in bytes
					String contentLength = "Content-Length: " + httpClientData.getBytes("UTF-8").length + "\r\n";
					// information about the sender
					String userAgent = "User-Agent: " + clientName + "\r\n";

					// It means connection will be closed after the corresponding response
					String connection = "Connection: Close\r\n";

					// To separate body from header
					String endHeader = "\r\n";
					String httpClientHeader = initialRequestLine + host + date + contentType + contentLength + userAgent
							+ connection;

					// send HTTP request to server
					dataOutputStream.writeUTF(httpClientHeader + endHeader);
					dataOutputStream.writeUTF(httpClientData);

					// read HTTP response from server from the data input streams of client socket
					String headerFromServer = dataInputStream.readUTF();
					String dataFromServer = dataInputStream.readUTF();

					// To update GUI components outside GUI Application class
					Platform.runLater(new Runnable() {

						@Override
						public void run() {

							// Display the parsed HTTP Response received from server on Client GUI
							addServerMessage(dataFromServer + "\n\n");
						}
					});

				}
			}

			// Unable to connect to the server
			catch (Exception e) {

				// To update GUI components outside GUI Application class
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// Display this message on Client GUI
						addStatusMessage("Server is offline. Press EXIT to close this window.\n\n");
					}
				});

				try {

					// if clientSocket is open close it.
					if (clientSocket != null)
						clientSocket.close();

				} catch (IOException e1) {

					// Display this message on Client GUI
					addStatusMessage("Cannot close client socket. Press EXIT to close this window.\n\n");
				}
			}
		}
	}

}
