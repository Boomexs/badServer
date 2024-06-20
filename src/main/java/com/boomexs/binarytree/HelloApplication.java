package com.boomexs.binarytree;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Klient");
        stage.setScene(scene);
        stage.show();
        HelloController controller = fxmlLoader.getController();

        try {
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try{
                        Socket socket = new Socket("localhost", 4999);
                        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                        BufferedReader br = new BufferedReader(isr);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                        Thread inputThread = new Thread(){
                            @Override
                            public void run(){
                                try {
                                    String str;
                                    while ((str = br.readLine()) != null) {
                                        String finalStr = str;
                                        Platform.runLater(() -> {
                                            if (finalStr.equals("clear")) {
                                                controller.flowText.getChildren().clear();
                                            }
                                            else if (finalStr.split("\\s+")[0].equals("search")) {
                                                controller.flowText.getChildren().clear();
                                                controller.flowText.getChildren().add(new Text(finalStr));
                                            }
                                            else {
                                                controller.flowText.getChildren().add(new Text(finalStr + "\n"));
                                            }
                                        });
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                    System.exit(0);
                                }
                            }
                        };
                        inputThread.start();
                        //printWriter.println("double");
                        controller.send.setOnAction((actionEvent) -> {
                            printWriter.println(controller.cmd.getText());
                        });

                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("user could not connect to websocket server");
                        System.exit(0);
                    }
                }
            };
            thread.start();
        }
        catch (Exception e) {
            System.out.println("thread failed");
        }
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }

    private void draw(PrintWriter printWriter){

    }
}