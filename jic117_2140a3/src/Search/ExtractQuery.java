package Search;

import Classes.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExtractQuery {
  private File toptics;
  private BufferedReader inputStream;
  private List<Query> queryList;
  private int currentIndex;

  public ExtractQuery() throws IOException {
    //you should extract the 4 queries from the Path.TopicDir
    //NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
    //NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
    toptics = new File(Path.TopicDir);
    inputStream = null;
    queryList = new ArrayList<>();
    currentIndex = 0;
    // read query file
    try {
      inputStream = new BufferedReader(new FileReader(toptics));
    } catch (FileNotFoundException e) {
      System.out.println("File was not Found!");
    }
    Query query = new Query();
    String content = "";
    String line = inputStream.readLine();
    while(line!=null) {
      // set topic id for this query
      if (line.length()>=14 && line.substring(0, 14).equals("<num> Number: ")) {
        query.SetTopicId(line.substring(14, line.length()));
      }
      // set content for this query
      if (line.length()>=7 && line.substring(0, 7).equals("<title>")) {
        content += preprocess(line.substring(7, line.length()));
        query.SetQueryContent(content);
        queryList.add(query);
        query = new Query();
        content = "";
      }
//      if (line.length()>=6 && line.substring(0, 6).equals("<desc>")) {
//        // skip the line of "<desc> Description: "
//        line = inputStream.readLine();
//        while (!(line.length()>=6 && line.substring(0, 6).equals("<narr>"))) {
//          content += preprocess(line);
//          line = inputStream.readLine();
//        }
//      }
//      if (line.length()>=6 && line.substring(0, 6).equals("<narr>")) {
//        // skip the line of "<narr> Narrative: "
//        line = inputStream.readLine();
//        while (!line.equals("</top>")) {
//          content += preprocess(line);
//          line = inputStream.readLine();
//        }
//        query.SetQueryContent(content);
//        queryList.add(query);
//        query = new Query();
//        content = "";
//      }
      line = inputStream.readLine();
    }
    inputStream.close();
  }

  public boolean hasNext() {
    if(currentIndex<queryList.size()) return true;
    return false;
  }

  public Query next() {
    return queryList.get(currentIndex++);
  }

  public String preprocess(String line) {
    String result = "";
    if(line==null || line.length()==0) return result;
    // tokenize, normalize, remove stop words, stemming
    // Loading stopword, and initiate StopWordRemover.
    StopWordRemover stopwordRemover = new StopWordRemover();

    // Initiate WordNormalizer.
    WordNormalizer normalizer = new WordNormalizer();

    // Initiate the WordTokenizer class.
    WordTokenizer wordTokenizer = new WordTokenizer(line.toCharArray());

    // Initiate a word object
    char[] word = null;

    // Process the document word iteratively
    while ((word = wordTokenizer.nextWord()) != null) {
      // Each word is transformed into lowercase.
      word = normalizer.lowercase(word);
      // Only non-stopword will appear in result life
      if (!stopwordRemover.isStopword(word)) {
        // Words are stemmed.
        result += normalizer.stem(word) + " ";
      }
    }
    return result;
  }
//  public static void main(String[] args) throws IOException{
//    ExtractQuery trytry = new ExtractQuery() ;
//    while (trytry.hasNext()) {
//      Query query = trytry.next();
//      System.out.println(query.GetTopicId());
//      System.out.println(query.GetQueryContent());
//    }
//  }
}