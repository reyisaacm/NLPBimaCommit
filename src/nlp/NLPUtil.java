/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;


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
}
