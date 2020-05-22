package Tips;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 18:48
 * @Description:
 */
public class Tips_UI {
    public void Tips(String message){
        Stage Tip_Stage = new Stage();
        GridPane TipPane = new GridPane();
        TipPane.setPadding(new Insets(11.5,12.5,13.5,14.5));
        TipPane.setVgap(5.5);
        TipPane.setHgap(5.5);
        TipPane.add(new Label(message),0,0);
        Button button = new Button("确定");
        GridPane.setHalignment(button, HPos.CENTER);
        TipPane.add(button,0,1);
        TipPane.setAlignment(Pos.CENTER);

        button.setOnAction(event -> {
            Tip_Stage.close();
        });

        Scene scene = new Scene(TipPane,300,100);
        Tip_Stage.setTitle("错误");
        Tip_Stage.setScene(scene);
        Tip_Stage.show();
    }
}
