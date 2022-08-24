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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class JFarmClient02 {
    public static void main(String[] args) throws IOException {
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

            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get("D:/");
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Watch Service registered for dir: " + dir.getFileName());

            WatchKey key = null;

            System.out.println("Talking to Server");
            System.out.println(InetAddress.getLocalHost().getHostAddress());
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
                sentMessage="Client 2: ";

                for (WatchEvent<?> event : key.pollEvents()) {
                    // Retrieve the type of event by using the kind() method.
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        sentMessage = sentMessage + "A new file " + fileName.getFileName() + " was created.\n" ;
                        System.out.println("A new file %s was created.%n" + fileName.getFileName());
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        sentMessage = sentMessage +  "A file " + fileName.getFileName() + "  was modified.\n";
                        System.out.println("A file %s was modified.%n" + fileName.getFileName());
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        sentMessage = sentMessage +  "A file " + fileName.getFileName() + "  was deleted.%\n" ;
                    }
                }
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
}
