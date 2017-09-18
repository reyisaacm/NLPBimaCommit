/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NLPUtil {
    
    public static void deleteDirectory(File dir) {
	File [] files = dir.listFiles();
	for (int i = 0; i < files.length; i++) {
	    File f = files[i];
	    if (f.isDirectory())
		deleteDirectory(f);
	    f.delete();
	}
        dir.delete();
    }
    
    public static void deleteFilesInDirectory(File dir) {
	File [] files = dir.listFiles();
	for (int i = 0; i < files.length; i++) {
	    File f = files[i];
	    if (f.isDirectory())
		deleteDirectory(f);
	    f.delete();
	}
    }
    
    public static String encodeRegexp(String regexp) {
	// rewrite shell fname pattern to regexp.
	// * -> .*
	// ? -> .?
	// Add ^,$
	// . -> \.
	String retval = "^" + regexp + "$";
	retval = retval.replaceAll("\\.", "\\.");
	retval = retval.replaceAll("\\*", ".*");
	retval = retval.replaceAll("\\?", ".?");
	return retval;
    }
    
    public static String ReplaceWholeWordsWithEmptyString(String token, String source)
    {
        source = source.toLowerCase();
        token = token.toLowerCase();
        source = source.replaceAll("\\b"+token+"\\b", "");

        return source;
    }
    
    
    public static void CreateTextFile(String input, int sequenceNumber, String path) {
        BufferedWriter bufferedWriter = null;
        try {
            File absPath = new File(path);
//            File myFile = new File(absPath.getAbsolutePath()+"/"+sequenceNumber+".txt");
            String temp = input.trim().replaceAll("\\s","");
            String fileName=temp.substring(0, Math.min(25, temp.length()));;
            File myFile = new File(absPath.getAbsolutePath()+"/"+fileName+".txt");
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
    
    public static boolean ContainExactWord(String source, String subItem){
        try
        {
            String pattern = "\\b"+subItem+"\\b";
            Pattern p=Pattern.compile(pattern);
            Matcher m=p.matcher(source);
            return m.find();
        }
        catch(Exception ex)
        {
            return true;
        }
         
    }
    
    public static String TrimAndLowerCase(String input)
    {
        return input.trim().toLowerCase();
    }
    
    public static String RemoveEscapeCharacter(String input)
    {
        List<String> escapeCharacterList = Arrays.asList(
                "\\n",
                "\\t"
        );
        
        for(String s: escapeCharacterList)
        {
                input = input.replace(s, "");
        }
        
        return input;
    }
    

}
