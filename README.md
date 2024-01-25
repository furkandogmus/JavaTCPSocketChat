# Socket Chat App

A simple chat application using Java sockets for communication. This application allows multiple clients to connect to a server and engage in real-time chat.

## Features

- **Real-time Chat:** Multiple clients can connect to the server and communicate with each other in real-time.

- **Nickname Support:** Clients can set and change their nicknames using the "/nick" command.

- **Graceful Shutdown:** Clients can gracefully exit the chat using the "/quit" command.

## Usage

### Server

1. Run the `Server` class to start the chat server.

    ```bash
    java Server
    ```

2. The server will be listening on port `9999`.

### Client

1. Run the `Client` class to start a chat client.

    ```bash
    java Client
    ```

2. Enter a nickname when prompted.

3. Start chatting! Type messages in the console and press Enter to send them.

4. Use the "/nick" command to change your nickname.

    ```plaintext
    /nick NewNickname
    ```

5. Use the "/quit" command to gracefully exit the chat.

    ```plaintext
    /quit
    ```

## Dependencies

- Java (JDK 8 or later)

## Contributing

If you'd like to contribute to the project, please fork the repository and submit a pull request. Feel free to open issues for any bugs or feature requests.

## License

This project is licensed under the [MIT License](LICENSE).
