package Indexing;
import Classes.Path;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...

	//use this map / arrayList to record docno and docid
	private Map<String, Integer> docNoId;
	private List<String> docFreList;
	//use this map to record term docid and frequence
	private Map<String, String> dic;
	private File dicFile;
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		docNoId = new HashMap<>();
		docFreList = new ArrayList<>();
		dic = new HashMap<>();
		File docFreFile;
		if(type.equals("trecweb")) {
			dicFile = new File(Path.IndexWebDir + "indexweb.txt");
			docFreFile = new File("data//webdocfrequency.txt");
		} else {
			dicFile = new File(Path.IndexTextDir + "indextext.txt");
			docFreFile = new File("data//textdocfrequency.txt");
		}
		//got docno <-> docid
		FileReader fr = new FileReader(docFreFile);
		BufferedReader br = new BufferedReader(fr);
		String docNo = br.readLine();
		int id = 1;
		while (docNo != null) {
			docNoId.put(docNo, id++);
			docFreList.add(docNo);
			docNo = br.readLine();
		}
		fr.close();
		br.close();
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		if (docNoId.containsKey(docno)) {
			return docNoId.get(docno);
		}
		return -1;
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		if (docid>0 && docid<=docFreList.size()) {
			return docFreList.get(docid-1);
		}
		return null;
	}
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 *
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		if (!dic.containsKey(token)) return null;
		String res = dic.get(token);
		String[] resArray = res.split(" ");
		int[][] result = new int[resArray.length / 2][2];
		for (int i=0; i<resArray.length; i++) {
			if (i%2==0) result[i/2][0] = Integer.parseInt(resArray[i]);
			else result[i/2][1] = Integer.parseInt(resArray[i]);
		}
		return result;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		int result = 0;
		if(!dic.containsKey(token)) dicMap();
		if(!dic.containsKey(token)) return result;
		String[] docFreString = dic.get(token).split(" ");
		result = docFreString.length / 2;
		return result;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		long result = 0;
		if(!dic.containsKey(token)) dicMap();
		if(!dic.containsKey(token)) return result;
		String[] docFreString = dic.get(token).split(" ");
		for (int i = 1; i < docFreString.length; i+=2) {
			result += Integer.parseInt(docFreString[i]);
		}
		return result;
	}

	private void dicMap() throws IOException {
		FileReader fr = new FileReader(dicFile);
		BufferedReader br = new BufferedReader(fr);
		String termAndDocfre = br.readLine();
		while (termAndDocfre != null) {
			String[] s = termAndDocfre.split(" ");
			String term = s[0];
			termAndDocfre = termAndDocfre.substring(term.length()+1,
							termAndDocfre.length());
			dic.put(term, dic.getOrDefault(term,"") + termAndDocfre);
			termAndDocfre = br.readLine();
		}
		fr.close();
		br.close();
	}
	
	public void Close() throws IOException {
		dic=null;
		docFreList=null;
		docNoId=null;
	}
}