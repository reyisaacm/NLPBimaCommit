
package nlp;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.LineUnavailableException;
import lemurproject.indri.QueryEnvironment;
import nlp.NLPParameters.OperationMode;
import static nlp.NLPUtil.deleteFilesInDirectory;


public class NLP {

    
    NLPParameters param;
    String storedQuestionWord=null;
    String exportedResult ="";
    String exportedRanking = "";
    
    public NLP()
    {
        param = new NLPParameters();
        deleteFilesInDirectory(new File(param.firstStageDocPath));
        deleteFilesInDirectory(new File(param.secondStageDocPath));
    }
    
    public String ExportResult()
    {
        return exportedResult;
    }
    
    public String ExportRanking()
    {
        return exportedRanking;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting program");
        NLP main = new NLP();
        
        String indexString ="";
        Scanner indexInput = new Scanner(System.in);
        System.out.println();
        
        System.out.print("Rebuild Index (Y/N): ");
        indexString = indexInput.next();
        if(indexString.toLowerCase().equals("y"))
        {
            main.processIndex();
        }
        
        
        main.Begin();
        
    }
    
    public void Begin()
    {
        String queryString = "";

        System.out.println();
        System.out.println(param.exampleText);
        
        if(param.operationMode == OperationMode.FULL.toString() || param.operationMode == OperationMode.SOUNDINPUT.toString())
        {
            System.out.print(param.soundInputText);
            this.GetSoundInput();
            KaldiUtil util = new KaldiUtil();
            queryString = util.GetSoundInText();
            Scanner userConfirm = new Scanner(System.in);
        
            System.out.println(param.soundInputConfirmText+queryString);
            System.out.print(param.soundInputConfirmText2);
            String inp = userConfirm.nextLine();

            if(inp.toLowerCase().equals("y"))
            {
    //            queryString="Sebutkan langkah-langkah penerimaan mahasiswa baru magister?";
                this.processQuery(queryString);
            }
            else
            {
                this.Begin();
            }
        }
        else if(param.operationMode == OperationMode.TEXTINPUT.toString() || param.operationMode == OperationMode.TEXTINPUT_SOUNDOUTPUT.toString())
        {
            Scanner userInput= new Scanner(System.in);

            System.out.print(param.textInputText);
            queryString = userInput.nextLine();
            this.processQuery(queryString);

        }
        
       
        
        
    }
    
    public void GetSoundInput()
    {
        File wavFile = new File(param.recordPath);
         
        final SoundRecordingUtil recorder = new SoundRecordingUtil();
         
        // create a separate thread for recording
        Thread recordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Start recording...");
                    recorder.start();
                } catch (LineUnavailableException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }              
            }
        });
         
        recordThread.start();
         
        try {
            Thread.sleep(param.RECORD_TIME);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
         
        try {
            recorder.stop();
            recorder.save(wavFile);
            System.out.println("STOPPED");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
         
        System.out.println("DONE");

        
    }
    
    public void processIndex()
    {
         NLPIndex index = new NLPIndex(param.indexPath, param.docExtension, param.appendIndex, param.filePath);
         index.BuildIndex();
    }
    
    private String ExportResult(String text)
    {
        exportedResult +=text;
        exportedResult += "\n";
        return text;
    }
    

     
    public void processQuery(String query)
    {
        QueryEnvironment env = new QueryEnvironment();
        boolean isIndexOpen = false;
        
        
        boolean suppressQueryResult = false;    
        NLPQuery q = new NLPQuery(env, param.maximumDocs,param.indexPath,suppressQueryResult);
        
        isIndexOpen = q.OpenIndex(param.indexPath);
        if(isIndexOpen)
        {
            //query = "Sebutkan langkah-langkah penerimaan mahasiswa baru magister?";
            query = this.ParseQuery(query);
            
            String finalResult="";
            boolean hasAnswer = true;
            
            String result = q.GetResult(query,true,0);
            exportedRanking += q.GetRankingResults();

            if(result == "")
            {
                System.out.println(ExportResult("No results"));
                hasAnswer = false;
            }
            else
            {
                NLPFirstStage st1 = new NLPFirstStage();
                String firstStageProcessResult = st1.GetResult(result,query);
                

                if(firstStageProcessResult == "")
                {
                    System.out.println(ExportResult("No match found in document sub section. Displaying whole document..."));
                    System.out.println("");
                    System.out.println(ExportResult(result));
                    hasAnswer = false;
                }
                else
                {
                    exportedRanking += st1.GetRankingResults();
                    NLPSecondStage st2 =new NLPSecondStage();
                    String secondStageProcessResult = st2.GetResult(result, firstStageProcessResult);
                    if(secondStageProcessResult == "")
                    {
                        System.out.println(ExportResult("Second Stage Query Fail"));
                        hasAnswer = false;
                    }
                    else
                    {
                        exportedRanking += st2.GetRankingResults();
                        NLPThirdStage st3 = new NLPThirdStage();
                        String thirdStageProcessResult = st3.GetResult(query, secondStageProcessResult, storedQuestionWord);
                        if(thirdStageProcessResult == "")
                        {
                            System.out.println(ExportResult("Third Stage Query Fail"));
                            hasAnswer = false;
                        }
                        else
                        {
                            exportedRanking += st3.GetRankingResults();
                            finalResult = thirdStageProcessResult;
                        }
                        
                    }
                }
            }
            
            
            
            if(hasAnswer)
            {
                System.out.println();
                System.out.println("Exported Ranking:"+exportedRanking);
                System.out.println(ExportResult("The answer to your query: "));
                System.out.println(ExportResult(finalResult));
            }
            
            
        }
        else
        {
            System.out.println("Index failed to open");
        }
        
    }
    
    public String ParseQuery(String query)
    {
       //Remove question word
       for(String s:param.QuestionWordList)
       {
           if(NLPUtil.ContainExactWord(query.toLowerCase(), s.toLowerCase()))
           {
               storedQuestionWord = s;
               query = NLPUtil.ReplaceWholeWordsWithEmptyString(s, query);
           }
       }
       
       //Remove useless character
       for(String s:param.FilterCharacter)
       {
           if(query.indexOf(s) >=0)
           {
               query = query.replace(s, "");
           }
       }
       
       //Remove unecessary word
       for(String s:param.UnecessaryWordList)
       {
           if(query.indexOf(s)>=0)
           {
               query = NLPUtil.ReplaceWholeWordsWithEmptyString(s, query);
           }
       }
       
       return query;
    }
   
}