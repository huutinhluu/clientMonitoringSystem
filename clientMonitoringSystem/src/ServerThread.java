import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable {

    private Socket socketOfServer;
    public String nameClient;
    public String directory;
    public List<LogClient> listLogClient;
    public Boolean run = false;
    private int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
    public static JTextArea jTextarea;
    public static JTable jTableLogServer;

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public ServerThread(Socket socketOfServer, int clientNumber, JTable jTableLog, String Directory, JTextArea jtext)  {
        listLogClient = new ArrayList<>();
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        this.directory = Directory;
        this.jTableLogServer = jTableLog;
        this.jTextarea = jtext;
        nameClient = socketOfServer.getInetAddress().getHostAddress() + " - Port: " + socketOfServer.getPort();

        System.out.println("Server thread number " + clientNumber + " Started");
        isClosed = false;
    }

    public void recivedDirectory() throws IOException, InterruptedException, InvocationTargetException {
        do {

            InputStream is = socketOfServer.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String receivedMessage="";
            receivedMessage = br.readLine();
            String finalReceivedMessage = receivedMessage;
            if (receivedMessage.equalsIgnoreCase("quit")) {
                System.out.println("Client has left !");
                break;
            }
            SwingUtilities.invokeAndWait(new Runnable(){
                public void run()
                {
                    jTextarea.append("\n"+ finalReceivedMessage);
                }
            });

        }
        while (true);
    }

    @Override
    public void run() {
        try {
            do {
                this.run = true;
                InputStream is = socketOfServer.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                OutputStream os = socketOfServer.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                // send directory
                DataInputStream din = new DataInputStream(System.in);
                String k = directory;
                bw.write(k);
                bw.newLine();
                bw.flush();

                System.out.println("Waiting for a Client");

                System.out.println("Talking to client");
                System.out.println(String.valueOf(socketOfServer.getPort()));



                String receivedMessage;

                do {
                    receivedMessage= br.readLine();

                    if(receivedMessage.contains("Directory Client")) {
                        String finalReceivedMessage1 = receivedMessage;
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                jTextarea.append("\n" + finalReceivedMessage1);
                            }
                        });
                    }
                    else if (receivedMessage.contains("ENTRY")) {

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();

                        LogClient logClient = new LogClient();
                        logClient.STT = listLogClient.size() + 1;
                        logClient.ipClient = nameClient;
                        logClient.dateTime = dtf.format(now).toString();
                        logClient.action = receivedMessage.split(",")[0];
                        logClient.detail = receivedMessage.split(",")[1];

                        listLogClient.add(logClient);

                        System.out.println("Received : " + receivedMessage);
                        String finalReceivedMessage = receivedMessage;
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                Object[] row = {logClient.STT, logClient.ipClient,
                                        logClient.dateTime, logClient.action, logClient.detail};
                                DefaultTableModel model = (DefaultTableModel) jTableLogServer.getModel();
                                model.addRow(row);
                            }
                        });
                    }
                    if (receivedMessage.equalsIgnoreCase("quit")) {
                        System.out.println("Client has left !");
                        break;
                    } else {
                        din = new DataInputStream(System.in);
                        k = "Received message ok";//din.readLine();
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
