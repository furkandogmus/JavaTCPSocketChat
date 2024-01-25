import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            client = new Socket("localhost", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread inputThread = new Thread(inputHandler);
            inputThread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            done = true; // Set the flag to stop the InputHandler thread
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
            System.out.println("Client closed.");
        } catch (IOException e) {
            // Handle exception if necessary
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                String message;
                while (!done) {
                    message = inReader.readLine();
                    out.println(message);
                    if (message.equals("/quit")) {
                        break;
                    }
                }
                // Wait for the server's acknowledgment before shutting down
                while (!done && in.ready()) {
                    System.out.println(in.readLine());
                }
                shutdown();
            } catch (IOException e) {
                System.out.println("Error reading user input. Closing client.");
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
