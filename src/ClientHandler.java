import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = null;  // to broadcast a message to all client
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            if (clientHandlers == null) {
                clientHandlers = new ArrayList<>();
            }
            clientHandlers.add(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeEveryThing();
        }
    }

    private void closeEveryThing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
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

    public void removeClientHandler() {
        try {
            clientHandlers.remove(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        removeClientHandler();
        closeEveryThing(this.socket, this.bufferedWriter, this.bufferedReader);
    }

    @Override
    public void run() {
        String commandFromClient;
        while (this.socket.isConnected()) {
            try {
                commandFromClient = this.bufferedReader.readLine();
                String[] spCommands = commandFromClient.split(" ");
                boolean isError = false;
                // response
                double result = 0;
                double startTime = System.currentTimeMillis();

                if (spCommands.length == 0) {
                    result = -1;
                    isError = true;
                } else {
                    String operator = spCommands[0];
                    float opt1 = 0;
                    float opt2 = 0;

                    if (spCommands.length > 1)
                        opt1 = Float.parseFloat(spCommands[1]);

                    if (spCommands.length > 2)
                        opt2 = Float.parseFloat(spCommands[2]);

                    switch (operator) {
                        case "Add" -> result = opt1 + opt2;
                        case "Subtract" -> result = opt1 - opt2;
                        case "Divide" -> {
                            result = -1;
                            if (opt2 == 0)
                                isError = true;
                            else
                                result = opt1 / opt2;
                        }
                        case "Multiply" -> result = opt1 * opt2;
                        case "Sin" -> result = Math.sin(Math.toRadians(opt1));
                        case "Cos" -> result = Math.cos(Math.toRadians(opt1));
                        case "Tan" -> result = Math.tan(Math.toRadians(opt1));
                        case "Cot" -> {
                            double tan = Math.tan(Math.toRadians(opt1));
                            result = -1;
                            if (tan == 0)
                                isError = true;
                            else
                                result = 1 / tan;
                        }
                    }
                }

                double endTime = System.currentTimeMillis();
                double calculationTime = endTime - startTime;

                String msgToClient;
                if (isError)
                    msgToClient = calculationTime + " ERR";
                else
                    msgToClient = calculationTime + " " + result;


                sendMessage(msgToClient);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                closeEveryThing();
                break;
            }
        }
    }
}
