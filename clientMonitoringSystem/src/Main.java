import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost",3200);
        System.out.println(s.getPort());
    }

}
