import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Comparator;

public class JFarmClient extends JFrame {
    private JPanel jpanelMain;
    private JTextField textFieldPort;
    private JButton connectButton;
    private JTextArea textAreaLog;
    public static Integer port = 3200;
    public static Socket s;

    public JFarmClient() throws IOException  {
        setContentPane(jpanelMain);
        setSize(500,400);
        setVisible(true);

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

    public static void traverseDepthFiles(final File fileOrDir, Socket s) throws IOException {
        OutputStream os=s.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        String sentMessage= "Directory Client 1: ";
        // check xem fileOrDir la file hay foder
        if (fileOrDir.isDirectory()) {
            // in ten folder ra man hinh

            final File[] children = fileOrDir.listFiles();
            if (children == null) {
                return;
            }
            // sắp xếp file theo thứ tự tăng dần
            Arrays.sort(children, new Comparator<File>() {
                public int compare(final File o1, final File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (final File each : children) {
                // gọi lại hàm traverseDepthFiles()
                sentMessage= "Directory Client 1: ";
                sentMessage = sentMessage + each.getAbsolutePath();
                bw.write(sentMessage);
                bw.newLine();
                bw.flush();
            }
        } else {
            // in ten file ra man hinh
            sentMessage= "Directory Client 1: ";
            sentMessage = sentMessage + fileOrDir.getAbsolutePath();
            bw.write(sentMessage);
            bw.newLine();
            bw.flush();
        }
        bw.write(sentMessage);
        bw.newLine();
        bw.flush();
    }

    public static void main(String[] args) throws IOException {
        JFarmClient clien = new JFarmClient();
//        try
//        {
//            Socket s = new Socket("localhost",3200);
//            System.out.println(s.getPort());
//
//            InputStream is=s.getInputStream();
//            BufferedReader br=new BufferedReader(new InputStreamReader(is));
//
//            OutputStream os=s.getOutputStream();
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//
//            String sentMessage="";
//            String receivedMessage;
//
//
//
//            System.out.println("Talking to Server");
//            System.out.println(InetAddress.getLocalHost().getHostAddress());
//
//            // get directory from server
//            receivedMessage = br.readLine();
//
//            File fileOrDir = new File(receivedMessage);
//
//            WatchService watcher = FileSystems.getDefault().newWatchService();
//            Path dir = Paths.get("D:/");
//            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
//                    StandardWatchEventKinds.ENTRY_MODIFY);
//
//            System.out.println("Watch Service registered for dir: " + dir.getFileName());
//
//            WatchKey key = null;
//
//            traverseDepthFiles(fileOrDir,s);
//
//            do
//            {
//                try {
//                    System.out.println("Waiting for key to be signalled...");
//                    key = watcher.take();
//                } catch (InterruptedException ex) {
//                    System.out.println("InterruptedException: " + ex.getMessage());
//                    return;
//                }
//
//                DataInputStream din=new DataInputStream(System.in);
//                sentMessage="Client 1: ";
//
//                for (WatchEvent<?> event : key.pollEvents()) {
//                    // Retrieve the type of event by using the kind() method.
//                    WatchEvent.Kind<?> kind = event.kind();
//                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
//                    Path fileName = ev.context();
//                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
//                        sentMessage = sentMessage + "A new file " + fileName.getFileName() + " was created.\n" ;
//                        System.out.println("A new file %s was created.%n" + fileName.getFileName());
//                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
//                        sentMessage = sentMessage +  "A file " + fileName.getFileName() + "  was modified.\n";
//                        System.out.println("A file %s was modified.%n" + fileName.getFileName());
//                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
//                        sentMessage = sentMessage +  "A file " + fileName.getFileName() + "  was deleted.%\n" ;
//                    }
//                }
//                bw.write(sentMessage);
//                bw.newLine();
//                bw.flush();
//
//                if (sentMessage.equalsIgnoreCase("quit"))
//                    break;
//                else
//                {
//                    receivedMessage=br.readLine();
//                    System.out.println("Received : " + receivedMessage);
//                }
//
//                boolean valid = key.reset();
//                if (!valid) {
//                    break;
//                }
//
//            }
//            while(true);
//
//            bw.close();
//            br.close();
//        }
//        catch(IOException e)
//        {
//            System.out.println("There're some error");
//        }
    }

    private void createUIComponents() {
        jpanelMain = new JPanel();
        setContentPane(jpanelMain);
        setSize(500,400);
        setVisible(true);
    }
}
