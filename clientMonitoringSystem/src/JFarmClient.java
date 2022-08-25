import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class JFarmClient extends JFrame {
    private JPanel jpanelMain;
    private JTextField textFieldPort;
    private JButton connectButton;
    public JTable tableLog;
    private JButton saveLogButton;
    public static Integer port = 3200;
    public static Socket s;
    public static Integer countLog = 0;
    static String current = System.getProperty("user.dir");
    public static String outputFile = current + "\\logFileClient01.txt";
    public JFarmClient() throws IOException  {
        setContentPane(jpanelMain);
        setSize(500,400);
        setVisible(true);

        DefaultTableModel model = (DefaultTableModel) tableLog.getModel();
        model.addColumn("STT");
        model.addColumn("Date time");
        model.addColumn("Action");
        model.addColumn("Description");
        tableLog.setVisible(true);

        saveLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel model = (DefaultTableModel) tableLog.getModel();
                String data = "";
                for (int count = 0; count < model.getRowCount(); count++) {
                    data = data + model.getValueAt(count, 1).toString() + " / ";
                    data = data + model.getValueAt(count, 2).toString() + " / ";
                    data = data + model.getValueAt(count, 3).toString() + "\n";
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

        connectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                port = Integer.valueOf(textFieldPort.getText());
//                if(s == null || !s.isConnected()) {
//                    try {
//                        s = new Socket("localhost", port);
//
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
                JOptionPane.showMessageDialog(null,
                        "Connected to server");
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
        sentMessage="-------------------------\n";
        bw.write(sentMessage);
        bw.newLine();
        bw.flush();
    }

    public static void main(String[] args) throws IOException {
        JFarmClient clien = new JFarmClient();
        try
        {
            Socket s = new Socket("localhost",3200);
            System.out.println(s.getPort());

            InputStream is=s.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            OutputStream os=s.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            String sentMessage="";
            String receivedMessage;



            System.out.println("Talking to Server");
            System.out.println(InetAddress.getLocalHost().getHostAddress());

            // get directory from server
            receivedMessage = br.readLine();

            File fileOrDir = new File(receivedMessage);

            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(receivedMessage);
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Watch Service registered for dir: " + dir.getFileName());

            WatchKey key = null;

            traverseDepthFiles(fileOrDir,s);

            do
            {
                try {
                    System.out.println("Waiting for key to be signalled...");
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    System.out.println("InterruptedException: " + ex.getMessage());
                    return;
                }

                DataInputStream din=new DataInputStream(System.in);

                for (WatchEvent<?> event : key.pollEvents()) {
                    // Retrieve the type of event by using the kind() method.
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    countLog ++;
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        sentMessage = sentMessage + "ENTRY_CREATE,A new file " + fileName.getFileName() + " was created.\n" ;
                        Object[] row = {countLog, dtf.format(now).toString(),
                                "ENTRY_CREATE", "A new file " + fileName.getFileName() + " was created"};
                        DefaultTableModel model = (DefaultTableModel) clien.tableLog.getModel();
                        model.addRow(row);

                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        sentMessage = sentMessage +  "ENTRY_MODIFY,A file " + fileName.getFileName() + "  was modified.\n";

                        Object[] row = {countLog, dtf.format(now).toString(),
                                "ENTRY_MODIFY",  "A file " + fileName.getFileName() + "  was modified"};
                        DefaultTableModel model = (DefaultTableModel) clien.tableLog.getModel();
                        model.addRow(row);

                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        sentMessage = sentMessage +  "ENTRY_DELETE,A file " + fileName.getFileName() + "  was deleted.\n" ;
                        Object[] row = {countLog, dtf.format(now).toString(),
                                "ENTRY_DELETE",  "A file " + fileName.getFileName() + "  was deleted"};
                        DefaultTableModel model = (DefaultTableModel) clien.tableLog.getModel();
                        model.addRow(row);

                    }
                }
                bw.write(sentMessage);
                bw.newLine();
                bw.flush();

                if (sentMessage.equalsIgnoreCase("quit"))
                    break;
//                else
//                {
//                    receivedMessage=br.readLine();
//                    System.out.println("Received : " + receivedMessage);
//                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
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

    private void createUIComponents() {
        jpanelMain = new JPanel();
        setContentPane(jpanelMain);
        setSize(500,400);
        setVisible(true);
    }
}
