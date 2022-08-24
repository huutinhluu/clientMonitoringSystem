import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JFramServer extends JFrame {
    public static ListServerThread listServerThread;
    private JButton startButton;
    private JTextPane textPanePort;
    private JPanel jPanelMain;
    private JTextArea textAreaMessage;
    private JList listClient;
    public static Socket socketOfServer;
    static ServerSocket listener;
    static Integer port;
    public JFramServer() throws IOException {
        setContentPane(jPanelMain);
        setSize(300,400);
        startButton.setVisible(true);
        textPanePort.setVisible(true);
        textAreaMessage.setVisible(true);
        listClient.setVisible(true);
        setVisible(true);
        port = Integer.valueOf(textPanePort.getText());
        listServerThread = new ListServerThread();
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if(listener == null) {
                        listener = new ServerSocket(Integer.valueOf(textPanePort.getText()));
                    }
                    textAreaMessage.append("Listening client...");
                    JOptionPane.showMessageDialog(null, "Listening...\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        listener = new ServerSocket(port);
        int clientNumber = 0;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );
        try {
            while (listener != null) {
                // Chấp nhận một yêu cầu kết nối từ phía Client.
                // Đồng thời nhận được một đối tượng Socket tại server.
                socketOfServer = listener.accept();
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++,textAreaMessage);
                listServerThread.add(serverThread);
                executor.execute(serverThread);
                listClient.setListData(listServerThread.getListServerThreads().toArray());

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
