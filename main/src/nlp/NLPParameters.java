/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryAnnotation;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;


public class NLPParameters {
    public String operationMode = OperationMode.TEXTINPUT.toString();
    
    public String exampleText = "CONTOH: Sebutkan langkah-langkah penerimaan mahasiswa baru magister?";
    public String soundInputText = "Speak your query: ";
    public String soundInputConfirmText = "Your speech text is: ";
    public String soundInputConfirmText2 = "Is this correct (y/n)?: ";
    public String textInputText = "Type your query: ";

    public boolean appendIndex = false;
    public String firstStageDocPath = "./repository/temp/docs/1/";
    public String firstStageIndexPath = "./repository/temp/index/1/";
    
    public String secondStageDocPath = "./repository/temp/docs/2/";
    public String secondStageIndexPath = "./repository/temp/index/2/";
    
    public String indexPath = "./repository/index/";
    public String filePath = "./repository/docs_txt_no_space/";
    public String docExtension ="txt";
    public Integer maximumDocs = 10;
    
    public int RECORD_TIME = 10000;
    public String recordPath = "./tmp/rec.wav";
    public String recordFolderPath = "./tmp";

    
    public List<String> QuestionWordList;
    public List<String> FilterWord;
    public List<String> FilterCharacter;
    public List<String> UnecessaryWordList;
    public List<String> SubMarkList;
    
    
    public enum OperationMode {
        FULL,
        SOUNDINPUT,
        TEXTINPUT,
        TEXTINPUT_SOUNDOUTPUT;
    }
    
    private void CreateDirectoryIfNotExist()
    {
        new File(firstStageDocPath).mkdirs();
        new File(firstStageIndexPath).mkdirs();
        new File(secondStageDocPath).mkdirs();
        new File(secondStageIndexPath).mkdirs();
        new File(indexPath).mkdirs();
        new File(filePath).mkdirs();
        new File(recordFolderPath).mkdirs();

    }
    
    public NLPParameters()
    {
        CreateDirectoryIfNotExist();
        QuestionWordList = Arrays.asList(
                "bagaimana",
                "mengapa",
                "apa",
                "sebutkan",
                "kapan",
                "di mana",
                "dimana",
                "siapa",
                "apakah",
                "sebelum",
                "sesudah",
                "setelah",
                "apa saja"
        );
        
        FilterCharacter = Arrays.asList(
                "?",
                "."
        );
        
        UnecessaryWordList = new ArrayList<String>();
        
        SubMarkList = Arrays.asList(
                "a.\t",
                "b.\t",
                "c.\t",
                "d.\t",
                "e.\t",
                "f.\t",
                "g.\t",
                "h.\t",
                "i.\t",
                "j.\t",
                "k.\t",
                "l.\t",
                "m.\t",
                "n.\t",
                "o.\t",
                "p.\t",
                "q.\t",
                "r.\t",
                "s.\t",
                "t.\t",
                "v.\t",
                "w.\t",
                "x.\t",
                "y.\t",
                "z.\t"
        );
    }
}
