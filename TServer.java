import java.io.IOException;
import java.net.ServerSocket;

public class TServer {
    public static void main(String[] args) throws IOException {
        TService[] tServices = new TService[3];
        Thread[] threads = new Thread[3];
        int[] active = new int[3];
        ChessBoard chessBoard = new ChessBoard();
        for (int i = 0; i < 3; i++) {
            ServerSocket server = new ServerSocket(2000+i);
            tServices[i] = new TService(server, chessBoard, active, i, tServices);
            Thread t = new Thread(tServices[i]);
            t.start();
            active[i] = 1;
            System.out.println("finish "+i);
        }

//        while(true) {
//            s1 = server1.accept();
//            System.out.println("s1");
//            Scanner in1 = new Scanner(s1.getInputStream());
//            PrintWriter out1 = new PrintWriter(s1.getOutputStream());
//
//            s2 = server2.accept();
//            System.out.println("s2");
//            Scanner in2 = new Scanner(s1.getInputStream());
//            PrintWriter out2 = new PrintWriter(s1.getOutputStream());
//
//
//            Thread t = new Thread(new TService(s1, chessBoard));
//            t.start();
//
//        }
    }
}