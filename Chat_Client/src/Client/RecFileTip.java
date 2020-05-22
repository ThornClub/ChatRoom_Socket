package Client;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/22 13:16
 * @Description:
 */
public class RecFileTip {
    public static Button rec,cancel;
    public Stage Tip_Stage;
    public void Tips(String message){
         Tip_Stage = new Stage();
        GridPane TipPane = new GridPane();
        TipPane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        TipPane.setVgap(5.5);
        TipPane.setHgap(5.5);
        TipPane.add(new Label(message),0,0);
        rec = new Button("接收");
        cancel = new Button("取消");
        GridPane.setHalignment(rec, HPos.CENTER);
        TipPane.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(rec, cancel);
        hBox.setSpacing(40);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(TipPane, hBox);

        Scene scene = new Scene(vBox,400,100);
        Tip_Stage.setTitle("文件接收提示");
        Tip_Stage.setScene(scene);
        Tip_Stage.show();
    }

}
