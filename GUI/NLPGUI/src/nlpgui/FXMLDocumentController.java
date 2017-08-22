/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlpgui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import javax.sound.sampled.LineUnavailableException;
import nlp.KaldiUtil;
import nlp.NLPParameters;
import nlp.NLP;
import nlp.SoundRecordingUtil;


public class FXMLDocumentController implements Initializable {

    NLP app = new NLP();
    NLPParameters param = new NLPParameters();
    SoundRecordingUtil recorder = new SoundRecordingUtil();
    String outputAreaText="";

    @FXML
    private TextArea outputArea;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
//        outputArea.setText("Button clicked");
//        System.out.println("You clicked me!");
//        label.setText("Hello World!");
        outputAreaText="";
        outputArea.setText(outputAreaText);
        Alert alert = new Alert(AlertType.NONE, "Press OK to start recording sound", ButtonType.OK, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
            app.processIndex();
            ProcessSound();

        }
        
    }
    
    private void ProcessSound()
    {
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
                                    ProcessSoundStage2();
                               
                }
            } ) );
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
//         Alert alert = new Alert(AlertType.NONE,"Processing sound");
//         alert.show();
         
         Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SetOutputText("Processing sound...");
                        KaldiUtil util = new KaldiUtil();
                        String queryString = util.GetSoundInText();
                        SetOutputText("Your query is: "+queryString);
//                        app.processQuery("Sebutkan langkah-langkah penerimaan mahasiswa baru magister?");
                        app.processQuery(queryString);
                        SetOutputText(app.ExportResult());
                    }
           });
         
          t.start();
          
//          alert.setResult(ButtonType.CANCEL);
//          alert.close();
         
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
       
}