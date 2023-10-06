# ChatApp

ChatApp is a simple Java-based console application that allows users to establish peer-to-peer connections and communicate with each other through a command-line interface.

## Features

- **Connection Management:** Connect to other peers using their IP address and port.
- **Message Sending:** Send messages to connected peers.
- **Connection Termination:** Terminate connections with specific peers.
- **List Connected Peers:** Display a list of currently connected peers.
- **Display IP and Port:** View your own IP address and listening port.
- **Graceful Exit:** Close all connections and terminate the server gracefully.

## How to Use

1. **Compile the Code:** Compile the Java code using a Java compiler (e.g., `javac App.java`).
2. **Run the Application:** Run the compiled application with the desired port number (e.g., `java App 5000`).
3. **Use Commands:** Use various commands to interact with the ChatApp (e.g., `connect`, `send`, `list`, `terminate`, etc.).
4. **Exit:** Type `exit` to gracefully close all connections and terminate the ChatApp.

## Commands

- `help`: Display the ChatApp manual.
- `myip`: Display your IP address.
- `myport`: Display the port number you are listening on.
- `connect <ip> <port>`: Connect to a peer with the specified IP address and port.
- `list`: Display a list of connected peers.
- `terminate <id>`: Terminate the connection with the peer identified by the given ID.
- `send <id> <message>`: Send a message to the peer identified by the given ID.
- `exit`: Gracefully exit the ChatApp.

## Dependencies

The ChatApp has no external dependencies beyond the Java standard library.

## License

This project is licensed under the [MIT License](LICENSE).
