/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;
import java.util.Vector;
import lemurproject.indri.IndexEnvironment;
import lemurproject.indri.Specification;


public class NLPIndex {
    
        public String indexPath;
        public String docExtension;
        public boolean appendIndex;
        public String filePath;
        
        public NLPIndex(String indexPath, String docExtension, boolean appendIndex, String filePath)
        {
            this.indexPath = indexPath;
            this.docExtension = docExtension;
            this.appendIndex = appendIndex;
            this.filePath = filePath;
        }
        
        private String[] FormatDataFiles() {	
	Vector accumulator = new Vector();
	String [] retval = new String[0];

        File file = new File(filePath);
        FormatDataFiles(file, accumulator);		
	retval = (String[]) accumulator.toArray(retval);
	return retval;
    }
    
     private void FormatDataFiles(File file, Vector accum) {

          if (file.isDirectory()) {
          // handle directory
          File [] files = file.listFiles();
          for (int i = 0; i < files.length; i++) {
             accum.add(files[i].getAbsolutePath());
          }
      } else {
             accum.add(file.getAbsolutePath());
      }      
     }
    
        public void BuildIndex() {
  
//        System.out.println();
//        System.out.println("Building Index...");
  
	// if iname is not an absolute path, rewrite it to be so.
	File idx = new File(indexPath);
        if(idx.exists())
        {
            NLPUtil.deleteDirectory(idx);
        }
        
	// construct IndexEnvironment
	// set parameters
	// go.
	IndexEnvironment env = new IndexEnvironment();
  
        try {
          // memory
          env.setMemory(128000000);

          // set the field definitions from the table
          java.util.Vector fieldVec=new java.util.Vector();
          java.util.Vector numericFields=new java.util.Vector();
          
//          int numFields=fieldTable.getModel().getRowCount();
//          for (int f=0; f < numFields; f++) {
//                  String thisFieldName=((String)fieldTable.getModel().getValueAt(f, 0)).trim();
//                  Boolean thisFieldNumeric=(Boolean)fieldTable.getModel().getValueAt(f, 1);
//                  if ((thisFieldName.length() > 0) && (!fieldVec.contains(thisFieldName))) {
//                          fieldVec.add(thisFieldName);
//                          if (thisFieldNumeric.booleanValue()) {
//                                  numericFields.add(thisFieldName);
//                          }
//                  }
//          }

          String[] fields=new String[fieldVec.size()];
          for (int f=0; f < fieldVec.size(); f++) {
                  fields[f]=(String)fieldVec.get(f);
          }
          env.setIndexedFields(fields);

          // now, if there's any numeric fields, we need to set those...
//          for (int f=0; f < numericFields.size(); f++) {
//                  String thisNumericField=(String)numericFields.get(f);
//                  env.setNumericField(thisNumericField, true, "NumericFieldAnnotator");
//          }
//
//          String [] metafields = colFields.getText().split(",");;
//          String [] stopwords = new String[0];
//
//                // this needs to address the forward/backward/metadata distinction.
//          env.setMetadataIndexedFields(metafields, metafields);	
//          String stops = stopwordlist.getText();
//          if (! stops.equals("")) {
//              // load the stopwords into an array.
//              Vector tmp = new Vector();
//              try {
//            BufferedReader buf = new BufferedReader(new FileReader(stops));
//            String line;
//            while((line = buf.readLine()) != null) {
//                tmp.add(line.trim());
//            }
//            buf.close();
//              } catch (IOException ex) {
//            // unlikely.
//            showException(ex);
//              }
//              stopwords = (String[]) tmp.toArray(stopwords);
//              env.setStopwords(stopwords);
//          }

//          if (doStem.isSelected()) {
//              String stemmer = (String)stemmers.getSelectedItem();
//              env.setStemmer(stemmer);
//          }
          // add an empty string option

          String fileClass = docExtension;
          // augment the environment as required
          Specification spec = env.getFileClassSpec(fileClass);
          java.util.Vector vec = new java.util.Vector();
          java.util.Vector incs = null;
          if (spec.include.length > 0)
              incs = new java.util.Vector();

          // indexed fields
          for (int i = 0; i < spec.index.length; i++)
              vec.add(spec.index[i]);
          for (int i = 0; i < fields.length; i++) {
              if (vec.indexOf(fields[i]) == -1)
            vec.add(fields[i]);
              // add to include tags only if there were some already.
              if (incs != null && incs.indexOf(fields[i]) == -1)
            incs.add(fields[i]);
          }

          if (vec.size() > spec.index.length) {
              // we added something.
              spec.index = new String[vec.size()];
              vec.copyInto(spec.index);
          }
          /* FIXME: forward/backward and plain metadata have to address the
                   issue of inserting entries for all names that conflate to a given
                   name.
                 */
          // metadata fields.
          vec.clear();
          for (int i = 0; i < spec.metadata.length; i++)
              vec.add(spec.metadata[i]);
//          for (int i = 0; i < metafields.length; i++) {	    
//              if (vec.indexOf(metafields[i]) == -1)
//            vec.add(metafields[i]);
//              // add to include tags only if there were some already.
//              if (incs != null && incs.indexOf(metafields[i]) == -1)
//            incs.add(metafields[i]);
//          }

          if (vec.size() > spec.metadata.length) {
              // we added something.
              spec.metadata = new String[vec.size()];
              vec.copyInto(spec.metadata);
          }
          // update include if needed.
          if (incs != null && incs.size() > spec.include.length) {
              spec.include = new String[incs.size()];
              incs.copyInto(spec.include);
          }
          // update the environment.
          env.addFileClass(spec);
          

          String [] datafiles = FormatDataFiles();
//          String [] dummyStringArray=new String[0];
          //String [] offsetFiles=(String[])dataFilesOffsetFiles.toArray(dummyStringArray);

          // create a new empty index (after parameters have been set).
          if (appendIndex)
              env.open(indexPath);
          else 
              env.create(indexPath);

          for (int i = 0; i < datafiles.length; i++){
              String fname = datafiles[i];
              
              env.addFile(fname, fileClass);
          }
          
//          System.out.println("Index build successful");
//          System.out.println("Document Seen: "+ env.documentsSeen());
//          System.out.println("Document Indexed: "+ env.documentsIndexed());
          env.close();
        } catch (Exception e) {
            System.out.println(e.toString());

        }
        
          
        
    }
}
