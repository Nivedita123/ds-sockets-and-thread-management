/** Name - Nivedita Gautam
 * Student ID = 1001649735
 * */

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

// References - https://docs.oracle.com/javase/8/javafx/scene-graph-tutorial/scenegraph.htm
public class ServerGUI extends Application {

	final static HTTPServer httpServer = new HTTPServer();

	// GUI Components

	// Set the heading for Server GUI
	Text serverHeading = new Text("HTTP Server is online on port - " + HTTPServer.PORT_NUMBER + ". \n\n");
	// Create labels and text areas to be displayed on GUI
	static private Text statusMsgsLabel = new Text("\n\nStatus Messages: \n \n ");
	static private Text httpMsgsLabel = new Text("HTTP Request/Response Messages: \n\n");
	static private TextArea statusMsgs = new TextArea("");
	static private TextArea httpMsgs = new TextArea("");
	private Button exitButton = new Button("   Exit   ");

	/*
	 * Observable list (A list that allows listeners to track changes when they
	 * occur.) to store list of clients connected. Reference -
	 * https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList
	 * .html
	 */
	static private ObservableList<String> clientList = FXCollections
			.observableArrayList("List of clients currently connected:");
	// listview is used to represent the type of the objects stored in the
	// ObservableList
	static private ListView<String> clientListView = new ListView<String>();

	// getter method for private variable - clientList
	public ObservableList<String> getClientList() {
		return clientList;
	}

	// Adds an element to clientList
	public void addClient(String client) {
		clientList.add(client);
	}

	// removes an element from clientList
	public void removeClient(String client) {
		clientList.remove(client);
	}

	// to append text to status messages text area
	static public void addToStatusMsgs(String statusMsg) {
		statusMsgs.appendText(statusMsg + "\n\n");
	}

	// to append text to http msgs text area
	static public void addToHttpMsgs(String httpMsg) {
		httpMsgs.appendText(httpMsg + "\n\n\n");
	}

	@Override
	public void start(Stage stage) throws Exception {

		// creating textflow layout
		TextFlow textFlow = new TextFlow();
		textFlow.setLayoutX(40);
		textFlow.setLayoutY(40);

		// contains list of children to be rendered.
		Group group = new Group(textFlow);

		// setting window height, width and color
		Scene scene = new Scene(group, 1100, 700, Color.WHITE);
		// add the scene to the top level container - stage
		stage.setScene(scene);

		// Setting the title of the GUI window
		stage.setTitle("Multi-threaded HTTP Server");

		// setting font color, size, type and style
		serverHeading.setFill(Color.GREEN);
		serverHeading.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(serverHeading);

		// set fonts and add to GUI
		httpMsgsLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));
		textFlow.getChildren().add(httpMsgsLabel);
		textFlow.getChildren().add(httpMsgs);

		// set fonts and add to GUI
		statusMsgsLabel.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));
		textFlow.getChildren().add(statusMsgsLabel);
		textFlow.getChildren().add(statusMsgs);

		// bind observable list to list view
		clientListView.setItems(clientList);
		// set size of list displayed on GUI
		clientListView.setPrefSize(280, 185);
		// to add a bit of space before the client list
		textFlow.getChildren().add(new Text("                      "));
		// add to textFlow to display on GUI
		textFlow.getChildren().add(clientListView);

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

	// main function
	public static void main(String[] args) throws IOException {

		// Start HTTPServer thread Accepts connection requests from client
		httpServer.start();

		// launch the GUI thread
		launch(args);
	}
}
