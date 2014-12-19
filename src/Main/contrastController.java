package Main;

import FuzzDect.Fuzz;
import Util.readText;
import VSM.VSM;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.io.File;
import java.io.IOException;


public class contrastController {



    @FXML private TextField textA,textB,count;
    @FXML private TextFlow textflowA,textflowB;
    @FXML private ComboBox comboBox;
    String contentA=null,contentB=null;

    /**
     *
     * @param a  a string for textA
     * @param b  a string for textB
     * @throws IOException
     */
    void initData(String a,String b) throws IOException {

        textA.setText(a);
        textB.setText(b);
        File fa=new File(VSM.path+"\\"+a);
        File fb=new File(VSM.path+"\\"+b);
        readText rt=new readText();
        try {
             contentA=rt.readFile(fa);
             contentB=rt.readFile(fb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Text ta=new Text(contentA);
        Text ta1=new Text();
        ta1.setFill(Color.RED);
        Text ta2=new Text();

        Text tb=new Text(contentB);
        Text tb1=new Text();
        tb1.setFill(Color.RED);
        Text tb2=new Text();
        textflowA.getChildren().addAll(ta,ta1,ta2);
        textflowB.getChildren().addAll(tb, tb1, tb2);

        Fuzz.init();
        Fuzz fuzz=new Fuzz(contentA,contentB);
        int size=Fuzz.resultList.size();
        ObservableList<Integer> list= FXCollections.observableArrayList();
        for(int i=1;i<=size;++i){
            list.add(i);
        }

        count.setText(String.valueOf(size));

        comboBox.setItems(list);

        //set the listener of comboBox
        comboBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends  Number> ov,Number old_val,Number new_val)->{

            int cnt=new_val.intValue();
            int i=Fuzz.resultList.get(cnt).geti();
            int j=Fuzz.resultList.get(cnt).getj();
            String []sa=contentA.split(Fuzz.scentencesA.get(i));
            String []sb=contentB.split(Fuzz.scentencesB.get(j));
            clear();
            if(sa.length>0){
                ( (Text) textflowA.getChildren().get(0)).setText(sa[0]);
                if(sa.length>1)
                    ( (Text) textflowA.getChildren().get(2)).setText(sa[1]);
            }

            ( (Text) textflowA.getChildren().get(1)).setText(Fuzz.scentencesA.get(i));


            if(sb.length>0){
                ( (Text) textflowB.getChildren().get(0)).setText(sb[0]);
                if(sb.length>1)
                    ( (Text) textflowB.getChildren().get(2)).setText(sb[1]);
            }

            ( (Text) textflowB.getChildren().get(1)).setText(Fuzz.scentencesB.get(j));

        });


    }



    void clear(){

        //clear all the Text Node
        ( (Text) textflowA.getChildren().get(0)).setText("");
        ( (Text) textflowA.getChildren().get(1)).setText("");
        ( (Text) textflowA.getChildren().get(2)).setText("");
        ( (Text) textflowB.getChildren().get(0)).setText("");
        ( (Text) textflowB.getChildren().get(1)).setText("");
        ( (Text) textflowB.getChildren().get(2)).setText("");
    }

}
