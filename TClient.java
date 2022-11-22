import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class TClient extends Application {
    Socket socket = null;
    Scanner in;
    PrintWriter out;
    FXMLLoader fxmlLoader;
    static int player;
    static int roomNum;
    Lock sendingLock = new ReentrantLock();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tic-Tac-Toe");
        StackPane root = new StackPane();
        Button btnCon = new Button();
        btnCon.setText("connect");
        btnCon.setOnAction(event -> {
            try {
                if (socket == null) {
                    playerInit(new Socket("localhost", 2002));
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(primaryStage);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Connected!");
                    alert.showAndWait();
                }
                out.println("CONNECT");
                out.flush();
                Thread.sleep(100);
                String response = in.nextLine();
                System.out.println(response);
                if (response.equals("NO")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(primaryStage);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("No other players! Please reConnect");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(primaryStage);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Match Successfully! Please get start");
                    alert.showAndWait();
                }
            } catch (IOException | InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(primaryStage);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Non-Server Detected");
                alert.showAndWait();
                //System.out.println("Non-Server Detected");
            }
        });

        Button btnStart = new Button();
        btnStart.setText("start");
        btnStart.setOnAction(actionEvent -> {
            try {
                out.println("START");
                out.flush();
                String response = in.nextLine();
                if (response.equals("NON_MATCH")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(primaryStage);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Non-Match");
                    alert.showAndWait();
                } else {
                    String[] num = response.split(" ");
                    player = Integer.parseInt(num[0]);
                    roomNum = Integer.parseInt(num[1]);
                    gameBegin(primaryStage);
                }
            } catch (IOException | NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(primaryStage);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Non-Connected");
                alert.showAndWait();
            }
        });

        root.getChildren().add(btnCon);
        root.getChildren().add(btnStart);
        btnCon.setTranslateX(-50);
        btnStart.setTranslateX(50);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                out.println("EXIT");
                out.flush();
                System.exit(0);
            } catch (NullPointerException e) {

            }
        });
    }

    public void playerInit(Socket socket) throws IOException {
        this.socket = socket;
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        out = new PrintWriter(outputStream);
        in = new Scanner(inputStream);

    }

    public void gameBegin(Stage primaryStage) throws IOException {
        this.fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
        Pane root1 = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        Rectangle gamePlace = (Rectangle) fxmlLoader.getNamespace().get("game_panel");
        gamePlace.setOnMouseClicked(event -> {
            try {
                socket.sendUrgentData(0);
                System.out.println("hhh");
                int x = (int) (event.getX() / 90);
                int y = (int) (event.getY() / 90);
                int turn = controller.TURN ? 1 : 2;
                if (player == turn) {
                    String message = x + " " + y + " " + player;
                    out.println("SET " + message);
                    System.out.println("Sending: SET " + message);
                    out.flush();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(primaryStage);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Not your turn!");
                    alert.showAndWait();
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(primaryStage);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("The server has lost. Sorry to exit");
                alert.showAndWait();
                System.exit(0);
            }

        });
        Text room = new Text("ROOM #" + roomNum);
        room.setTranslateX(10);
        room.setTranslateY(10);
        Text playerInform = new Text("YOU ARE PLAYER_" + player);
        playerInform.setTranslateX(10);
        playerInform.setTranslateY(50);

        root1.getChildren().add(room);
        root1.getChildren().add(playerInform);
        primaryStage.setScene(new Scene(root1));
        primaryStage.setResizable(false);
        Thread t = new Thread(() -> {
            while (true) {
                if (!in.hasNext()) {
                    return;
                }
                String response = in.nextLine();
                System.out.println("Receive: " + response);
                if (response.equals("EXIT")) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(primaryStage);
                        alert.setTitle(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Opponent has left. Sorry to exit");
                        alert.showAndWait();
                        System.exit(0);
                    });
                } else if (response.contains("SET")) {
                    String[] command = response.split(" ");
                    int x = Integer.parseInt(command[1]);
                    int y = Integer.parseInt(command[2]);
                    int p = Integer.parseInt(command[3]);
                    Controller c = fxmlLoader.getController();
                    Platform.runLater(() -> {
                        if (c.refreshBoard(x, y)) {
                            c.TURN = !c.TURN;
                        }
                        int winner;
                        if ((winner = c.checkWinner()) > 0) {
                            if (winner < 3) {
                                if (player == winner) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.initOwner(primaryStage);
                                    alert.setTitle(null);
                                    alert.setHeaderText(null);
                                    alert.setContentText("YOU WIN!");
                                    alert.showAndWait();
                                    System.exit(0);
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.initOwner(primaryStage);
                                    alert.setTitle(null);
                                    alert.setHeaderText(null);
                                    alert.setContentText("YOU LOSE!");
                                    alert.showAndWait();
                                    System.exit(0);
                                }
                            } else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.initOwner(primaryStage);
                                alert.setTitle(null);
                                alert.setHeaderText(null);
                                alert.setContentText("NO WINNER");
                                alert.showAndWait();
                                System.exit(0);
                            }
                        }

                    });
                }
            }
        });
        t.start();
    }

    public static void main(String[] args) throws IOException {
        launch(args);

    }
}
