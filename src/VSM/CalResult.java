package VSM;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * the class bind to the TableView
 */
public  class CalResult{
    private final SimpleStringProperty textA,textB;
    private final SimpleStringProperty  result;
    private final DoubleProperty sim;

    public  CalResult(String textA,String textB,double sim,String result){
        this.textA=new SimpleStringProperty(textA);
        this.textB=new SimpleStringProperty(textB);
        this.sim=new SimpleDoubleProperty(sim);
        this.result=new SimpleStringProperty(result);
    }
    public String getTextA(){
        return textA.get();
    }
    public String getTextB(){
        return textB.get();
    }
    public double getSim(){
        return sim.get();
    }
    public String getResult(){return result.get();}

    public void setSim(double s) {
        sim.set(s);
    }
    public void setTextA(String a){
        textA.set(a);
    }
    public void setTextB(String b){
        textB.set(b);
    }
    public void setResult(String r){result.set(r);}
}
