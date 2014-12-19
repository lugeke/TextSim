package Main;

import VSM.CalResult;
import VSM.CalService;
import VSM.VSM;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Controller {

    @FXML private ListView listView;
    @FXML private Button calBut,clearBut,exportBtn;
    @FXML private ProgressBar proBar;
    @FXML private Label leftLabel,proLabel;
    private  ObservableList<String> observableList=null;
    public static List<File> fileList;

    @FXML protected void chooseFile(ActionEvent e){

        FileChooser fileChooser=new FileChooser();
       // fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("txt",".txt",".doc"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ALL TEXT","*.*"),
                new FileChooser.ExtensionFilter("TXT","*.txt"),
                new FileChooser.ExtensionFilter("DOC","*.doc"),
                new FileChooser.ExtensionFilter("DOC","*.docx"),
                new FileChooser.ExtensionFilter("DOC","*.wps")
                );

        fileList=fileChooser.showOpenMultipleDialog(Main.stage);

        if(fileList==null||fileList.size()==0){
            listView.setItems(FXCollections.observableArrayList("choose no file"));

        }else{
            if(fileList.size()>1)
                calBut.setDisable(false);
            leftLabel.setText("choose  "+fileList.size()+" file"+(fileList.size()>1?"s":""));
            clearBut.setDisable(false);
            observableList=FXCollections.observableArrayList();
//            for(File f:fileList){
//                //System.out.println(f.getName().toString());
//                observableList.add(f.getName().toString());
//            }
            fileList.stream().forEach(f->{observableList.add(f.getName().toString());});
            listView.setItems(observableList);


        }

    }
    @FXML protected void menuExit(ActionEvent e){
        System.exit(0);
    }
    @FXML protected void clearList(ActionEvent e){

        if(observableList!=null)
            observableList.clear();
        clearBut.setDisable(true);
        calBut.setDisable(true);
        proBar.setVisible(false);
        leftLabel.setText("choose 0 file");
    }
    @FXML
    private TableColumn textAcol,textBcol,simCol,resultCol;
    @FXML private TableView<CalResult> table;
    @FXML private TextField date;
    private ObservableList<CalResult> data=null;


    @FXML TextField textA,textB;

    public static String teA,teB;

    @FXML protected void calSim(ActionEvent e) throws InterruptedException {
        proBar.setVisible(true);
        proLabel.setVisible(true);

        CalService service=new CalService();
        proBar.progressProperty().bind(service.progressProperty());
        //proBar.visibleProperty().bind(service.runningProperty());
        table.itemsProperty().bind(service.valueProperty());
        proLabel.textProperty().bind(service.messageProperty());

        textAcol.setCellValueFactory(new PropertyValueFactory<>("textA"));
        textBcol.setCellValueFactory(new PropertyValueFactory<>("textB"));
        simCol.setCellValueFactory(new PropertyValueFactory<>("sim"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));




        table.setRowFactory(//set contextMenu
                (TableView<CalResult> tableView) -> {
                    final TableRow<CalResult> row = new TableRow<>();
                    final ContextMenu rowMenu = new ContextMenu();
                    MenuItem contrastItem = new MenuItem("对比");

                    contrastItem.setOnAction((actionEvent)->{
                        CalResult calResult=table.getSelectionModel().getSelectedItem();

                         teA=calResult.getTextA();
                         teB=calResult.getTextB();
                        System.out.println("selected "+teA+"+"+teB);

                        FXMLLoader loader = new FXMLLoader( getClass().getResource("contrast.fxml") );

                        Stage stage = new Stage(StageStyle.DECORATED);
                        try {
                            stage.setScene( new Scene( (Pane) loader.load()) );
                            contrastController controller =loader.<contrastController>getController();
                            controller.initData(teA,teB);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        stage.setTitle("文件对比");
                        stage.show();

                    });

                    rowMenu.getItems().addAll(contrastItem);
                    row.contextMenuProperty().bind(
                            Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                    .then(rowMenu)
                                    .otherwise((ContextMenu) null));
                    return row;
                });

        //start similarity calculate service
        service.start();
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        date.setText(dateString);
       // proBar.setProgress(1.0f);
        exportBtn.setDisable(false);

    }

    @FXML protected void exportFile(ActionEvent e){
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("保存文档");
        FileChooser.ExtensionFilter filter=new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateString = formatter.format(currentTime);
        fileChooser.setInitialFileName("抄袭检测记录 "+dateString+".txt");
        //fileChooser.setInitialDirectory(new File(System.getProperty("user.desktop")));
        File file=fileChooser.showSaveDialog(Main.stage);
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("文本A"+"-----"+"文本B"+"-------"+"相似度"+"------"+"结果"+"\r\n");
        VSM.result.forEach(r->{
            stringBuffer.append(r.getTextA()+"-----"+r.getTextB()+"-------"+r.getSim()+"------"+r.getResult()+"\r\n");
        });
        if(file!=null){
            saveFile(stringBuffer.toString(),file);
        }
    }

    public static  void saveFile(String content,File file){
        try {
            FileWriter fileWriter=new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static Stage setStage ;
    @FXML protected void settings(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader( getClass().getResource("set.fxml") );
        setStage = new Stage(StageStyle.DECORATED);
        try {
            setStage.setScene( new Scene( (Pane) loader.load()) );
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        setController controller =loader.<setController>getController();
        controller.initData();
        setStage.setTitle("设置");
        setStage.show();

    }

    @FXML protected void about(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("about.fxml"));
        Stage aboutStage = new Stage();
        aboutStage.setTitle("关于此软件");
        aboutStage.setScene(new Scene(root));
        aboutStage.setResizable(false);
        aboutStage.show();
    }



}
