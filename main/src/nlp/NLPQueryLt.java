/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;
import java.io.IOException;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.ScoredExtentResult;
import lemurproject.lemur.ArrayAccumulator;
import lemurproject.lemur.DocumentManager;
import lemurproject.lemur.Index;
import lemurproject.lemur.IndexedReal;
import lemurproject.lemur.RetMethodManager;
import lemurproject.lemur.RetrievalMethod;
import lemurproject.lemur.lemur;
import lemurproject.lemur.IndexManager;
import lemurproject.lemur.MatchInfo;
import lemurproject.lemur.Query;
import lemurproject.lemur.TMatch;
import lemurproject.lemur.ui.EvalTableModel;
import sun.nio.cs.US_ASCII;


public class NLPQueryLt {
    
    Index index;
    ArrayAccumulator accum = null;
    RetrievalMethod model = null;
    String searchQuery;
    String indexPath;
    Integer maximumDocs;
    ScoredExtentResult[] data;
    String[] names;
    ParsedDocument currentParsedDoc = null;
    boolean hasResult;
    boolean suppressQueryResult;
    String rankingResults="";
    
    public String GetRankingResults()
    {
          return rankingResults;
    }
    
    public NLPQueryLt(String searchQuery, String indexPath, Integer maximumDocs,boolean suppressQueryResult)
    {
        this.searchQuery = searchQuery;
        this.indexPath = indexPath;
        this.maximumDocs = maximumDocs;
        this.names = new String[maximumDocs];
        this.hasResult = true;
        this.suppressQueryResult = suppressQueryResult;
    }
    
    public String GetResult(int stage)
    {
        
        this.SendQuery(searchQuery);
        
        if(hasResult)
        {
            return this.printQueryResult(stage);
        }
        else
        {
            return "";
        }
        
    }
    
    private void SendQuery(String query) {
        
        String retmethod = "tfidf";
//        String retmethod = "okapi";
        File file = new File(indexPath);
        
        if (model == null) {
            // if params change, have to remake the model.
            try {
                index = IndexManager.openIndex(file.getAbsolutePath());
                index.setProps();

                
                lemur.ParamSet("feedbackPosCoeff", "0.4");
                lemur.ParamSet("doc.tfMethod", "0");
                lemur.ParamSet("doc.bm25K1", "1.2");
                lemur.ParamSet("doc.bm25B", "0.75");
                lemur.ParamSet("query.tfMethod", "0");
                lemur.ParamSet("query.bm25K1", "1.2");

//                  lemur.ParamSet("BM25K1", "1.2");
//                  lemur.ParamSet("BM25B", "0.75");
//                  lemur.ParamSet("BM25K3", "7.0");
//                  lemur.ParamSet("BM25QTF", "0.5");
//                  lemur.ParamSet("feedbackTermCount","50");
                    
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            try {
                accum = new ArrayAccumulator(index.docCount());
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            try {
                model = RetMethodManager.createModel(index, accum, retmethod);
            } catch (Exception e) {
                System.out.println(e.toString());
                
            }
        }
        IndexedReal[] res = null;
        try {
            res =  model.runQuery(searchQuery);
            if(res.length == 0)
            {
                hasResult = false;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        if(hasResult)
        {
            data = this.FormatResults(res);
        }
        //System.out.println(res[0]);
        
    }
    
    private ScoredExtentResult[] FormatResults(IndexedReal[] results) {
        
        int count = min(maximumDocs,results.length);
        
        ScoredExtentResult[] collection = new ScoredExtentResult[count];
        
        
        try{
            for(int i=0; i<count; i++)
            {

                DocumentManager dm = index.docManager(i+1);
                String docid = (index.document(results[i].ind));
                String score = Double.toString(results[i].val);
                //String a = dm.getDoc(docid);
                
                ScoredExtentResult s = new ScoredExtentResult();
                s.score = Double.parseDouble(score);
                collection[i] = s;
                names[i] = docid;
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
        
       
        return collection;
    }
    
    public String printQueryResult(int stage)
    {
        if(!suppressQueryResult)
        {
//            System.out.println("Query Results: ");
            rankingResults += "Query Results for stage "+stage+":\r\n";
            if(stage ==2)
            {
                rankingResults += "Fetching contents of the following document:\r\n";
            }
            int i =0;
            for(ScoredExtentResult s : data)
            {
                File f = new File(names[i]);
                
                rankingResults += f.getName() + " " + s.score + " \r\n";

//                System.out.print(f.getName() + " ");
//                System.out.println(s.score);
                i++;
            }
        
            rankingResults +="\r\n";
        }
        
//        System.out.println("Content: ");
        String parsedDocumentString = GetParsedDocumentString();
        //String parsedDocumentString ="";
        //String processedDocument = ProcessParsedDocument(parsedDocumentString);
        
        return parsedDocumentString;
    }
    
    public String GetParsedDocumentString() {
        
        String retVal = "";
        try
        {
            File f = new File(names[0]);
            retVal = readFile(f.getAbsolutePath(),StandardCharsets.UTF_8);
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        return retVal;
    }
    
    private static String readFile(String path, Charset encoding) 
  throws IOException 
    {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
    }
    
}
