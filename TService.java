import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TService implements Runnable{
    Socket player;
    Socket fighter = null;
    ChessBoard chessBoard;
    int PLAYER_NUM;
    Scanner in, in2;
    PrintWriter out, out2;
    Map<Integer, Socket> playerMap;
    Map<Socket, Integer> playerTypeMap;

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public TService (Socket player, Map<Integer, Socket> playerMap, Map<Socket, Integer> playerTypeMap) {
        this.player = player;
        this.playerMap = playerMap;
        this.playerTypeMap = playerTypeMap;
        try{
            in = new Scanner(player.getInputStream());
            out = new PrintWriter(player.getOutputStream());
        } catch(IOException e){
            e.printStackTrace();
        }
        playerMap.forEach((key, value) -> {
            if (value == player) this.PLAYER_NUM = key;
        });
        this.chessBoard = new ChessBoard();
    }
    @Override
    public void run() {
        while(true) {
            if(!in.hasNext())return;
            String command = in.nextLine();
            if(command.equals("CONNECT")){
                int FIGHTER_NUM = PLAYER_NUM%2==0 ? PLAYER_NUM-1 : PLAYER_NUM+1;
                try {
                    if((fighter=playerMap.get(FIGHTER_NUM))==null) {
                        out.println("NO");
                        out.flush();
                    }
                    else {
                        in2 = new Scanner(fighter.getInputStream());
                        out2 = new PrintWriter(fighter.getOutputStream());
                        out.println("MATCH");
                        out.flush();
                        break;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(command.equals("EXIT")){

            }
            else{
                out.println("NON_MATCH");
                out.flush();
            }
        }
        while(true){
            if(!in.hasNext())return;
            String command = in.nextLine();
            if (command.equals("START")) {
                int num = PLAYER_NUM%2==0 ? 2 : 1;
                int roomNum = PLAYER_NUM%2==0 ? PLAYER_NUM-1 : PLAYER_NUM;
                out.println(num+" "+roomNum);
                out.flush();
                break;
            }
            else if(command.equals("EXIT")){

            }
            else{
                out.println("PLEASE_START");
                out.flush();
            }
        }
        while(true) {
            if (!in.hasNext()) return;
            String command = in.nextLine();
            if(command.equals("EXIT")){
                out2.println(command);
                out2.flush();
            }
            else if(command.contains("SET")){
                out.println(command);
                out2.println(command);
                out.flush();
                out2.flush();
            }
        }
    }
}
