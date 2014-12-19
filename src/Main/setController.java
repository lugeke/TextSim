package Main;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.File;


public class setController {

    @FXML private ChoiceBox diffCB;
    @FXML private TextField thresholdText;

    private  int a;
    @FXML protected  void setOK(ActionEvent e){


        String threshold=thresholdText.getText();
        System.out.print(threshold+"-"+a);

        File file=new File("set.txt");
        Controller.saveFile(threshold+"-"+a,file);
        Controller.setStage.close();
    }

    void initData(){
        diffCB.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends  Number> ov,Number old_val,Number new_val)->{
            a=new_val.intValue();
            System.out.println(new_val.intValue());
        });
    }
    @FXML protected void setCancel(ActionEvent e){
        Controller.setStage.close();
    }
}
