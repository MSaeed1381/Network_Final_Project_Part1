import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ServerSocket serverSocket;
    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server is Created!");

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void startServer(){
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("A client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeSeverSocket();
        }
    }

    void closeSeverSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void broadCast(String message) {
        for (ClientHandler clientHandler: ClientHandler.clientHandlers){
            try {
                clientHandler.sendMessage(message);
            }catch (Exception e) {
                System.out.println(e.getMessage());
                clientHandler.closeEveryThing();
            }
        }
    }

    public static void main(String[] args) {
        int PORT = 1234;
        Server server = new Server(PORT);
        server.startServer();
    }
}
