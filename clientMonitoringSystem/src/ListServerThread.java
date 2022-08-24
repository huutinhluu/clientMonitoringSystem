import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListServerThread {
    private List<ServerThread> listServerThreads;

    public List<ServerThread> getListServerThreads() {
        return listServerThreads;
    }

    public ListServerThread() {
        listServerThreads = new ArrayList<>();
    }

    public void add(ServerThread serverThread){
        listServerThreads.add(serverThread);
    }

    public int getLength(){
        return listServerThreads.size();
    }
}