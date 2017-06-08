
package nlp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.indri;
import lemurproject.indri.IndexEnvironment;
import lemurproject.indri.IndexStatus;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryAnnotation;
import lemurproject.indri.QueryAnnotationNode;
import lemurproject.indri.ScoredExtentResult;
import lemurproject.indri.Specification;



public class NLP {

    boolean appendIndex = false;
    String firstStageDocPath = "./repository/temp/docs/1/";
    String firstStageIndexPath = "./repository/temp/index/1/";
    
    String secondStageDocPath = "./repository/temp/docs/2/";
    String secondStageIndexPath = "./repository/temp/index/2/";
    
    String indexPath = "./repository/index/";
    String filePath = "./repository/docs_txt_no_space/";
    String docExtension ="txt";
    private Vector dataFilesOffsetFiles=null;
    String filterString="";
    Integer maximumDocs = 10;
    QueryEnvironment queryEnvironment;
    QueryAnnotation annotationResults = null;
    ScoredExtentResult[] scored = null;
    Map annotations = null;
    String [] names = null;
    
    QueryEnvironment queryEnvironmentTemp;
    QueryAnnotation annotationResultsTemp = null;
    ScoredExtentResult[] scoredTemp = null;
    Map annotationsTemp = null;
    String [] namesTemp = null;
    
    
    List<String> QuestionWordList;
    List<String> FilterWord;
    List<String> FilterCharacter;
    int [] docids = null;
    ParsedDocument currentParsedDoc = null;
    List<String> UnecessaryWordList;
    List<String> SubMarkList;
    
