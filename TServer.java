import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class TServer {
    public static void main(String[] args) throws IOException {
        ServerSocket gameCenter = new ServerSocket(2002);
        Map<Integer, Socket> playerMap = new LinkedHashMap<>();
        Map<Socket, Integer> playerTypeMap = new LinkedHashMap<>();
        int playerNum = 1;
        while (true) {
            Socket player = gameCenter.accept();
            playerMap.put(playerNum, player);
            playerTypeMap.put(player, playerNum % 2);
            TService service = new TService(player, playerMap, playerTypeMap);
            Thread t = new Thread(service);
            t.start();
            playerNum++;
        }
    }
}
