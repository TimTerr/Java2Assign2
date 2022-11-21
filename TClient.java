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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TClient extends Application{
    Socket player = null;
    Scanner in;
    PrintWriter out;
    FXMLLoader fxmlLoader;
    Thread t;
    Lock sendingLock = new ReentrantLock();

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Tic-Tac-Toe");
        StackPane root = new StackPane();

        Button btnCon = new Button();
        btnCon.setText("connect");
        btnCon.setOnAction(event -> {
            try{
                if(player==null){
                    playerInit(new Socket("localhost", 2002));
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
                if(response.equals("NO")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("No other players! Please click reConnect");
                    alert.showAndWait();
                }
                else if(response.equals("MATCH")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Match Successfully! Please get start");
                    alert.showAndWait();
                }
            } catch (IOException | InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Non-Server Detected");
                alert.showAndWait();
                //System.out.println("Non-Server Detected");
            }
        });

        Button btnReCon = new Button();
        btnReCon.setText("reConnect");
        btnReCon.setOnAction(event -> {
            out.println("CONNECT");
            out.flush();
            String response = in.nextLine();
            System.out.println(response);
            if(response.equals("NO")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("No other players! Please click reConnect");
                alert.showAndWait();
            }
            else if(response.equals("MATCH")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Match Successfully! Please get start");
                alert.showAndWait();
            }
        });

        Button btnStart = new Button();
        btnStart.setText("start");
        btnStart.setOnAction((actionEvent)-> {
            try {
                gameBegin(primaryStage);
                out.println("START");
                out.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        });

        root.getChildren().add(btnCon);
        root.getChildren().add(btnReCon);
        root.getChildren().add(btnStart);
        btnCon.setTranslateX(-120);
        btnStart.setTranslateX(120);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public void playerInit(Socket socket) throws IOException {
        player = socket;
        OutputStream outputStream = player.getOutputStream();
        InputStream inputStream = player.getInputStream();
        out = new PrintWriter(outputStream);
        in = new Scanner(inputStream);

    }

    public void gameBegin(Stage primaryStage) throws IOException {
        this.fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
        Pane root1 = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();Rectangle gamePlace = (Rectangle) fxmlLoader.getNamespace().get("game_panel");
        gamePlace.setOnMouseClicked(event -> {
            int x = (int) (event.getX()/90);
            int y = (int) (event.getY()/90);
            int p = controller.TURN ? 1 : 2;
            String message = x+" "+y+" "+p;
            out.println("SET "+message);
            System.out.println("Sending: SET "+message);
            out.flush();
        });
        primaryStage.setScene(new Scene(root1));
        primaryStage.setResizable(false);
        Thread t = new Thread(()->{
            while (true) {
                if(!in.hasNext()) return;
                String response = in.nextLine();
                System.out.println("Receive: "+response);
                String[] command = response.split(" ");
                int x = Integer.parseInt(command[1]);
                int y = Integer.parseInt(command[2]);
                int p = Integer.parseInt(command[3]);
                Controller c = fxmlLoader.getController();
                Platform.runLater(()->{
                    if (c.refreshBoard(x,y)) {
                        c.TURN = !c.TURN;
                    }
                    int winner;
                    if((winner=c.checkWinner())>0){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Winner: "+winner);
                        alert.showAndWait();
                    }

                });
//                switch (response) {
//                    case "Invalid":
//                        break;
//                    default:
//                        if(response.contains("WIN")) {
//                            Platform.runLater(()->{
//                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                                alert.setTitle(null);
//                                alert.setHeaderText(null);
//                                alert.setContentText(response);
//                                alert.showAndWait();
//                            });
//                            continue;
//                        }
//                        String[] command = response.split(" ");
//                        int x = Integer.parseInt(command[1]);
//                        int y = Integer.parseInt(command[2]);
//                        int p = Integer.parseInt(command[3]);
//                        Controller c = fxmlLoader.getController();
//                        Platform.runLater(()->{
//                            if (c.refreshBoard(x,y)) {
//                                c.TURN = !c.TURN;
//                            }
//                        });
//                }
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
        t.start();
    }

    public static void main(String[] args) throws IOException {
        launch(args);

    }
}
