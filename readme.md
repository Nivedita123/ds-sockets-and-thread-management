## Sockets and Thread Management

This  system consists of a server and multiple client processes. Each client process will connect to the server over a socket connection and register a user name at the server.
The server and the client are managed with a simple GUI. The messages exchanged between server and client use HTTP format.

The actions performed by the client and server are summarized below -

### Client

The client will execute the following sequence of steps:
1. Connect to the server via a socket.
2. Provide the server with a unique user name (Some value associated with the process)
3. Generate a random integer between 5 and 15.
4. Upload that integer to the server.
5. Wait until response received from the server.
6. Parse the HTTP message and print response from the server in **normal** text.
7. Repeat at step 3 until the process is killed by the user.

### Server

The server concurrently supports all the connected clients and displays a list of them in real-time. The server will execute the following sequence of steps:
1. Startup and listen for incoming connections.
2. Print that a client has connected and fork a thread to handle that client.
3. Print integer received from client to GUI and announce that it is waiting for that period of time.
4. Sleep for the number of seconds equal to that integer.
5. After waiting, will return a message to client stating, “Server waited <#> seconds for client <name>.”
6. Begin at step 3 until connection is closed by the client.
7. When a client gets disconnected, the user is notified in real-time.
8. Print all the incoming and outgoing messages in **unparsed HTTP** format.

### Classes
1. `ServerGUI` extends `javafx.application.Application`
2. `HTTPServer` extends `java.lang.Thread`
3. `ServerThread` extends `java.lang.Thread`
4. `ClientGUI` extends `javafx.application.Application`
5. `Client` extends `java.lang.Thread`


### Compilation  

- `javac HTTPServer.java`   
- `javac ServerGUI.java`   
- `javac ClientGUI.java`

### Execution
- `java ServerGUI`
- `java ClientGUI`

### References

1. JavaFX: Working with the JavaFX Scene Graph – https://docs.oracle.com/javase/8/javafx/scene-graph-tutorial/scenegraph.htm
2. HTTP Made Really Easy - A Practical Guide to Writing Clients and Servers – https://www.jmarshall.com/easy/http/
3. Introducing Threads in Socket Programming in Java – https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/


#### Note -
This code was submitted as part of Distributed Systems course assignment at The University of Texas at Arlington to Prof. Chance Eary (https://mentis.uta.edu/explore/profile/chance-eary)
