import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("client connected to server!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeEveryThing();
        }
    }

    public void sendMessage(String message) {
        try {
            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (Exception e){
            System.out.println(e.getMessage());
            closeEveryThing();
        }
    }

    public void closeEveryThing() {
        try {
            if (socket != null)
                socket.close();

            if (bufferedReader != null)
                bufferedReader.close();

            if (bufferedWriter != null)
                bufferedWriter.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listenForResponse() {
        new Thread(() -> {
            String responseFromServer = "";
            try {
                while (socket.isConnected()){
                    responseFromServer = bufferedReader.readLine();
                    System.out.println(responseFromServer);
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                closeEveryThing();
            }
        }).start();
    }

    private void sendCommandToServer() {    // TODO
        new Thread(() -> {
            Scanner input = new Scanner(System.in);
            String command;
            while (socket.isConnected()) {
                command = input.nextLine();
                sendMessage(command);
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            int PORT = 1234;
            String HOSTNAME = "localhost";
            Client client = new Client(HOSTNAME, PORT);
            client.listenForResponse();
            client.sendCommandToServer();
        } catch (Exception e){
            System.out.println("Server is not running!");
        }

    }
}