    public NLP()
    {
        QuestionWordList = Arrays.asList(
                "Bagaimana",
                "Mengapa",
                "Apa",
                "Sebutkan",
                "Kapan",
                "Di mana",
                "Dimana",
                "Siapa",
                "Apa Saja"
        );
        
        FilterCharacter = Arrays.asList(
                "?"
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
        
        String queryString = "";
        Scanner userInput = new Scanner(System.in);
        System.out.println();
        System.out.println("CONTOH: Sebutkan langkah-langkah penerimaan mahasiswa baru magister?");
        System.out.print("Enter query: ");
        queryString = userInput.nextLine();
        
        main.processQuery(queryString);
       
        
    }
    
    public void processIndex()
    {
         NLPIndex index = new NLPIndex(indexPath, docExtension, appendIndex, filePath);
         index.BuildIndex();
    }
    

     
    public void processQuery(String query)
    {
        QueryEnvironment env = new QueryEnvironment();
        boolean isIndexOpen = false;
        
        
        boolean suppressQueryResult = false;    
        NLPQuery q = new NLPQuery(env, maximumDocs,indexPath,suppressQueryResult);
        
        
        isIndexOpen = q.OpenIndex(indexPath);
        if(isIndexOpen)
        {
            //query = "Sebutkan langkah-langkah penerimaan mahasiswa baru magister?";
            query = this.ParseQuery(query);
            
            String finalResult="";
            boolean hasAnswer = true;
            
            String result = q.GetResult(query,true);
            if(result == "")
            {
                System.out.println("No results");
                hasAnswer = false;
            }
            else
            {
                String firstStageProcessResult = this.FirstStageProcessParsedDocument(result,query);
                if(firstStageProcessResult == "")
                {
                    System.out.println("First Stage Query Fail");
                    hasAnswer = false;
                }
                else
                {
                    String secondStageProcessResult = this.SecondStageProcessParsedDocument(result, firstStageProcessResult);
                    if(secondStageProcessResult == "")
                    {
                        System.out.println("Second Stage Query Fail");
                        hasAnswer = false;
                    }
                    else
                    {
                        finalResult = secondStageProcessResult;
                    }
                }
            }
            
            
            
            if(hasAnswer)
            {
                System.out.println();
                System.out.println("The answer to your query: ");
                System.out.println(finalResult);
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
       for(String s:QuestionWordList)
       {
           if(query.indexOf(s)>=0)
           {
               query = NLPUtil.ReplaceWholeWordsWithEmptyString(s, query);
           }
       }
       
       //Remove useless character
       for(String s:FilterCharacter)
       {
           if(query.indexOf(s) >=0)
           {
               query = query.replace(s, "");
           }
       }
       
       //Remove unecessary word
       for(String s:UnecessaryWordList)
       {
           if(query.indexOf(s)>=0)
           {
               query = NLPUtil.ReplaceWholeWordsWithEmptyString(s, query);
           }
       }
       
       return query;
    }
 
    public void CreateTextFile(String input, int sequenceNumber, String path) {
        BufferedWriter bufferedWriter = null;
        try {
            File absPath = new File(path);
            File myFile = new File(absPath.getAbsolutePath()+"/"+sequenceNumber+".txt");
            // check if file exist, otherwise create the file before writing
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            Writer writer = new FileWriter(myFile);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(bufferedWriter != null) bufferedWriter.close();
            } catch(Exception ex){
                
            }
        }
    }
    
    public void SplitParsedDocument(String source, int stage)
    {
        String processSource = source;
        int i;
        switch(stage)
        {
            case 1:
                i=-1;
                for(String s : SubMarkList)
                {
                    Integer startIndex = source.indexOf(s);
                    i++;
                    if(startIndex >=0)
                    {
                        int count = SubMarkList.size();
                        Integer endIndex = 0;
                        if(count >= (i+1))
                        {
                            String a = SubMarkList.get(i+1);
                            endIndex = source.indexOf(a);
                            if(endIndex == -1)
                            {
                                endIndex = source.length()-1;
                            }
                        }
                        else
                        {
                            endIndex = source.length()-1;
                        }
                        processSource = source;
                        processSource = processSource.substring(startIndex,endIndex);
                        
                        String[] subTitleOnly = processSource.split("\n");
                        
                        
                        CreateTextFile(subTitleOnly[0],i,firstStageDocPath);
                    }
                    else
                    {
                        break;
                    }

                } 
                break;
                
                case 2:
            i=-1;
            for(String s : SubMarkList)
            {
                Integer startIndex = source.indexOf(s);
                i++;
                if(startIndex >=0)
                {
                    int count = SubMarkList.size();
                    Integer endIndex = 0;
                    if(count >= (i+1))
                    {
                        String a = SubMarkList.get(i+1);
                        endIndex = source.indexOf(a);
                        if(endIndex == -1)
                        {
                            endIndex = source.length()-1;
                        }
                    }
                    else
                    {
                        endIndex = source.length()-1;
                    }
                    processSource = source;
                    processSource = processSource.substring(startIndex,endIndex);
                    CreateTextFile(processSource,i,secondStageDocPath);
                }
                else
                {
                    break;
                }

            }  
            break;
        }
        
        
    }
    
    public String FirstStageProcessParsedDocument(String source, String query)
    {   
        this.SplitParsedDocument(source,1);

        NLPIndex index= new NLPIndex(firstStageIndexPath, docExtension, appendIndex, firstStageDocPath);
        index.BuildIndex();
        
        QueryEnvironment env = new QueryEnvironment();
        boolean suppressQueryResult = true;
        NLPQuery q = new NLPQuery(env, maximumDocs, firstStageIndexPath,suppressQueryResult);
        String result = q.GetResult(query,true);
        
        //fallback to modern search if tfidf failed to find records
        if(result =="")
        {
            result = q.GetResult(query);
        }
        
        return result;
    }
    
    public String SecondStageProcessParsedDocument(String source, String query)
    {   
        this.SplitParsedDocument(source,2);

        NLPIndex index= new NLPIndex(secondStageIndexPath, docExtension, appendIndex, secondStageDocPath);
        index.BuildIndex();
        
        QueryEnvironment env = new QueryEnvironment();
        boolean suppressQueryResult = true;
        NLPQuery q = new NLPQuery(env, maximumDocs, secondStageIndexPath,suppressQueryResult);
        String result = q.GetResult(query,true);
        
        //fallback to modern search if tfidf failed to find records
        if(result =="")
        {
            result = q.GetResult(query);
        }
        
        if(result != "")
        {
           result = result.substring(result.indexOf('\n')+1);
        }
        
        return result;
    }
     
}