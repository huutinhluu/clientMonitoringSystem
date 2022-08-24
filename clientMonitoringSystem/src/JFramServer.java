import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
    private JTextArea textAreaMessage;
    private JList listClient;
    private JTextField textFieldDirectory;
    private JButton checkLogButton;
    public static Socket socketOfServer;
    static ServerSocket listener;
    static Integer port;
    public int clientNumber = 0;
    public JFramServer() throws IOException {
        setContentPane(jPanelMain);
        setSize(500,400);
        startButton.setVisible(true);
        textPanePort.setVisible(true);
        textAreaMessage.setVisible(true);
        checkLogButton.setVisible(true);
        listClient.setVisible(true);
        setVisible(true);
        textFieldDirectory.setVisible(true);
        port = Integer.valueOf(textPanePort.getText());
        listServerThread = new ListServerThread();


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
                ServerThread serverThread = listServerThread.getListServerThreads().get(listClient.getSelectedIndex());
                serverThread.directory = textFieldDirectory.getText();
                executor.execute(serverThread);
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
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++,textAreaMessage, textFieldDirectory.getText());
                listServerThread.add(serverThread);
                //executor.execute(serverThread);
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
