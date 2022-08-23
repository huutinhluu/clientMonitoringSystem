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
import java.net.InetAddress;
import java.net.Socket;

public class JFarmClient {
    private JTextField textFieldPort;
    private JButton connectButton;
    public static Integer port = 3200;
    public static Socket s;
    public JFarmClient() {
        connectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                port = Integer.valueOf(textFieldPort.getText());
                try {
                    s = new Socket("localhost",port);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        try
        {
            Socket s = new Socket("localhost",port);
            System.out.println(s.getPort());

            InputStream is=s.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            OutputStream os=s.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            String sentMessage="";
            String receivedMessage;

            System.out.println("Talking to Server");
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            do
            {
                DataInputStream din=new DataInputStream(System.in);
                sentMessage="Client 1 "+din.readLine();
                bw.write(sentMessage);
                bw.newLine();
                bw.flush();

                if (sentMessage.equalsIgnoreCase("quit"))
                    break;
                else
                {
                    receivedMessage=br.readLine();
                    System.out.println("Received : " + receivedMessage);
                }

            }
            while(true);

            bw.close();
            br.close();
        }
        catch(IOException e)
        {
            System.out.println("There're some error");
        }
    }
}
