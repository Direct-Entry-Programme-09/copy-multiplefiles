package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class MainFormController {
    public JFXButton btnChooseSource;
    public VBox vbxFileList;
    public JFXButton btnChooseDestination;
    public JFXButton btnCopy;
    public Rectangle rctContainer;
    public Rectangle rctLoad;
    public Label lblDirectory;

    private List<File> sourceFiles;

    private File destiDir;

    public void initialize(){
        btnCopy.setDisable(true);
        lblDirectory.setText("Select a directory");
        if (sourceFiles!=null && destiDir!=null){
            btnCopy.setDisable(false);
        }
    }

    public void btnChooseSourceOnAction(ActionEvent actionEvent) {

        FileChooser fc = new FileChooser();

        File file = new File("/home/nipunija/Desktop");

        fc.setInitialDirectory(file);

        sourceFiles =fc.showOpenMultipleDialog(lblDirectory.getScene().getWindow());


        if (sourceFiles!=null){
            for (int i = 0; i < sourceFiles.size(); i++) {
                Label lblName = new Label();
                lblName.setText(sourceFiles.get(i).getName());
                lblName.setPrefWidth(90);

                Label lblSize = new Label();
                lblSize.setText(sourceFiles.get(i).length()>1000 ? sourceFiles.get(i).length()/1024+" kB":sourceFiles.get(i).length()+" bytes");

                HBox hBox = new HBox();
                hBox.getChildren().add(lblName);
                hBox.getChildren().add(lblSize);

                vbxFileList.getChildren().add(hBox);
            }
        }else {
            Label label = new Label();
            label.setText("No File Selected");

            vbxFileList.getChildren().add(label);
        }

        btnCopy.setDisable(sourceFiles==null || destiDir==null);


    }

    public void btnChooseDestinationOnAction(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();

        File file = new File("/home/nipunija/Documents");

        dc.setInitialDirectory(file);

        destiDir = dc.showDialog(lblDirectory.getScene().getWindow());
        if (destiDir!=null){
            lblDirectory.setText(destiDir.getAbsolutePath());
        }else {
            lblDirectory.setText("No directory selected");
        }

        btnCopy.setDisable(sourceFiles==null || destiDir==null);
    }

    public void btnCopyOnAction(ActionEvent actionEvent) throws IOException {



            for (File source : sourceFiles) {

                    File file = new File(destiDir, source.getName());
                    if (!file.exists()) {

                        file.createNewFile();

                    } else {
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to override the file", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (buttonType.equals(ButtonType.NO)) {
                            return;
                        }
                    }
                    new Thread(()->{
                        try {
                            FileInputStream fis =new FileInputStream(source);

                            FileOutputStream fos = new FileOutputStream(file);

                            for (int i = 0; i < source.length(); i++) {

                                fos.write(fis.read());

                                int l = i;
                                Platform.runLater(() -> {
                                    rctLoad.setWidth((rctContainer.getWidth() / source.length()) * l);
                                });

                            }
                            fis.close();
                            fos.close();

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }).start();



            }

            new Alert(Alert.AlertType.INFORMATION, "Successfully copied the files").show();

            vbxFileList.getChildren().clear();
            lblDirectory.setText("Select a directory");

    }
}
