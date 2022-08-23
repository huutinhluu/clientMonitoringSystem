import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class ServerThread implements Runnable {

    private Socket socketOfServer;
    private int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
    public static JTextArea jTextarea;

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public ServerThread(Socket socketOfServer, int clientNumber, JTextArea jTextArea)  {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        jTextarea = jTextArea;
        System.out.println("Server thread number " + clientNumber + " Started");
        isClosed = false;
    }

    @Override
    public void run() {
        try {
            do {
                System.out.println("Waiting for a Client");

                System.out.println("Talking to client");
                System.out.println(String.valueOf(socketOfServer.getPort()));

                InputStream is = socketOfServer.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                OutputStream os = socketOfServer.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));


                String receivedMessage;

                do {
                    receivedMessage = br.readLine();
                    System.out.println("Received : " + receivedMessage);
                    String finalReceivedMessage = receivedMessage;
                    SwingUtilities.invokeAndWait(new Runnable(){
                        public void run()
                        {
                            jTextarea.append("\n"+ finalReceivedMessage);
                        }
                    });
                    if (receivedMessage.equalsIgnoreCase("quit")) {
                        System.out.println("Client has left !");
                        break;
                    } else {
                        DataInputStream din = new DataInputStream(System.in);
                        String k = "";//din.readLine();
                        bw.write(k);
                        bw.newLine();
                        bw.flush();
                    }
                }
                while (true);
                bw.close();
                br.close();
            }
            while (true);

        } catch (IOException | InterruptedException | InvocationTargetException ex) {
            System.out.println("There're some error");
        }
    }
}
