import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JFramServer extends JFrame {
    public static List<Thread> listThread;
    public static ListServerThread listServerThread;
    private JButton startButton;
    private JTextPane textPanePort;
    private JPanel jPanelMain;
    private JList listClient;
    private JTextField textFieldDirectory;
    private JButton checkLogButton;
    private JTable tableLog;
    private JTextArea textArea;
    private JButton saveLogButton;
    public static Socket socketOfServer;
    static ServerSocket listener;
    static Integer port;
    public int clientNumber = 0;
    static String current = System.getProperty("user.dir");
    public static String outputFile = current + "\\logFile.txt";
    public JFramServer() throws IOException {
        listThread = new ArrayList<>();
        setContentPane(jPanelMain);
        setSize(900,400);
        startButton.setVisible(true);
        textPanePort.setVisible(true);
        checkLogButton.setVisible(true);
        saveLogButton.setVisible(true);
        listClient.setVisible(true);
        textArea.setVisible(true);
        setVisible(true);
        textFieldDirectory.setVisible(true);
        port = Integer.valueOf(textPanePort.getText());
        listServerThread = new ListServerThread();


        DefaultTableModel model = (DefaultTableModel) tableLog.getModel();
        model.addColumn("STT");
        model.addColumn("IP Client");
        model.addColumn("Date time");
        model.addColumn("Action");
        model.addColumn("Description");
        tableLog.setVisible(true);



        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );


        checkLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textArea.append("=================\n");
                for (Thread serverThread : listThread){
                    if(serverThread.isAlive()){
                        serverThread.stop();
                    }
                }
                ServerThread serverThread = listServerThread.getListServerThreads().get(listClient.getSelectedIndex());
                serverThread.directory = textFieldDirectory.getText();
                Thread thread = new Thread(serverThread);
                thread.start();
                listThread.add(thread);
                JOptionPane.showMessageDialog(null,
                        "Start check log real time to " + listClient.getSelectedValue());
            }
        });

        saveLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel model = (DefaultTableModel) tableLog.getModel();
                String data = "";
                for (int count = 0; count < model.getRowCount(); count++) {
                    data = data + model.getValueAt(count, 1).toString() + " / ";
                    data = data + model.getValueAt(count, 2).toString() + " / ";
                    data = data + model.getValueAt(count, 3).toString() + " / ";
                    data = data + model.getValueAt(count, 4).toString() + "\n";
                }

                try {
                        File file = new File(outputFile);

                        /* This logic is to create the file if the
                         * file is not already present
                         */
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        //Here true is to append the content to file
                        FileWriter fw = new FileWriter(file, true);
                        //BufferedWriter writer give better performance
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(data);
                        //Closing BufferedWriter Stream
                        bw.close();
                    } catch (IOException ex){

                }
                JOptionPane.showMessageDialog(null,
                        "Save log OK, address: " + outputFile);
            }


        });

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if(listener == null) {
                        listener = new ServerSocket(Integer.valueOf(textPanePort.getText()));
                    }
                    JOptionPane.showMessageDialog(null, "Listening...\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        listener = new ServerSocket(port);
        try {
            while (listener != null) {
                // Ch???p nh???n m???t y??u c???u k???t n???i t??? ph??a Client.
                // ?????ng th???i nh???n ???????c m???t ?????i t?????ng Socket t???i server.
                socketOfServer = listener.accept();
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++,
                        tableLog, textFieldDirectory.getText(),textArea );
                listServerThread.add(serverThread);
                List<String> listNameServer = listServerThread.getListServerThreads().stream()
                        .map(server -> server.nameClient)
                        .collect(Collectors.toList());
                listClient.setListData(listNameServer.toArray());

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        JFramServer server = new JFramServer();

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
