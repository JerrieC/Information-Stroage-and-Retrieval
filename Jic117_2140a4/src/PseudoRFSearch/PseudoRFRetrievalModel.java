package PseudoRFSearch;

import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

  MyIndexReader ixreader;
  List<Document> docList;
  String[] tokens;
  Set<Integer> docIdSet;

  public PseudoRFRetrievalModel(MyIndexReader ixreader) {
    this.ixreader = ixreader;
  }
  /**
   * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4.
   * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
   *
   * @param aQuery The query to be searched for.
   * @param TopN   The maximum number of returned document
   * @param TopK   The count of feedback documents
   * @param alpha  parameter of relevance feedback model
   * @return TopN most relevant document, in List structure
   */
  public List<Document> RetrieveQuery(Query aQuery, int TopN, int TopK, double alpha) throws Exception {
    // this method will return the retrieval result of the given Query, and
    // this result is enhanced with pseudo relevance feedback

    // (1) use the original retrieval model to get TopK documents,
    // which will be regarded as feedback documents
    QueryRetrievalModel qrModel = new QueryRetrievalModel(ixreader);
    docList = qrModel.retrieveQuery(aQuery, TopK);

    // (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
    // get P(token|feedback documents)
    tokens = aQuery.GetQueryContent().split(" ");
    docIdSet = new HashSet<>();
    HashMap<String, Double> tokenRFScore = GetTokenRFScore(aQuery, TopK);

    // loading all information we need in tokenDocFre map
    Map<String, Map<Integer, Integer>> tokenDocFre = new HashMap<>();
    for (String token : tokens) {
      // if a token has never appeared in the whole collection, remove it
      if (!tokenRFScore.containsKey(token)) continue;
      // if this token has been processed
      if (tokenDocFre.containsKey(token)) continue;
      Map<Integer, Integer> docFre = new HashMap<>();
      int[][] postingList = ixreader.getPostingList(token);
      for (int[] row : postingList) {
        int docId = row[0];
        int docF = row[1];
        // if the doc is not in document list
        if (!docIdSet.contains(docId)) continue;
        docFre.put(docId, docF);
      }
      tokenDocFre.put(token, docFre);
    }

    /**/
//    System.out.println("tokenDocFre size: "+ tokenDocFre.size());
//    for (Map<Integer, Integer> map : tokenDocFre.values()) {
//      System.out.println(map.size());
//    }
    /**/

    Comparator<Document> comparator = new Comparator<Document>() {
      @Override
      public int compare(Document o1, Document o2) {
        if (o1.score() < o2.score()) return -1;
        if (o1.score() > o2.score()) return 1;
        return 0;
      }
    };
    // ues MIN-HEAP data structure to implement TopN-sized heap for highest
    // scores
    PriorityQueue<Document> topNQueue = new PriorityQueue<>(TopN, comparator);

    // (3) implement the relevance feedback model for each token: combine the
    // each query token's original retrieval score P(token|document) with its
    // score in feedback documents P(token|feedback model)

    // (4) for each document, use the query likelihood language model to get
    // the whole query's new score
    // P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')

    for (Integer docId : docIdSet) {
      double score = 1.0;
      for (String token : tokens) {
        if (!tokenRFScore.containsKey(token)) continue;
        double tokenDoc;
        if (!tokenDocFre.get(token).containsKey(docId)) tokenDoc = 0.0;
        else tokenDoc = (double) tokenDocFre.get(token).get(docId);
        double tokenDocPro = tokenDoc / (double) ixreader.docLength(docId);
        double tokenFBPro = tokenRFScore.get(token);
        double tokenScore = alpha * tokenDocPro + (1.0 - alpha) * tokenFBPro;
        score *= tokenScore;
      }
      if (topNQueue.size() < TopN) {
        topNQueue.add(new Document(Integer.toString(docId), ixreader
                .getDocno(docId), score));
      } else if (score > topNQueue.peek().score()) {
        topNQueue.poll();
        topNQueue.add(new Document(Integer.toString(docId), ixreader
                .getDocno(docId), score));
      }
    }
    // sort all retrieved documents from most relevant to least, and return TopN
    List<Document> results = new ArrayList<Document>();
    // add the top N documents into the result list
    while (!topNQueue.isEmpty()) {
      results.add(0, topNQueue.poll());
    }
    return results;
  }

  public HashMap<String, Double> GetTokenRFScore(Query aQuery, int TopK) throws Exception {
    int totalDocLength = 142062976, numberOfDoc = 503473;
    double miu = (double) totalDocLength / (double) numberOfDoc;
    // for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
    // use Dirichlet smoothing
    // save <token, score> in HashMap TokenRFScore, and return it
    HashMap<String, Double> tokenRFScore = new HashMap<String, Double>();
    // document length = total length of top K documents
    int docLen = 0;
    for (Document doc : docList) {
      int docId = Integer.parseInt(doc.docid());
      docIdSet.add(docId);
      docLen += ixreader.docLength(docId);
    }

    for (String token : tokens) {
      //System.out.println("handling token "+t);
      // if a token has never appeared in the whole collection, remove it
      long termTotalFre = ixreader.CollectionFreq(token);
      if (termTotalFre == 0) continue;
      int[][] postingList = ixreader.getPostingList(token);
      // document frequency
      int termDocFre = 0;
      for (int[] row : postingList) {
        int docId = row[0];
        if (docIdSet.contains(docId)) termDocFre += row[1];
      }
      // Dirichlet smoothing: consider the topK documents as one
      // document in the whole collection
      double up = (double) termDocFre + miu * (double) termTotalFre /
              (double) totalDocLength;
      double down = (double) docLen + miu;
      double score = up / down;
      tokenRFScore.put(token, score);
    }
    return tokenRFScore;
  }
}