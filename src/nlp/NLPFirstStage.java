/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import lemurproject.indri.QueryEnvironment;


public class NLPFirstStage {
    public String Result;
    NLPParameters param;
    
    public String GetResult(String input, String query)
    {
        param = new NLPParameters();
        return this.Process(input, query);
    }
    
    private String Process(String source, String query)
    {
        this.SplitParsedDocument(source);

        NLPIndex index= new NLPIndex(param.firstStageIndexPath, param.docExtension, param.appendIndex, param.firstStageDocPath);
        index.BuildIndex();
        
        QueryEnvironment env = new QueryEnvironment();
        boolean suppressQueryResult = true;
        NLPQuery q = new NLPQuery(env, param.maximumDocs, param.firstStageIndexPath,suppressQueryResult);
        String result = q.GetResult(query,true);
        
        //fallback to modern search if tfidf failed to find records
        if(result =="")
        {
            result = q.GetResult(query);
        }
        
        return result;
    }
    
    private void SplitParsedDocument(String source)
    {
        String processSource = source;
        int i;

                i=-1;
                for(String s : param.SubMarkList)
                {
                    Integer startIndex = source.indexOf(s);
                    i++;
                    if(startIndex >=0)
                    {
                        int count = param.SubMarkList.size();
                        Integer endIndex = 0;
                        if(count >= (i+1))
                        {
                            String a = param.SubMarkList.get(i+1);
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
                        
                        
                        NLPUtil.CreateTextFile(subTitleOnly[0],i,param.firstStageDocPath);
                    }
                    else
                    {
                        break;
                    }

                } 
            
        
    }
    
}
