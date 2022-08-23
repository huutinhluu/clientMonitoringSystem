import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JFramServer extends JFrame {
    private JButton startButton;
    private JTextPane textPanePort;
    private JPanel jPanelMain;
    private JTextArea textAreaMessage;
    public static Socket socketOfServer;
    static ServerSocket listener;
    static Integer port;
    public JFramServer() throws IOException {
        setContentPane(jPanelMain);
        setVisible(true);
        setSize(300,400);
        startButton.setVisible(true);
        textPanePort.setVisible(true);
        textAreaMessage.setVisible(true);
        port = Integer.valueOf(textPanePort.getText());
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if(listener == null) {
                        listener = new ServerSocket(Integer.valueOf(textPanePort.getText()));
                    }
                    textAreaMessage.append("Listening client...");
                    JOptionPane.showMessageDialog(null, "Listening...");
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
                executor.execute(serverThread);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        JFramServer server = new JFramServer();

    }
}
