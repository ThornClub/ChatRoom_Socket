package Login_Client;

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
 * @Date: 2020/5/15 09:22
 * @Description:
 */
public class Sign_UI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane UserPane = new GridPane();
        UserPane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        UserPane.setHgap(5.5);
        UserPane.setVgap(5.5);
        UserPane.setAlignment(Pos.CENTER);
        TextField User = new TextField();
        PasswordField Passwd = new PasswordField();
        TextField UserName = new TextField();
        UserPane.add(new Label("账号"),0,0);
        UserPane.add(User,0,1);
        UserPane.add(new Label("昵称"),0,2);
        UserPane.add(UserName,0,3);
        UserPane.add(new Label("密码"),0,4);
        UserPane.add(Passwd,0,5);
        Button Sign = new Button("注册");
        Button Back = new Button("返回");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(Sign,Back);
        hBox.setSpacing(40);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(UserPane,hBox);

        Scene scene = new Scene(vBox,500,300);
        primaryStage.setTitle("Chat-注册");
        primaryStage.setScene(scene);
        primaryStage.show();
        Sign.setOnAction(event -> {
            if (!User.getText().equals("") && !Passwd.getText().equals("") && !UserName.getText().equals("")) {
                Login_Client.Sign sign = new Sign();
                sign.Method_Sign(User.getText(), UserName.getText(),Passwd.getText());
            }
            else {
                Tips_UI tips_ui = new Tips_UI();
                tips_ui.Tips("请将用户信息填写完整");
            }
        });
        Back.setOnAction(event -> {
            Login_UI login_ui = new Login_UI();
            try {
                login_ui.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
