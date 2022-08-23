import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

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

            System.out.println("Talking to Server");

            do
            {
                DataInputStream din=new DataInputStream(System.in);
                sentMessage="Client2" + din.readLine();
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
