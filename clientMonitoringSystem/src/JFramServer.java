import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JFramServer extends JFrame {
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
    public JFramServer() throws IOException {
        setContentPane(jPanelMain);
        setSize(700,400);
        startButton.setVisible(true);
        textPanePort.setVisible(true);
        checkLogButton.setVisible(true);
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

        Object[] row = { "data1", "data2", "data3", "data4", "data4" };
        model.addRow(row);
        tableLog.setVisible(true);



        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );
        ThreadPoolExecutor executor_temp = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );


        checkLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ServerThread serverThread = listServerThread.getListServerThreads().get(listClient.getSelectedIndex());
                serverThread.directory = textFieldDirectory.getText();
                //executor_temp.shutdown();
                //executor.execute(serverThread);
                Thread thread = new Thread(serverThread);
                thread.start();
                JOptionPane.showMessageDialog(null,
                        "Start check log real time to " + listClient.getSelectedValue());
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
                // Chấp nhận một yêu cầu kết nối từ phía Client.
                // Đồng thời nhận được một đối tượng Socket tại server.
                socketOfServer = listener.accept();
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++,
                        tableLog, textFieldDirectory.getText(),textArea );
                listServerThread.add(serverThread);
                //executor_temp.execute(serverThread_temp);
                List<String> listNameServer = listServerThread.getListServerThreads().stream()
                        .map(server -> server.nameClient)
                        .collect(Collectors.toList());
                listClient.setListData(listNameServer.toArray());

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        saveLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
            }
        });
    }


    public static void main(String[] args) throws IOException {
        JFramServer server = new JFramServer();

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
