package PreProcessData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 */
public class TrectextCollection implements DocumentCollection {
  // Essential private methods or variables can be added.
  private File trecFile;
  //	private static final String TEXT_START = "<DOC>";
  private static final String TEXT_END = "</DOC>";
  private static final String TEXT_NUM_START = "<DOCNO>";
  //	private static final String TEXT_NUM_END = "</DOCNO>";
  private static final String TEXT_CONTENT_START = "<TEXT>";
  private static final String TEXT_CONTENT_END = "</TEXT>";
  private BufferedReader inputStream;

  // YOU SHOULD IMPLEMENT THIS METHOD.
  public TrectextCollection() throws IOException {
    // 1. Open the file in Path.DataTextDir.
    // 2. Make preparation for function nextDocument().
    // NT: you cannot load the whole corpus into memory!!
    trecFile = new File(Path.DataTextDir);
    inputStream = null;
    try
    {
      inputStream = new BufferedReader(new FileReader(trecFile));
    }catch(FileNotFoundException e) {
      System.out.println("File was not Found!");
    }
  }

  // YOU SHOULD IMPLEMENT THIS METHOD.
  public Map<String, Object> nextDocument() throws IOException {
    // 1. When called, this API processes one document from corpus, and returns its doc number and content.
    // 2. When no document left, return null, and close the file.
    Map<String, Object> map = new HashMap<String, Object>();
    String number = null;
    StringBuffer content = new StringBuffer();
    String line = "", line2 = "";
    line = inputStream.readLine();
    while(line!=null && !line2.equals(TEXT_CONTENT_END)) {
      line = inputStream.readLine();
      String[] lineArray = line.split(" ");
      if(lineArray[0].equals(TEXT_NUM_START)) { //read number
        number = lineArray[1];
      }else if(lineArray[0].equals(TEXT_CONTENT_START)) { //read content
        while(line2!=null) {
          line2 = inputStream.readLine();
//          System.out.println(line2);
          if(!line2.equals(TEXT_CONTENT_END)) content.append(line2+" ");
          else break;
        }
      }
    }
    if(line!=null) {
      if(number!=null && content!=null) {
        map.put(number, content.toString().toCharArray());
      }
      while(line!=null && !line.equals(TEXT_END)) {
        line = inputStream.readLine();
      }
      // NTT: remember to close the file that you opened, when you do not use it any more
      return map;
    }
    // when no document left, return null
    inputStream.close();
    return null;
  }
//  public static void main(String[] args) throws IOException {
//    DocumentCollection corpus = new TrectextCollection();
//    long startTime=System.currentTimeMillis(); //star time of running code
//    corpus.nextDocument();
//    corpus.nextDocument();
//
//    long endTime=System.currentTimeMillis();
//    System.out.println("running time: "+(endTime-startTime)/1000.0+" sec");
//    System.out.println((corpus.nextDocument()==null));
//  }
}
