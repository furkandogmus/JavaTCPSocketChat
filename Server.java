import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private List<ConnectionHandler> connectionHandlers;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connectionHandlers = new CopyOnWriteArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connectionHandlers.add(handler);
                pool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void broadcast(String message) {
        for (ConnectionHandler connectionHandler : connectionHandlers) {
            if (connectionHandler != null) {
                connectionHandler.sendMessage(message);
            }
        }
    }

    public void shutdown() {
        try {
            done = true;
            pool.shutdownNow();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler connectionHandler : connectionHandlers) {
                connectionHandler.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception
        }
    }

    class ConnectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        public ConnectionHandler(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + "connected!");
                broadcast(nickname + " joined the chat!");
                String message;
                while ((message = in.readLine()) != null){
                    if (message.startsWith("/nick ")){
                        String[] messageSplit = message.split(" ",2);
                        if(messageSplit.length ==2){

                            if (messageSplit[1].replaceAll(" ", "").isEmpty()){
                                out.println("No nickname provided!");
                                continue;
                            }
                            nickname = messageSplit[1];
                            broadcast(nickname + " renamed themselves to "+messageSplit[1]);
                            System.out.println(nickname + " renamed themselves to "+messageSplit[1]);
                            out.println("Successfully changed nickname to "+ nickname);
                        } else {
                            out.println("No nickname provided!");
                        }
                    } else if (message.equals("/quit")){
                        broadcast(nickname + " left the chat!");
                        shutdown();
                    } else {
                        broadcast(nickname +":" + message);
                    }
                }
            }
            catch(IOException e){
                shutdown();
            } finally {
                connectionHandlers.remove(this); // Remove the handler upon disconnect
            }
        }

        public void sendMessage(String message){
            out.println(message);
        }
        public void shutdown() {
            try {
                in.close();
            } catch (IOException e) {
                // IGNORE
            }
            out.close();
            if(!client.isClosed()){
                try {
                    client.close();
                } catch (IOException e) {
                // IGNORE
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

}
