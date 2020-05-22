package Server;

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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 17:04
 * @Description:服务端界面
 */
public class Server_UI extends Application {
    public static TextField InputMessage;
    public static Button Sendbt;
    public static TextArea ShowMessage;
    public static ListView<String> ShowFriends;
    public static ObservableList<String> list;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //消息输入窗口面板
        GridPane InputPane = new GridPane();
        //设置面板边距
        InputPane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        //设置节点的间距
        InputPane.setHgap(5.5);
        InputPane.setVgap(5.5);
        //消息输入窗口
        InputMessage = new TextField();
        InputMessage.setPrefWidth(300);
        InputPane.add(InputMessage,0,0);
        //发送按钮
        Sendbt = new Button("发送");
        InputPane.add(Sendbt,1,0);

        //显示消息面板
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

        //显示朋友列表面板
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

        //将消息面板和输入面板放在一列
        VBox vBox = new VBox();
        vBox.getChildren().addAll(MessagePane,InputPane);
        vBox.setAlignment(Pos.CENTER_RIGHT);

        //将好友列表面板和端口面板放在一列
        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(FriendsPane);

        //整体将vBox1和vBox放在一行
        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1,vBox);

        Scene scene = new Scene(hBox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("简易聊天室-服务器端");
        primaryStage.show();
        try {
            //正则表达式匹配IP
            //使用InetAddress.getLocalHost()获取本机IP
            String ip = InetAddress.getLocalHost().toString();
            //编写正则表达式
            String reg = "\\d+\\.\\d+\\.\\d+\\.\\d";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(ip);
            while (matcher.find()) {
                ShowMessage.appendText("当前服务端IP:" + matcher.group(0) + "\r\n");
            }
        } catch (UnknownHostException e) {
            ShowMessage.appendText("本地主机名无法解析成地址"+"\r\n");
        }
        //启动服务端线程
        new Thread(new Server()).start();
        //关闭图形界面时，关闭所有线程
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
