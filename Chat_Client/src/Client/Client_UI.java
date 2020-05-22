package Client;

import FileToStringUtils.*;
import Login_Client.GetName;
import Login_Client.Login_UI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/2 09:16
 * @Description:客户端UI
 */
public class Client_UI extends Application {
    public static File file;
    public static TextField InputMessage;
    public static Button Sendbt,FileChose;
    public static String UserName;
    public static TextArea ShowMessage;
    public static ObservableList<String> list;
    public static ListView<String> ShowFriends;
    @Override
    public void start(Stage primaryStage){
        GridPane InputPane = new GridPane();
        InputPane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        InputPane.setHgap(5.5);
        InputPane.setVgap(5.5);
        //消息输入窗口
        InputMessage = new TextField();
        InputMessage.setPrefWidth(210);
        InputPane.add(InputMessage,0,0);
        //发送按钮
        Sendbt = new Button("发送");
        InputPane.add(Sendbt,1,0);
        FileChose = new Button("发送文件");
        InputPane.add(FileChose,2,0);

        GridPane MessagePane = new GridPane();
        MessagePane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        MessagePane.setVgap(5.5);
        MessagePane.setHgap(5.5);
        //显示消息窗口
        Label MessageLabel = new Label("消息列表");
        MessagePane.add(MessageLabel,0,0);
        ShowMessage = new TextArea();
        ShowMessage.setWrapText(true);
        ShowMessage.setEditable(false);
        ShowMessage.setMaxWidth(350);
        ShowMessage.setPrefHeight(400);
        MessagePane.add(ShowMessage,0,1);
        Login_UI login_ui = new Login_UI();
        GetName getName = new GetName();
        UserName = getName.getName(login_ui.User.getText());
        ShowMessage.appendText("欢迎你"+UserName+"！"+"\r\n");


        GridPane FriendsPane = new GridPane();
        FriendsPane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        FriendsPane.setHgap(5.5);
        FriendsPane.setVgap(5.5);
        //好友列表窗口
        Label label = new Label("好友列表");
        FriendsPane.add(label,0,0);
        list = FXCollections.observableArrayList();
        ShowFriends = new ListView<>(list);
        FriendsPane.add(ShowFriends,0,1);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(MessagePane,InputPane);
        vBox.setAlignment(Pos.CENTER_RIGHT);

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(FriendsPane);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1,vBox);

        Scene scene = new Scene(hBox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("简易聊天室-客户端");
        primaryStage.show();
        new Thread(new Client()).start();
        //关闭图形界面时，关闭所有线程
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        FileChose.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("File Choose");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files","*.*")
            );
            file = chooser.showOpenDialog(primaryStage);
            try {
                String FileString = FileToStringUtils.fileToString(file.getPath());
                SendAndRec sendAndRec = new SendAndRec();
                sendAndRec.Send(Client.socket,(2+file.getName()+"#"+FileString));
                ShowMessage.appendText("文件传输完成,请提醒接收!"+"\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
