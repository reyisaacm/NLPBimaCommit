/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;
import java.util.Map;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryAnnotation;
import lemurproject.indri.QueryAnnotationNode;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;
import lemurproject.lemur.RetMethodManager;


public class NLPQuery {
      public QueryEnvironment queryEnvironment;
      QueryAnnotation annotationResults = null;
      ScoredExtentResult[] scored = null;
      Map annotations = null;
      String [] names = null;
      int [] docids = null;
      ParsedDocument currentParsedDoc = null;
      Integer maximumDocs;
      String indexPath;
      boolean suppressQueryResult;
    
      public NLPQuery(QueryEnvironment queryEnvironment, Integer maximumDocs, String indexPath, boolean suppressQueryResult)
      {
          this.queryEnvironment = queryEnvironment;
          this.maximumDocs = maximumDocs;
          this.indexPath = indexPath;
          this.suppressQueryResult = suppressQueryResult;
      }
      
      public String GetResult(String query,boolean isLegacy)
      {
          
          String result = "";
//          this.OpenIndex(indexPath);
//          result = RunQuestion(query);

            NLPQueryLt qt = new NLPQueryLt(query, indexPath,maximumDocs,suppressQueryResult);
            result = qt.GetResult();
            
          return result;
      }
      
      public String GetResult(String query)
      {
          
          String result = "";
          this.OpenIndex(indexPath);
          result = RunQuestion(query);

//            NLPQueryLt qt = new NLPQueryLt(query, indexPath,maximumDocs);
//            result = qt.GetResult();
            
          return result;
      }
    
      public boolean OpenIndex(String indexPath) {
            boolean retVal = true;
	    File file = new File(indexPath);
	    String index = file.getAbsolutePath();
            
            if (! file.exists()) {
		index = file.getParentFile().getAbsolutePath();
	    }
             
	    try {
		queryEnvironment.addIndex(index);
	    } catch (Exception e){
                retVal = false;
		System.out.println(e.toString());
	    }
            
            return retVal;
    }
      
        public String RunQuestion(String questionString) {

		    String question = questionString;
		  
		    // results have extents and matches.
		    try {
			try {
			    annotationResults = queryEnvironment.runAnnotatedQuery( question, maximumDocs);
			} catch (Exception exc2) {
                            System.out.println(exc2.toString());
			}
 
			scored = annotationResults.getResults();
			try {
			    names = queryEnvironment.documentMetadata( scored, "docno" );
			} catch (Exception exc1) {
			    // no titles, something bad happened.
//			    names = new String[scored.length];
//			    //			    error(exc.toString());
//			    error("No docs: " + exc1.toString());
                            System.out.println(exc1.toString());
			}
//			try {
//			    titles = env.documentMetadata( scored, "title" );
//			} catch (Exception exc) {
//			    // no titles, something bad happened.
//			    titles = new String[scored.length];
//			    for (int i = 0; i < titles.length; i++)
//				titles[i] = "";
//			    //			    error(exc.toString());
//			    error("No titles: " + exc.toString());
//			}
			
			docids = new int[scored.length];
			for (int j = 0; j < scored.length; j++)
			    docids[j] = scored[j].document;
			// initialize scored docs table
//			for( int i = 0; i < scored.length; i++ ) {
//			    m.setValueAt(i, scored[i].score, trim(names[i]), 
//					 titles[i]);
//			}
			// initialize query tree view for doc text frame
			// keys query node names (as inserted in QueryTree).
			// get root node
//			QueryAnnotationNode query = annotationResults.getQueryTree();
//			annotations = annotationResults.getAnnotations();
			// update the JTree
//			DefaultTreeModel tree = makeTreeModel(query);
//			docQueryTree.setModel(tree);
			//
		    } catch (Exception e) {
                        System.out.println(e.toString());
		    }
                    
                    return printQueryResult();
    }
        
    public String printQueryResult()
    {
        if(!suppressQueryResult)
        {
             System.out.println("Query Results: ");
            int i =0;
            for(ScoredExtentResult s : scored)
            {
                File f = new File(names[i]);

                System.out.print(f.getName() + " ");
                System.out.println(s.score);
                i++;
            }

            System.out.println();
        }
       
//        System.out.println("Content: ");
        String parsedDocumentString = GetParsedDocumentString();
        
        //String processedDocument = ProcessParsedDocument(parsedDocumentString);
        
        return parsedDocumentString;
    }
    
    public String GetParsedDocumentString() {
        // get the doc text
        String name = names[0];
        // get the parsed document
        int [] ids = new int[1];
        ids[0] = docids[0];

        ParsedDocument[] docs = null;
        try {
            docs = queryEnvironment.documents(ids);
        } catch (Exception exc1) {
            System.out.println(exc1.toString());
        }

        currentParsedDoc = docs[0];
        // use content here to skip trecweb format headers
        String myDocText = currentParsedDoc.content;
        
        return myDocText;
    }
}
