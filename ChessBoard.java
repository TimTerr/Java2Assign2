public class ChessBoard {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static boolean TURN = false;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    public boolean setChess(int x, int y , int player) {
        if (!flag[x][y]) {
            chessBoard[x][y] = player;
            flag[x][y] = true;
            return true;
        }
        return false;
    }
    public int checkWinner(){
        int winner = EMPTY;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int a, b, c;
                if(i==0) {
                    a = chessBoard[i][j]; b = chessBoard[i+1][j];; c = chessBoard[i+2][j];
                    if((winner = check(a, b, c)) > 0) break;
                }
                if (j==0) {
                    a = chessBoard[i][j]; b = chessBoard[i][j+1];; c = chessBoard[i][j+2];
                    if((winner = check(a, b, c)) > 0) break;
                }
                if(i==0 && j==0) {
                    a = chessBoard[i][j]; b = chessBoard[i+1][j+1];; c = chessBoard[i+2][j+2];
                    if((winner = check(a, b, c)) > 0) break;
                }
                if(i==0 && j==2){
                    a = chessBoard[i][j]; b = chessBoard[i+1][j-1];; c = chessBoard[i+2][j-2];
                    if((winner = check(a, b, c)) > 0) break;
                }
            }
            if(winner > 0) break;
        }
        return winner;
    }
    public int check(int a, int b, int c) {
        if(a>0 && b>0 && c>0) {
            if(a==b && b==c){
                return a;
            }
        }
        return 0;
    }
}
