package Indexing;
import Classes.Path;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your
	// memory cannot hold our corpus...
	private File indexFile;
	//Use a map to save index. The format is: term / docid / freq in this doc
	private Map<String, TreeMap<Integer, Integer>> dic;
	private FileWriter fw;
	private FileWriter fwDoc;
	private int countDoc;

	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		if(type.equals("trecweb")) {
			indexFile = new File(Path.IndexWebDir + "indexweb.txt");
			fwDoc = new FileWriter(new File("data//webdocfrequency.txt"));
		} else {
			indexFile = new File(Path.IndexTextDir + "indextext.txt");
			fwDoc = new FileWriter(new File("data//textdocfrequency.txt"));
		}
		dic = new HashMap<>();
		fw = new FileWriter(indexFile);
		//put all docno into one list, the index of this list is docid
		//count docid from 1 to N
		countDoc = 1;
	}

	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		fwDoc.write(docno + "\n");
		String[] contentArray = content.split(" ");
		for (String term : contentArray) {
			TreeMap<Integer, Integer> defaultDocFre = new TreeMap<>();
			defaultDocFre.put(countDoc, 0);
			TreeMap<Integer, Integer> docFre = dic.getOrDefault(term,
							defaultDocFre);
			docFre.put(countDoc, docFre.getOrDefault(countDoc, 0)+1);
			dic.put(term, docFre);
		}
		if (countDoc % 10000 == 0) {
			fileWriter();
			dic.clear();
		}
		countDoc++;
	}

	private void fileWriter() throws IOException{
		for (String term : dic.keySet()) {
			fw.write(term + " ");
			Map<Integer, Integer> docFre = dic.get(term);
			for (Integer id : docFre.keySet()) {
				fw.write(id + " " + docFre.get(id) + " ");
			}
			fw.write("\n");
		}
	}

	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		fileWriter();
		fw.close();
		fwDoc.close();
	}
}
