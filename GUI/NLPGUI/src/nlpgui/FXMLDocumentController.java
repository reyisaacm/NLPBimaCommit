/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlpgui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import javax.sound.sampled.LineUnavailableException;
import nlp.KaldiUtil;
import nlp.NLPParameters;
import nlp.NLP;
import nlp.SoundRecordingUtil;


public class FXMLDocumentController implements Initializable {

    NLP app;
    NLPParameters param = new NLPParameters();
    SoundRecordingUtil recorder = new SoundRecordingUtil();
    String outputAreaText="";

    @FXML
    private TextArea outputArea;
    
    @FXML
    private Button startButton;
    
    @FXML
    private TextArea rankingArea;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
//        outputArea.setText("Button clicked");
//        System.out.println("You clicked me!");
//        label.setText("Hello World!");
        outputAreaText="";
        outputArea.setText(outputAreaText);
        rankingArea.setText("");

        Alert alert = new Alert(AlertType.NONE, "Press OK to start recording sound", ButtonType.OK, ButtonType.CANCEL);
        alert.showAndWait();
        app = new NLP();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
            app.processIndex();
            ProcessSound();

        }
        
    }
    
    private void ProcessSound()
    {
        outputAreaText="";
        outputArea.setText(outputAreaText);
        rankingArea.setText("");
        //            Dialog soundRecording = new Dialog();
//            soundRecording.getDialogPane().ge
//            soundRecording.showAndWait();
            Alert soundRecording = new Alert(AlertType.NONE,"Recording");
            soundRecording.show();

            Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        app.GetSoundInput();
                    }
           });
            
            Timeline idlestage = new Timeline( new KeyFrame( Duration.seconds((param.RECORD_TIME/1000) + 2), new EventHandler<ActionEvent>()
            {

                @Override
                public void handle( ActionEvent event )
                {

                                    soundRecording.setResult(ButtonType.CANCEL);
                                    soundRecording.close();
                                    
                               
                }
            } ) );
            idlestage.setOnFinished(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ProcessSoundStage2();                 
                        }
                    });

                }
            });
            
            idlestage.setCycleCount( 1 );
            idlestage.play();
            t.start();
            //SetOutputText("\n Recording");


    }
    
    private void SetOutputText(String textToAppend)
    {
        outputAreaText += textToAppend;
        outputAreaText += "\n";
        outputArea.setText(outputAreaText);
    }
   

    
    private void ProcessSoundStage2()
    {
         
         Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startButton.setDisable(true);
                        SetOutputText("Processing sound...");
                        KaldiUtil util = new KaldiUtil();
                        String queryString = util.GetSoundInText();
                        startButton.setDisable(false);
                        ProcessSoundStage3(queryString);
                    }
           });
         
          t.start();
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                 SetOutputText("Processing sound...");
//                  KaldiUtil util = new KaldiUtil();
//                  String queryString = util.GetSoundInText();
//                  ProcessSoundStage3(queryString);
//            }
//        });
         

//          alert.setResult(ButtonType.CANCEL);
//          alert.close();
         
    }
    
    private void ProcessSoundStage3(String query)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ButtonType retrySound = new ButtonType("Retry sound input");
                ButtonType inputText = new ButtonType("Manual text input");
                  Alert alert = new Alert(AlertType.NONE,"Your sound input is: \r"+query+"\rIs this correct?", ButtonType.YES,retrySound,inputText,ButtonType.CANCEL);
                        alert.showAndWait();
                  if(alert.getResult() == ButtonType.YES)
                  {
                      ProcessSoundStage4(query);
                  }
                  else if(alert.getResult() == retrySound)
                  {
                      RetrySoundInput();
                  }
                  else if(alert.getResult() == ButtonType.CANCEL)
                  {
                      outputArea.setText("");
                  }
                  else if(alert.getResult() == inputText)
                  {
                      ManualTextInput(query);
                  }
                        
            }
        });
           
    }
    
    private void RetrySoundInput()
    {
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                outputArea.setText("");
                app=new NLP();
                app.processIndex();
                ProcessSound();
            }
        });
    }
    
    private void ManualTextInput(String queryString)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Manual input");
        alert.setHeaderText("Type your query:");

        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setContent(expContent);

        alert.showAndWait();
        if(textArea.getText() != "")
        {
            queryString = textArea.getText();
            ProcessSoundStage4(queryString);
        }
        else
        {
            ProcessSoundStage4(queryString);
        }
    }
    
    private void ProcessSoundStage4(String queryString)
    {
        SetOutputText("Your query is: "+queryString);
//        app.processQuery("Sebutkan langkah-langkah penerimaan mahasiswa baru magister?");
        app.processQuery(queryString);
        SetOutputText(app.ExportResult());
        String ranking = app.ExportRanking();
        rankingArea.setText(ranking);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
       
}
