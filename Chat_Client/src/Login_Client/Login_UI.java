package Login_Client;

import Client.*;
import Tips.Tips_UI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 17:18
 * @Description:登录面板
 */
public class Login_UI extends Application {
    public static TextField User,IP;
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane UserPane = new GridPane();
        UserPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        UserPane.setHgap(5.5);
        UserPane.setVgap(5.5);
        UserPane.setAlignment(Pos.CENTER);
        IP = new TextField();
        User = new TextField();
        PasswordField Passwd = new PasswordField();
        UserPane.add(new Label("账号:"), 0, 0);
        UserPane.add(User, 0, 1);
        UserPane.add(new Label("密码:"), 0, 2);
        UserPane.add(Passwd, 0, 3);
        UserPane.add(new Label("服务器IP:"),0,4);
        UserPane.add(IP,0,5);

        Button Login = new Button("登录");
        Button Sign = new Button("注册");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(Login, Sign);
        hBox.setSpacing(40);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(UserPane, hBox);

        Scene scene = new Scene(vBox, 400, 250);
        primaryStage.setTitle("欢迎使用Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
        Login.setOnAction(event -> {
            Check_Login check_login = new Check_Login();
            try {
                if (!User.getText().equals("") && !Passwd.getText().equals("") && !IP.equals("")) {
                    if (check_login.Check(User.getText(), Passwd.getText())) {
                        Client_UI client_ui = new Client_UI();
                        client_ui.start(primaryStage);
                    }
                } else {
                    Tips_UI tip = new Tips_UI();
                    tip.Tips("请检查用户名、密码、IP地址是否为空");
                }
            }
            catch (Exception e) {
                    e.printStackTrace();
            }

        });
        Sign.setOnAction(event -> {
            Sign_UI sign_ui = new Sign_UI();
            try {
                sign_ui.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
