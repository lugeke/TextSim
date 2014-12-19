package VSM;

import Main.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * a Task which will be executed in background thread
 */
public class CalTask extends Task<ObservableList<CalResult>> {

    @Override
    protected ObservableList<CalResult> call() throws Exception {

        VSM vsm=new VSM(Controller.fileList);
        updateMessage("分词中");
        for(int i=0;i<200;++i){
            updateProgress(i,600);
            Thread.sleep(5);
        }
        vsm.wordSplit();

        //vsm.termSelection();
        updateMessage("计算tfidf");
        for(int i=200;i<400;++i){
            updateProgress(i,600);
            Thread.sleep(5);
        }
        vsm.countTfidf();

        updateMessage("计算余弦相似度");
        for(int i=400;i<600;++i){
            updateProgress(i,600);
            Thread.sleep(5);
        }
        vsm.SIMcos();

        updateMessage("完成");

        ObservableList<CalResult> list=FXCollections.observableList(VSM.result);
       /*FXCollections.sort(list,(x,y)->{
            double  a= Double.parseDouble(x.getSim().substring(0,5));
            double b=Double.parseDouble(y.getSim().substring(0,5));
            if(a==b) return 0;
            else if(a>b) return 1;
            else return -1;
        });*/
        return list;
    }



}
