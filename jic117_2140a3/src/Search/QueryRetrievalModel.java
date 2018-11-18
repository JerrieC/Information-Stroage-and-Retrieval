package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {

  protected MyIndexReader indexReader;
  private double coefficient1, coefficient2;
  // total document length: how many words are there in the total collection
  private int totalDocLength;
  private final int numberOfDoc = 503473;
  double miu;

  public QueryRetrievalModel(MyIndexReader ixreader) throws IOException{
    indexReader = ixreader;
    totalDocLength = 0;
    for (int i = 0; i < numberOfDoc; i++) {
      totalDocLength += indexReader.docLength(i);
    }
    // let miu as the average doc length of this collection
    miu = (double) totalDocLength / (double) numberOfDoc;
    // compute the coefficient:  miu + |D|
    coefficient1 = miu + (double) totalDocLength;
    // compute another coefficient: miu / (|D| + miu)
    coefficient2 = miu / coefficient1;
  }

  /**
   * Search for the topic information.
   * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
   * TopN specifies the maximum number of results to be returned.
   *
   * @param aQuery The query to be searched for.
   * @param TopN   The maximum number of returned document
   * @return
   */
  public List<Document> retrieveQuery(Query aQuery, int TopN) throws IOException {
    // NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
    // implement your retrieval model here, and for each input query, return the topN retrieved documents
    // sort the documents based on their relevance score, from high to low
    String[] queryContent = aQuery.GetQueryContent().split(" ");

    // build this map to record <String: term, Integer: collection frequency>
    Map<String, Long> termTotalFre = new HashMap<>();

    // build this map to record <Integer: docid, <String: term, Integer: docFre>>
    Map<Integer, Map<String, Integer>> docInfo = new HashMap<>();

    // add each term and it's collection frequency into termTotalFre map
    for (String term : queryContent) {
      // if this term has been processed
      // or this term has never appeared in total collection
      // skip this term
      if (termTotalFre.containsKey(term) ||
              indexReader.CollectionFreq(term) == 0) continue;
      termTotalFre.put(term, indexReader.CollectionFreq(term));
      int[][] postingList = indexReader.getPostingList(term);
      // save info into the map of docInfo
      for (int[] docFre : postingList) {
        int docId = docFre[0];
        int termDocFre = docFre[1];
        if (docInfo.containsKey(docId)) {
          Map<String, Integer> docFreMap = docInfo.get(docId);
          docFreMap.put(term, termDocFre);
        } else {
          Map<String, Integer> docFreMap = new HashMap<>();
          docFreMap.put(term, termDocFre);
          docInfo.put(docId, docFreMap);
        }
      }
    }

    Comparator<Document> comparator = new Comparator<Document>() {
      @Override
      public int compare(Document o1, Document o2) {
        if (o1.score()<o2.score()) return -1;
        if (o1.score()>o2.score()) return 1;
        return 0;
      }
    };
    // ues MIN-HEAP data structure to implement TopN-sized heap for highest
    // scores
    PriorityQueue<Document> topNHeap = new PriorityQueue<>(TopN, comparator);

    for (Integer docId : docInfo.keySet()) {
      Map<String, Integer> docFreMap = docInfo.get(docId);
      // initiate this score
      double score = 1;
      // iterate every word in the query content
      for (String currentTerm : queryContent) {
        // delete terms which are not appear in total collection
        // and terms which are not appear in current document
        if (!termTotalFre.containsKey(currentTerm)) continue;
        // calculate Dirichlet Prior Smoothing function score
        double up = (double)docFreMap.getOrDefault(currentTerm,0) +
                miu * termTotalFre.get(currentTerm)/ totalDocLength;
        double down = (double) indexReader.docLength(docId) + miu;
        // unigram: suppose each term are independent with each other
        score *= up / down;
      }
      if (topNHeap.size() < TopN) {
        Document document = new Document(Integer.toString(docId), indexReader
                .getDocno(docId), score);
        topNHeap.add(document);
      } else if (score > topNHeap.peek().score()){
        topNHeap.poll();
        Document document = new Document(Integer.toString(docId), indexReader
                .getDocno(docId), score);
        topNHeap.add(document);
      }
    }

    // add topN documents to the result list
    List<Document> result = new ArrayList<>();
    for (int i=0; i<TopN; i++) {
      result.add(0, topNHeap.poll());
    }
    return result;
  }
}
