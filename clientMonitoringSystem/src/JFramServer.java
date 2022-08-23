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

    public JFramServer() {
        setContentPane(jPanelMain);
        setVisible(true);
        setSize(300,400);
        startButton.setVisible(true);
        textPanePort.setVisible(true);
        textAreaMessage.setVisible(true);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {


            }
        });
    }
    public static Socket socketOfServer;

    public static void main(String[] args){
        int clientNumber = 0;
        try
        {
            ServerSocket listener = new ServerSocket(3200);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    10, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8) // queueCapacity
            );
            try {
                while (true) {
                    // Chấp nhận một yêu cầu kết nối từ phía Client.
                    // Đồng thời nhận được một đối tượng Socket tại server.
                    socketOfServer = listener.accept();
                    ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++);
                    executor.execute(serverThread);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        catch(IOException ex)
        {
            System.out.println("There're some error");
        }

    }
}
