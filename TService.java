import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TService implements Runnable{
    TService[] services;
    Socket player;
    Socket fighter;
    ChessBoard chessBoard;
    int winner = 0;
    int[] active;
    int playNum;
    Scanner in, in2;
    PrintWriter out, out2;

    Lock lock = new ReentrantLock();
    Condition winCondition = lock.newCondition();

    public TService(ServerSocket server, ChessBoard chessBoard, int[] active, int i, TService[] services){
        try {
            player = server.accept();
            in = new Scanner(player.getInputStream());
            out = new PrintWriter(player.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.chessBoard = chessBoard;
        this.active = active;
        playNum = i;
        this.services = services;
    }

    @Override
    public void run() {
        //try {
            if(playNum%2 == 0) {
                while(active[playNum+1] == 0) {
                    if(!in.hasNext()) return;
                    String require = in.nextLine();
                    if(require.equals("REGISTER")) {
                        out.println("NO");
                        out.flush();
                    }
                }
                while (true) {
                    if(!in.hasNext()) return;
                    String require = in.nextLine();
                    if(require.equals("REGISTER")) {
                        out.println("ACCEPT");
                        out.flush();
                        try {
                            fighter = services[playNum+1].player;
                            in2 = new Scanner(fighter.getInputStream());
                            out2 = new PrintWriter(fighter.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

            }
            else {
                while(active[playNum-1] == 0) {
                    if(!in.hasNext()) return;
                    String require = in.nextLine();
                    if(require.equals("REGISTER")) {
                        out.println("NO");
                        out.flush();
                    }
                }
                while(true) {
                    if(!in.hasNext()) return;
                    String require = in.nextLine();
                    if(require.equals("REGISTER")) {
                        out.println("ACCEPT");
                        out.flush();
                        try {
                            fighter = services[playNum-1].player;
                            in2 = new Scanner(fighter.getInputStream());
                            out2 = new PrintWriter(fighter.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

            }


            Thread check = new Thread(()->{
                //lock.lock();
                try{
                    int winner = 0;
                    while (winner == 0) {
                        winner = chessBoard.checkWinner();
                        Thread.sleep(100);
                    }
//                    while(winner==0) {
//                        winCondition.await();
//                        winner = chessBoard.checkWinner();
//                    }
                    out.println("WIN "+winner);
                    out2.println("WIN "+winner);
                    out.flush();
                    out2.flush();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
//                finally {
//                    lock.unlock();
//                }
            });
            check.start();
        while(true) {
            //lock.lock();
            if (!in.hasNext()) return;
            String command = in.next();
            int x = in.nextInt();
            int y = in.nextInt();
            int p = in.nextInt();
            command = command+" "+x+" "+y+" "+p;
            if(chessBoard.setChess(x,y,p)) {
                out.println(command);
                out2.println(command);
            }
            else {
                out.println("Invalid");
                out2.println("Invalid");
            }
            out.flush();
            out2.flush();
            //winCondition.signal();
            //lock.unlock();
        }
    }
}
