/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.util.ArrayList;
import java.util.List;


public class NLPThirdStage {
    
    String rankingResults="";
    
    public String GetRankingResults()
    {
        return rankingResults;
    }
    
    public String GetResult(String source, String query, String storedQuestionWord)
    {
        return this.Process(source, query,storedQuestionWord);
    }
    
    private String Process(String source, String query, String storedQuestionWord)
    {
        rankingResults += "Query Results for stage 3: \r\n";
        String result="";
        //System.out.println(storedQuestionWord);
        if(storedQuestionWord != null)
        {
            switch(storedQuestionWord)
            {
                case "apakah":
                    result = this.ProcessAnswerYesOrNo(source, query);
                    break;
                case "setelah":
                case "selanjutnya":
                case "sesudah":
                    result = this.ProcessAnswerAfterAndBefore(source, query,true);
                    break;
                case "sebelum":
                    result = this.ProcessAnswerAfterAndBefore(source, query,false);
                    break;
                default:
                    result = query;
            }
        }
        
        
        return result;
    }
    
    private String ProcessAnswerAfterAndBefore(String source, String query, boolean isAfter)
    {
        if(isAfter)
        {
            rankingResults += "Detected query question type is 'After' \r\n";
        }
        else
        {
            rankingResults = "Detected query question type is 'Before' \r\n";
        }
        String result = "";
        
        String lineDelimiter = "•";
        
        query = NLPUtil.TrimAndLowerCase(query);
        source = NLPUtil.TrimAndLowerCase(source);
        
        String[] splitQueryTemp = query.split(lineDelimiter);
//        List<String> splitQuery = new ArrayList();
//        
//        for(String s:splitQueryTemp)
//        {
//            if(s !="")
//            {
//                splitQuery.add(NLPUtil.RemoveEscapeCharacter(s));
//            }
//        }

        
        List<Double> scoreList = new ArrayList();
        List<String> wordList = new ArrayList();
        int indexOfMaxValue=0;
        for(String s:splitQueryTemp)
        {
            if(NLPUtil.ContainExactWord(source, s.trim()) && !s.equals("")) //perfect match
            {
                scoreList.add(100.0);
                wordList.add(s);
                rankingResults += "--> "+s.trim()+" "+100+"\r\n";
            }
            else
            {
                String [] lst = s.split("\\s+");
                int score = 0;
                int length = splitQueryTemp.length;
                for(String a:lst)
                {
                    if(!a.equals(""))
                    {
                        if(NLPUtil.ContainExactWord(source, a))
                        {
                            score++;
                        }
                    }
                   
                }
                double finalScore = ((double)score/(double)length) * 100.0;
                
                if(!s.equals(""))
                {
                    scoreList.add(finalScore);
                    wordList.add(s);
                    rankingResults += "--> "+s.trim()+" "+finalScore+"\r\n";
                }
                
                
            }
            
               
        }
        
        indexOfMaxValue = GetMaxValueIndex(scoreList);
        
        if(isAfter)
        {
            if((indexOfMaxValue+1) > splitQueryTemp.length - 1)
            {
                result = "None";
            }
            else
            {
                result = splitQueryTemp[(indexOfMaxValue+1)].trim();
                if(result.equals(""))
                {
                    result="None";
                }
            }
        }
        else
        {
            if((indexOfMaxValue-1) < 0)
            {
                result ="None";
            }
            else
            {
                result = splitQueryTemp[(indexOfMaxValue-1)].trim();
                if(result.equals(""))
                {
                    result="None";
                }
            }
        }
        
        
//        System.out.println("Source : "+source);
//        System.out.println("Query :"+query);

        return result;
    }
    
    private int GetMaxValueIndex(List<Double> list)
    {
        int index = 0;
        double largest = Integer.MIN_VALUE;
        for ( int i = 0; i < list.size(); i++ )
        {
            if ( list.get(i) > largest )
            {
                largest = list.get(i);
                index = i;
            }
        }
        
        return index;
    }
    
    private String ProcessAnswerYesOrNo(String source, String query)
    {
        rankingResults += "Detected query question type is 'Yes or No' \r\n";
        String result = "";
        double THRESHOLD_ANSWER_YES_OR_NO = 50;
        
//        System.out.println("Source : "+source);
//        System.out.println("Query :"+query);
        
        query = NLPUtil.TrimAndLowerCase(query);
        source = NLPUtil.TrimAndLowerCase(source);
        
        String[] splitQuery = query.split("\\s+");
        
        int score = 0;
        int length = splitQuery.length;
        
        for(String s:splitQuery)
        {
            if(NLPUtil.ContainExactWord(source, s))
            {
                score++;
            }
        }
        
        double finalScore = ((double)score/(double)length) * 100.0;
        rankingResults += "Score: "+finalScore+"\r\n";

        
        if(finalScore >= THRESHOLD_ANSWER_YES_OR_NO)
        {
            result = "Yes";
        }
        else
        {
            result = "No";
        }
        
        
        return result;
    }
    
}
